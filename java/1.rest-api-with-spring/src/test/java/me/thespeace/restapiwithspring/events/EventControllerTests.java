package me.thespeace.restapiwithspring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;

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
        Event event = Event.builder()
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
        event.setId(10);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))) // ObjectMapper를 사용하여 json으로 변환.
                .andDo(print()) // 요청과 응답 확인 가능.
                .andExpect(status().isCreated()) //201
                .andExpect(jsonPath("id").exists()) // id 존재 여부 확인.
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,"application/hal+json;charset=UTF-8"))
        ;
    }

}
