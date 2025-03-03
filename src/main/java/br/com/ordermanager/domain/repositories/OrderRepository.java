package br.com.ordermanager.domain.repositories;

import br.com.ordermanager.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    boolean existsByExternalOrderId(UUID externalOrderId);

    Order findByExternalOrderId(UUID externalOrderId);
}