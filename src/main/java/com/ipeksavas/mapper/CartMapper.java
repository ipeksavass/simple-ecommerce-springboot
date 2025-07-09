package com.ipeksavas.mapper;

import com.ipeksavas.dto.response.GetCartResponse;
import com.ipeksavas.model.CartItem;

public class CartMapper {
	public static GetCartResponse.CartItemDto mapToCartItemDto(CartItem item){
		GetCartResponse.CartItemDto dto = new GetCartResponse.CartItemDto();
		dto.setProductName(item.getProduct().getName());
		dto.setUnitPrice(item.getProduct().getPrice());
		dto.setQuantity(item.getQuantity());
		return dto;
	}
}
