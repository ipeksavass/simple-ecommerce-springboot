package com.ipeksavas.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.ipeksavas.model.OrderItem;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class GetOrderResponse {

	private BigDecimal totalPrice;
	private List<OrderItemDto> items;
	
	@Setter
	@Getter
	public static class OrderItemDto{
		private String name;
		private int quantity;
		private BigDecimal priceAtPurchase;
		
	}
}
