package br.com.ordermanager.repositories;

import br.com.ordermanager.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    boolean existsByExternalOrderId(UUID externalOrderId);
}