package br.com.ordermanager;

import br.com.ordermanager.infrastructure.kafka.event.OrderItemEvent;
import br.com.ordermanager.infrastructure.kafka.event.OrderEvent;
import br.com.ordermanager.application.services.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class OrdermanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdermanagerApplication.class, args);
	}

//		public static void main(String[] args) {
//			ConfigurableApplicationContext context = SpringApplication.run(OrdermanagerApplication.class, args);
//			OrderService orderService = context.getBean(OrderService.class);
//
//			// Agora você pode usar o objeto meuServico para chamar os métodos da classe de serviço
//			var orderRequestDTO  = new OrderEvent(UUID.randomUUID(), createOrderItemRequestDTOList());
//			orderService.processOrder(orderRequestDTO);
//
//			// Se necessário, feche o contexto da aplicação
////			context.close();
//
//		}
//
//	public static List<OrderItemEvent> createOrderItemRequestDTOList() {
//		return Arrays.asList(
//				new OrderItemEvent("Laptop", 1, new BigDecimal("1200.00")),
//				new OrderItemEvent("Mouse", 2, new BigDecimal("25.50")),
//				new OrderItemEvent("Keyboard", 1, new BigDecimal("75.99")),
//				new OrderItemEvent("Monitor", 1, new BigDecimal("350.00")),
//				new OrderItemEvent("Headphones", 3, new BigDecimal("49.95"))
//		);
//	}
}


