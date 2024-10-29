package me.thespeace.restapiwithspring.events;

import lombok.*;

import java.time.LocalDateTime;

/**
 * <h1>Event 도메인 구현</h1>
 * <p>@Data를 쓰지 않는 이유는 equals()와 hashCode()도 같이 구현해주는데
 * 모든 property를 다 써서 구현하기 때문에 entity에다가 Data 에너테이션을 쓰면 안된다.
 * 상호 참조로 인해 stack overflow가 발생할 여지가 있다.</p>
 */
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    private EventStatus eventStatus;

}
