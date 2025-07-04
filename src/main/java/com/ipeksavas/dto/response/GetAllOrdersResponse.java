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
	public static class OrderDto{//İç sınıfın bağımsızlığını sağlamak için static keywordü kullanıldı.
		//Siparişin genel bilgileri için bir dto tanımladım.
		private Long orderId;
		private BigDecimal totalPrice;
		private String createdAt;
		private List<OrderItemDto> items;
	}
	
	@Setter
	@Getter
	public static class OrderItemDto{
		//Siparişin bir ürününe ait bilgileri tutması için bir dto tanımladım.
		private String productName;
		private BigDecimal priceAtPurchase;
		private int quantity;
	}
}
