package com.ipeksavas.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.ipeksavas.model.CartItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCartResponse {
	private BigDecimal totalPrice;
	private List<CartItemDto> items;
	
	
	//sadece cart item görüntülemede kullanılıcağı için iç içe yazdım.
	@Getter
	@Setter
	public static class CartItemDto{
		private String productName;  
		private BigDecimal unitPrice ;
		private int quantity;
	}
}
