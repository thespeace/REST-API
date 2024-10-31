package me.thespeace.restapiwithspring.events;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <h1>입력값 제한하기</h1>
 * <ul>
 *     <li>id 또는 입력 받은 데이터로 계산해야 하는 값들은 입력을 받지 않아야 한다.</li>
 *     <li>애너테이션 분산을 위해 입력받는 Dto를 사용해서 분리한다.</li>
 *     <li>하지만 중복이 생긴다는 단점이 생긴다.</li>
 * </ul>
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private LocalDateTime beginEnrollmentDateTime;
    @NotNull
    private LocalDateTime closeEnrollmentDateTime;
    @NotNull
    private LocalDateTime beginEventDateTime;
    @NotNull
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    @Min(0)
    private int basePrice; // (optional)
    @Min(0)
    private int maxPrice; // (optional)
    @Min(0)
    private int limitOfEnrollment;

}
