package com.ipeksavas.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.ipeksavas.dto.response.GetAllOrdersResponse;
import com.ipeksavas.dto.response.GetOrderResponse;
import com.ipeksavas.model.Order;
import com.ipeksavas.model.OrderItem;

//A helper class containing all transformation methods.
public class OrderMapper {
	 public static GetAllOrdersResponse.OrderItemDto mapToOrderItemDto(OrderItem item) {
	        GetAllOrdersResponse.OrderItemDto dto = new GetAllOrdersResponse.OrderItemDto();
	        dto.setProductName(item.getProduct().getName());
	        dto.setPriceAtPurchase(item.getPriceAtPurchase());
	        dto.setQuantity(item.getQuantity());
	        return dto;
	    }

	    public static GetAllOrdersResponse.OrderDto mapToOrderDto(Order order) {
	        GetAllOrdersResponse.OrderDto dto = new GetAllOrdersResponse.OrderDto();
	        dto.setOrderId(order.getId());
	        dto.setTotalPrice(order.getTotalPrice());
	        dto.setCreatedAt(order.getCreatedAt().toString());

	        List<GetAllOrdersResponse.OrderItemDto> itemDtos = order.getOrderItems().stream()
	                .map(OrderMapper::mapToOrderItemDto)
	                .collect(Collectors.toList());

	        dto.setItems(itemDtos);
	        return dto;
	    }
	    
	    public static List<GetOrderResponse.OrderItemDto> mapToOrderItemDtosForSingleOrder(List<OrderItem> orderItems) {
	        return orderItems.stream().map(item -> {
	            GetOrderResponse.OrderItemDto dto = new GetOrderResponse.OrderItemDto();
	            dto.setName(item.getProduct().getName());
	            dto.setPriceAtPurchase(item.getPriceAtPurchase());
	            dto.setQuantity(item.getQuantity());
	            return dto;
	        }).toList();
	    }
}
