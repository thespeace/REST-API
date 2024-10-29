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
}