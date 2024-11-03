package me.thespeace.restapiwithspring.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    /**
     * <h2>Event 도메인 빌더 확인</h2>
     */
    @Test
    public void builder() {
        Event event = Event.builder()
                .name("thespeace")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    /**
     * <h2>Event 도메인 디폴트 생성자 확인</h2>
     * <p>Bean 스펙도 준수해야 한다.</p>
     */
    @Test
    public void javaBean() {
        // Given
        String name = "thespeace";
        String description = "Spring";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    /**
     * <h2>무료 여부 단위 테스트</h2>
     * <p>junit-params를 사용해서 중복 제거</p>
     */
    @ParameterizedTest
    @MethodSource("paramsForTestFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private static Stream<Arguments> paramsForTestFree() { // argument source method
        return Stream.of(
                Arguments.of(0,0, true),
                Arguments.of(100, 0, false),
                Arguments.of(0, 100, false),
                Arguments.of(100, 200, false)
        );
    }

    /**
     * <h2>장소 여부 단위 테스트</h2>
     */
    @ParameterizedTest
    @MethodSource("paramsForTestOffline")
    public void testOffline(String location, boolean isOffline) {
        // Given(장소가 있는 경우)
        Event event = Event.builder()
                .location(location)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private static Stream<Arguments> paramsForTestOffline() { // argument source method
        return Stream.of(
                Arguments.of("강남역", true),
                Arguments.of(null, false),
                Arguments.of("        ", false)
        );
    }
}