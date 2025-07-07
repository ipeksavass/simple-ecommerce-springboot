package com.ipeksavas.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCartResponse {
	private BigDecimal totalPrice;
	private List<CartItemDto> items;
	
	//I wrote it nested because it will only be used in cart item display.
	@Getter
	@Setter
	public static class CartItemDto{
		private String productName;  
		private BigDecimal unitPrice ;
		private int quantity;
	}
}
