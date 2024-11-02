package me.thespeace.restapiwithspring.events;

import org.junit.jupiter.api.Test;

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
     */
    @Test
    public void testFree() {
        // Given(무료일 경우)
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isTrue();

        // Given(유료인 경우, basePrice 값 존재)
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isFalse();

        // Given(유료인 경우, maxPrice 값 존재)
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isFalse();
    }

    /**
     * <h2>장소 여부 단위 테스트</h2>
     */
    @Test
    public void testOffline() {
        // Given(장소가 있는 경우)
        Event event = Event.builder()
                .location("강남역")
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isTrue();

        // Given(장소가 없는 경우)
        event = Event.builder()
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isFalse();
    }
}