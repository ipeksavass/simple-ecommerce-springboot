package com.ipeksavas.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetAllOrdersResponse {
	private List <OrderDto> orders;
	
	@Setter
	@Getter
	public static class OrderDto{//The static keyword is used to ensure the independence of the inner class.
		//I defined a dto for the general information of the order.
		private Long orderId;
		private BigDecimal totalPrice;
		private String createdAt;
		private List<OrderItemDto> items;
	}
	
	@Setter
	@Getter
	public static class OrderItemDto{
		//I defined a dto to hold the information for one product of the order.
		private String productName;
		private BigDecimal priceAtPurchase;
		private int quantity;
	}
}
