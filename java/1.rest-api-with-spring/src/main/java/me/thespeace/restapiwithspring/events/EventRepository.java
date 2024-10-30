package me.thespeace.restapiwithspring.events;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>Spring Data JPA Repository</h1>
 */
public interface EventRepository extends JpaRepository<Event, Integer> {
}
