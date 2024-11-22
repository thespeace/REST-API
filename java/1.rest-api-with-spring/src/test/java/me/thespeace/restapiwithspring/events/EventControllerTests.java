package me.thespeace.restapiwithspring.events;

import me.thespeace.restapiwithspring.accounts.Account;
import me.thespeace.restapiwithspring.accounts.AccountRepository;
import me.thespeace.restapiwithspring.accounts.AccountRole;
import me.thespeace.restapiwithspring.accounts.AccountService;
import me.thespeace.restapiwithspring.common.AppProperties;
import me.thespeace.restapiwithspring.common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <h1>스프링 부트 슬라이스 테스트</h1>
 * <ul>@WebMvcTest
 *     <li>MockMvc 빈을 자동 설정, 따라서 가져와서 쓰면 된다.</li>
 *     <li>웹 관련 빈만 등록해 준다.(슬라이스)</li>
 * </ul>
 * <ul>MockMvc
 *     <li>스프링 MVC 테스트 핵심 클래스</li>
 *     <li>웹 서버를 띄우지 않고도 스프링 MVC(DispatcherServlet)가 요청을
 *     처리하는 과정을 확인할 수 있기 때문에 컨트롤러 테스트용으로 자주 쓰인다.</li>
 * </ul>
 * <p>MockMvc는 웹서버를 띄우지 않기 때문에 단위 테스트보다는 빠르지만
 * 디스패처 서블릿까지 만들어야 되기 때문에 그렇다고 해서 단위 테스트보다 빠르지는 않다.</p>
 * <p>웹서버까지 띄우는 테스트보다는 조금 덜 걸리지만 단위 테스트보다는 조금 더 걸리는 테스트.</p>
 *
 * <br><br>
 * <br><br>
 *
 * <h1>통합 테스트로 전환</h1>
 * <p>DTO를 적용함으로써 실제 객체는 컨트롤러의 메서드안에서 새로 만들어지게 되어서
 * 더이상 Mocking이 불가능해졌다. 따라서 통합테스트로 전환하자.</p>
 * <ul>
 *     <li>@WebMvcTest 빼고 다음 애노테이션 추가
 *         <ul>
 *             <li>@SpringBootTest</li>
 *             <li>@AutoConfigureMockMvc</li>
 *         </ul>
 *     </li>
 *     <li>Repository @MockBean 코드 제거</li>
 * </ul>
 */
public class EventControllerTests extends BaseTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @BeforeEach
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    /**
     * <h2>event 생성 테스트: 201 응답 받기</h2>
     * <ul>테스트 할 것
     *     <li>입력값들을 전달하면 JSON 응답으로 201이 나오는 지 확인.</li>
     *     <li>Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는 지 확인.</li>
     *     <li>id는 DB에 들어갈 때 자동 생성된 값으로 나오는 지 확인.</li>
     * </ul>
     */
    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                        .name("thespeace")
                        .description("REST API Development with Spring")
                        .beginEnrollmentDateTime(LocalDateTime.of(2024,10,30,12,30))
                        .closeEnrollmentDateTime(LocalDateTime.of(2024,10,31,12,30))
                        .beginEventDateTime(LocalDateTime.of(2024,11,1,12,30))
                        .endEventDateTime(LocalDateTime.of(2024,11,2,12,30))
                        .basePrice(100)
                        .maxPrice(200)
                        .limitOfEnrollment(100)
                        .location("강남역")
                        .build();

        mockMvc.perform(post("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))) // ObjectMapper를 사용하여 json으로 변환.
                .andDo(print()) // 요청과 응답 확인 가능.
                .andExpect(status().isCreated()) //201
                .andExpect(jsonPath("id").exists()) // id 존재 여부 확인.
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists()) // view
                .andExpect(jsonPath("_links.query-events").exists()) // 목록으로 가는 링크
                .andExpect(jsonPath("_links.update-event").exists()) // update 링크
                .andDo(document("create-event",
                        //snippets 추가
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new evnet"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields( //Relaxed 접두어는 문서 일부분만 테스트 할 수 있다는 장점과 그렇기 때문에 정확한 문서를 생성하지 못한다는 단점이 공존한다. 사용하지 않는게 좋다.
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new evnet"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells is this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
        ;
    }

    private String getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer " + getAccessToken(needToCreateAccount);
    }

    private String getAccessToken(boolean needToCreateAccount) throws Exception {
        // Given
        if (needToCreateAccount) {
            createAccount();
        }

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    private Account createAccount() {
        Account user = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        return this.accountService.saveAccount(user);
    }

    /**
     * <h2>입력값 이외에 에러 테스트: 400 상태 코드 응답 확인</h2>
     * <p>createEvent 테스트가 잘못된 필드를 무시했다면, 이 테스트는
     * 유효하지 않은 필드로 에러가 발생하는지를 확인(unknown properties).</p>
     * <br><br>
     * <h2>테스트 할 것</h2>
     * <ul>
     *     <li>입력값으로 누가 id나 eventStatus, offline, free 이런 데이터까지 같이 주면?</li>
     *     <ul><li>Bad_Request로 응답 vs 받기로 한 값 이외는 무시</li></ul>
     * </ul>
     */
    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                        .id(100)
                        .name("thespeace")
                        .description("REST API Development with Spring")
                        .beginEnrollmentDateTime(LocalDateTime.of(2024,10,30,12,30))
                        .closeEnrollmentDateTime(LocalDateTime.of(2024,10,31,12,30))
                        .beginEventDateTime(LocalDateTime.of(2024,11,1,12,30))
                        .endEventDateTime(LocalDateTime.of(2024,11,2,12,30))
                        .basePrice(100)
                        .maxPrice(200)
                        .limitOfEnrollment(100)
                        .location("강남역")
                        .free(true)
                        .offline(false)
                        .eventStatus(EventStatus.PUBLISHED)
                        .build();

        mockMvc.perform(post("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
        ;
    }

    /**
     * <h2>EventDto 유효성 검증이 작동하는지 확인</h2>
     * <p>객체 필드에 아무런 값도 채워지지 않은 상태로 전송되었을 때 400 상태 코드 응답 확인</p>
     */
    @Test
    @DisplayName("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * <h2>EventDto 유효성 검증이 작동하는지 확인</h2>
     * <p>잘못된 데이터가 들어왔을 경우 400 상태 코드 응답 확인</p>
     * <p>annotation으로 검증이 어려우니 검증기(EvnetValidator) 사용</p>
     */
    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("thespeace")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2024,10,30,12,30))
                .closeEnrollmentDateTime(LocalDateTime.of(2024,10,31,12,30))
                .beginEventDateTime(LocalDateTime.of(2024,11,3,12,30))
                .endEventDateTime(LocalDateTime.of(2024,11,2,12,30))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists()) // 에러 응답 메시지에 에러에 대한 정보(errors) 추가.
//                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
//                .andExpect(jsonPath("$[0].rejectedValue").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    /**
     * <h2>Event 목록 조회 API</h2>
     * <p>page: 0부터 시작</p>
     * <p>size: 기본값 20</p>
     * <p>sort: property.property(ASC|DESC)</p>
     */
    @Test
    @DisplayName("사용자 인증 전, 30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When & Then
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort","name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events")) //TODO page 문서화
        ;
    }

    @Test
    @DisplayName("사용자 인증 후, 30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When & Then
        this.mockMvc.perform(get("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort","name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events")) //TODO page 문서화
        ;
    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        // Given
        Account account = this.createAccount();
        Event event = this.generateEvent(100, account);

        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event")) //TODO 문서화
        ;
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답 받기")
    public void getEvent404() throws Exception {
        // When & Then
        this.mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception{
        // Given
        Account account = this.createAccount();
        Event event = this.generateEvent(200, account);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event")) //TODO 문서화
        ;
    }

    @Test
    @DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEvent400_Empty() throws Exception{
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400_Wrong() throws Exception{
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404() throws Exception{
        // Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        this.mockMvc.perform(put("/api/events/123123")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Event generateEvent(int index, Account account) {
        Event event = buildEvent(index);
        event.setManager(account);
        return this.eventRepository.save(event);
    }
    
    private Event generateEvent(int index) {
        Event event = buildEvent(index);
        return this.eventRepository.save(event);
    }

    private static Event buildEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2024,10,30,12,30))
                .closeEnrollmentDateTime(LocalDateTime.of(2024,10,31,12,30))
                .beginEventDateTime(LocalDateTime.of(2024,11,1,12,30))
                .endEventDateTime(LocalDateTime.of(2024,11,2,12,30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
        return event;
    }


}
