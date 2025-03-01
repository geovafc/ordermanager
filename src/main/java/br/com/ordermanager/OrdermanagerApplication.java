package br.com.ordermanager;

import br.com.ordermanager.dtos.requests.OrderItemRequestDTO;
import br.com.ordermanager.dtos.requests.OrderRequestDTO;
import br.com.ordermanager.services.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class OrdermanagerApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(OrdermanagerApplication.class, args);
//	}

		public static void main(String[] args) {
			ConfigurableApplicationContext context = SpringApplication.run(OrdermanagerApplication.class, args);
			OrderService orderService = context.getBean(OrderService.class);

			// Agora você pode usar o objeto meuServico para chamar os métodos da classe de serviço
			var orderRequestDTO  = new OrderRequestDTO(UUID.randomUUID(), createOrderItemRequestDTOList());
			orderService.processOrder(orderRequestDTO);

			// Se necessário, feche o contexto da aplicação
//			context.close();

		}

	public static List<OrderItemRequestDTO> createOrderItemRequestDTOList() {
		return Arrays.asList(
				new OrderItemRequestDTO("Laptop", 1, new BigDecimal("1200.00")),
				new OrderItemRequestDTO("Mouse", 2, new BigDecimal("25.50")),
				new OrderItemRequestDTO("Keyboard", 1, new BigDecimal("75.99")),
				new OrderItemRequestDTO("Monitor", 1, new BigDecimal("350.00")),
				new OrderItemRequestDTO("Headphones", 3, new BigDecimal("49.95"))
		);
	}
}


