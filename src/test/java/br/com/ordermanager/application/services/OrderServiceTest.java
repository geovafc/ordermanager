package br.com.ordermanager.application.services;

import br.com.ordermanager.application.mappers.OrderMapper;
import br.com.ordermanager.domain.entities.Order;
import br.com.ordermanager.domain.entities.Outbox;
import br.com.ordermanager.domain.repositories.OrderRepository;
import br.com.ordermanager.domain.repositories.OutboxRepository;
import br.com.ordermanager.infrastructure.kafka.events.OrderEvent;
import br.com.ordermanager.infrastructure.kafka.events.OrderItemEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxRepository outboxRepository;

    private OrderEvent orderEvent;
    private Order order;
    private Outbox outbox;

    @BeforeEach
    void setup() {
        var externalOrderId = UUID.randomUUID();

        orderEvent = new OrderEvent(
                externalOrderId,
                List.of(new OrderItemEvent("Produto A", 2, new BigDecimal("10.00")))
        );

        order = OrderMapper.eventToEntity(orderEvent);

        outbox = Outbox.builder()
                .aggregateId(externalOrderId)
                .payload("{}")
                .build();
    }

    @Test
    void shouldProcessOrderSuccessfullyAndSaveToOutbox() {
        // Given
        when(orderRepository.existsByExternalOrderId(orderEvent.externalOrderId())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(outboxRepository.save(any(Outbox.class))).thenReturn(outbox);

        // When
        orderService.processOrder(orderEvent);

        // Then
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(outboxRepository, times(1)).save(any(Outbox.class));
    }

    @Test
    void shouldCorrectlyCalculateTotalPriceWithMultipleItems() {
        // Given
        when(orderRepository.existsByExternalOrderId(orderEvent.externalOrderId())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        orderService.processOrder(orderEvent);

        var orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        var savedOrder = orderCaptor.getValue();

        // Then
        var expectedTotal = new BigDecimal("20.00");
        assertEquals(expectedTotal, savedOrder.getTotalPrice());
    }

    @Test
    void shouldCalculateCorrectlyTheTotalPriceWithOneItem() {
        // Given
        var singleItemOrder = new OrderEvent(
                UUID.randomUUID(),
                List.of(new OrderItemEvent("Produto X", 1, new BigDecimal("25.00")))
        );

        when(orderRepository.existsByExternalOrderId(singleItemOrder.externalOrderId())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        orderService.processOrder(singleItemOrder);

        var orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        var savedOrder = orderCaptor.getValue();

        // Then
        assertEquals(new BigDecimal("25.00"), savedOrder.getTotalPrice());
    }

    @Test
    void shouldReturnTotalPriceZeroWhenItemListIsEmpty() {
        // Given
        var emptyOrder = new OrderEvent(UUID.randomUUID(), List.of());

        when(orderRepository.existsByExternalOrderId(emptyOrder.externalOrderId())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        orderService.processOrder(emptyOrder);

        var orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        var savedOrder = orderCaptor.getValue();

        // Then
        assertEquals(BigDecimal.ZERO, savedOrder.getTotalPrice());
    }

    @Test
    void shouldNotProcessExistingOrder() {
        // Given
        when(orderRepository.existsByExternalOrderId(orderEvent.externalOrderId())).thenReturn(true);

        // When
        orderService.processOrder(orderEvent);

        // Then
        verify(orderRepository, never()).save(any(Order.class));
        verify(outboxRepository, never()).save(any(Outbox.class));
    }

    @Test
    void shouldThrowExceptionWhenFailingToSaveToOutbox() throws JsonProcessingException {
        // Given
        when(orderRepository.existsByExternalOrderId(orderEvent.externalOrderId())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doThrow(new RuntimeException("Error saving to Outbox")).when(outboxRepository).save(any(Outbox.class));

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.processOrder(orderEvent));
        assertEquals("Error saving to outbox", exception.getMessage());

        // Then
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(outboxRepository, times(1)).save(any(Outbox.class));
    }
}
