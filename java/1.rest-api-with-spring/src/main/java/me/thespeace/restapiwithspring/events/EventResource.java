package me.thespeace.restapiwithspring.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * <h1>스프링 HATEOAS</h1>
 * <ul>
 *     <li>링크 만드는 기능
 *         <ul>
 *             <li>문자열 가지고 만들기</li>
 *             <li>컨트롤러와 메소드로 만들기</li>
 *         </ul>
 *     </li>
 *     <li>리소스 만드는 기능
 *         <ul>
 *             <li>리소스: 데이터 + 링크</li>
 *         </ul>
 *     </li>
 *     <li>링크 찾아주는 기능
 *         <ul>
 *             <li>Traverson</li>
 *             <li>LinkDiscoverers</li>
 *         </ul>
 *     </li>
 *     <li>링크
 *         <ul>
 *             <li>href</li>
 *             <li>rel : self, profile, update-event, query-events</li>
 *         </ul>
 *     </li>
 * </ul>
 * @see <a href="https://docs.spring.io/spring-hateoas/docs/current/reference/html/">spring-hateoas docs</a>
 */
public class EventResource extends EntityModel<Event> { // 스프링 HATEOAS 적용

    public EventResource(Event event, Link... links) {
        super(event, List.of(links));
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }

    /*@JsonUnwrapped
    private Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }*/
}
