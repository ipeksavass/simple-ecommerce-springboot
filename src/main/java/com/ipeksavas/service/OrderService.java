package com.ipeksavas.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ipeksavas.dto.request.PlaceOrderRequest;
import com.ipeksavas.dto.response.GetAllOrdersResponse;
import com.ipeksavas.dto.response.GetOrderResponse;
import com.ipeksavas.model.Cart;
import com.ipeksavas.model.CartItem;
import com.ipeksavas.model.Customer;
import com.ipeksavas.model.Order;
import com.ipeksavas.model.OrderItem;
import com.ipeksavas.model.Product;
import com.ipeksavas.repository.CustomerRepository;
import com.ipeksavas.repository.OrderRepository;
import com.ipeksavas.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
	
	@Transactional
	public void placeOrder(PlaceOrderRequest request) {
    	Long id = request.getCustomerId();
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("NO CUSTOMER FOUND"));
	
		Cart cart = customer.getCart();
		List<CartItem> cartItems = cart.getItems();

		if(cartItems.isEmpty()) {
			throw new IllegalStateException("BASKET EMPTY NO ORDER CAN BE PLACED");
		}

		Order order = new Order();
		order.setCustomer(customer);

		BigDecimal totalPrice = BigDecimal.ZERO;

		List<OrderItem> orderItems = new ArrayList<>();
		//  {entity,   x,     list}
		for(CartItem item: cart.getItems()) { 
			Product product = item.getProduct();
			if(product.getStock() < item.getQuantity()) {
				throw new IllegalArgumentException("INSUFFICIENT STOCK: " + product.getName());
			}
			
			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(product);
			orderItem.setOrder(order);
			orderItem.setQuantity(item.getQuantity());
			orderItem.setPriceAtPurchase(product.getPrice());
			orderItems.add(orderItem);
			
			//The quantity of the product we order is being reduced from stock.
			product.setStock(product.getStock() - item.getQuantity());
			productRepository.save(product);
			
			//Price update
			BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			totalPrice = totalPrice.add(itemTotal);
		}
		
		order.setOrderItems(orderItems);
		order.setTotalPrice(totalPrice);
		
		orderRepository.save(order);
		
		cart.getItems().clear();
		cart.setTotalPrice(BigDecimal.ZERO);
	}
	
	public GetOrderResponse getOrderForCode(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("NO ORDER FOUND"));
		
		GetOrderResponse response = new GetOrderResponse();
		response.setTotalPrice(order.getTotalPrice());
		
		List<GetOrderResponse.OrderItemDto> itemsDtos = order.getOrderItems().stream()
			.map(item -> {
				GetOrderResponse.OrderItemDto dto = new GetOrderResponse.OrderItemDto();
				dto.setName(item.getProduct().getName());
				dto.setPriceAtPurchase(item.getPriceAtPurchase());
				dto.setQuantity(item.getQuantity());
				return dto;//Each dto created and filled becomes part of the itemsDtos list.
			}).toList();
		
		response.setItems(itemsDtos);//We place the created itemsDtos list into the response object of type GetOrderResponse.
		return response;
		
	}
	
	public GetAllOrdersResponse getAllOrdersForCustomer(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("NO CUSTOMER FOUND"));
		
		List<Order> orders = customer.getOrders();//I pull the customer's orders into the list.
		
		List<GetAllOrdersResponse.OrderDto> orderDtos = orders.stream()//I go through the customer's orders one by one and make type conversions.
				.map(order -> {
					GetAllOrdersResponse.OrderDto orderDto = new GetAllOrdersResponse.OrderDto();
					orderDto.setOrderId(order.getId());
					orderDto.setTotalPrice(order.getTotalPrice());
					orderDto.setCreatedAt(order.getCreatedAt().toString());
		
					List<GetAllOrdersResponse.OrderItemDto> orderItemsDtos =order.getOrderItems().stream()
							.map(item ->{
								GetAllOrdersResponse.OrderItemDto orderItemDto = new GetAllOrdersResponse.OrderItemDto();
								orderItemDto.setProductName(item.getProduct().getName());
								orderItemDto.setPriceAtPurchase(item.getPriceAtPurchase());
								orderItemDto.setQuantity(item.getQuantity());
								return orderItemDto;//Returns the dto that holds the product lines in an order.
							}).toList();
					
					orderDto.setItems(orderItemsDtos);
					return orderDto;//Returns the dto that holds all the data of an order.
			
		}).toList();
		GetAllOrdersResponse response = new GetAllOrdersResponse();
		response.setOrders(orderDtos);
		return response;
	}
	
	
}
