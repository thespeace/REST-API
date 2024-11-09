package me.thespeace.restapiwithspring.index;

import me.thespeace.restapiwithspring.events.EventController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * <h1>index</h1>
 * <ul>
 *     <li>다른 리소스에 대한 링크 제공</li>
 *     <li>문서화</li>
 * </ul>
 */
@RestController
public class IndexController {

    @GetMapping("/api")
    public RepresentationModel<?> index() {
        var index = new RepresentationModel<>();
        index.add(linkTo(EventController.class).withRel("events"));
        return index;
    }
}
