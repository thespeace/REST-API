package me.thespeace.restapiwithspring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * <h2>event 생성 테스트: 201 응답 받기</h2>
     * <ul>테스트 할 것
     *     <li>입력값들을 전달하면 JSON 응답으로 201이 나오는 지 확인.</li>
     *     <li>Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는 지 확인.</li>
     *     <li>id는 DB에 들어갈 때 자동 생성된 값으로 나오는 지 확인.</li>
     * </ul>
     */
    @Test
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))) // ObjectMapper를 사용하여 json으로 변환.
                .andDo(print()) // 요청과 응답 확인 가능.
                .andExpect(status().isCreated()) //201
                .andExpect(jsonPath("id").exists()) // id 존재 여부 확인.
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;
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
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }
}
