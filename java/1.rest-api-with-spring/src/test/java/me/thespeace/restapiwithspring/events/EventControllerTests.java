package me.thespeace.restapiwithspring.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void createEvent() throws Exception {
        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isCreated()); //201
    }

}
