package me.thespeace.restapiwithspring.events;

import me.thespeace.restapiwithspring.accounts.Account;
import me.thespeace.restapiwithspring.accounts.AccountAdapter;
import me.thespeace.restapiwithspring.accounts.CurrentUser;
import me.thespeace.restapiwithspring.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = "application/hal+json; charset=UTF-8")
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    /**
     * <h3>ResponseEntity를 사용하는 이유</h3>
     * <p>응답 코드, 헤더, 본문 모두 다루기 편한 API이기 때문</p>
     * <p/>
     *
     * <h3>@Valid와 BindingResult (또는 Errors)</h3>
     * <ul>
     *     <li>BindingResult는 항상 @Valid 바로 다음 인자로 사용해야 함. (스프링 MVC)</li>
     *     <li>@NotNull, @NotEmpty, @Min, @Max, ... 사용해서 입력값 바인딩할 때 에러 확인할 수 있다.</li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors,
                                      @CurrentUser Account currentUser) {
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(currentUser); //manager를 현재 유저로 세팅.
        Event newEvent = this.eventRepository.save(event);

        // HATEOAS 적용
        // 응답에 _links(self, query-events, update-event) 추가
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(newEvent);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
//        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    /**
     * <h1>Event 목록 조회 API</h1>
     * <ul>
     *     <li>Spring Data JPA가 제공하는 Pageable 사용하여 페이징, 정렬.</li>
     *     <li>Page<Event>를 PagedResourceAssembler<T>를 사용하여 EventResource로 맵핑.</li>
     * </ul>
     */
    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler,
                                      @CurrentUser Account account) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        var pagedResources = assembler.toModel(page, EventResource::new);
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        if(account != null) { //db에서 확인하지 않아도 인증된 사용자 정보를 확인할 수 있다.
            pagedResources.add(linkTo(EventController.class).withRel("create-event")); //조회시 추가로 링크 전달.
        }
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if(event.getManager().equals(currentUser)) { //매니저 인 경우에만-
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event")); //update-event 링크 추가 전달.
        }

        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        this.eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        Event existingEvent = optionalEvent.get();
        if(!existingEvent.getManager().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        this.modelMapper.map(eventDto, existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }
}
