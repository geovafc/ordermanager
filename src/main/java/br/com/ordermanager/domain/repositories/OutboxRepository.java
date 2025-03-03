package br.com.ordermanager.domain.repositories;

import br.com.ordermanager.domain.entities.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
    List<Outbox> findByProcessedFalse();
}