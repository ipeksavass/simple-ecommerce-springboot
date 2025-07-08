package com.ipeksavas.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ipeksavas.dto.request.PlaceOrderRequest;
import com.ipeksavas.dto.response.GetAllOrdersResponse;
import com.ipeksavas.dto.response.GetOrderResponse;
import com.ipeksavas.mapper.OrderMapper;
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
		Customer customer = validateCustomerAndCart(request.getCustomerId());
		
		Cart cart = customer.getCart();
		
		Order order = new Order();
		order.setCustomer(customer);

		BigDecimal totalPrice = prepareOrder(cart.getItems(), order);
		order.setTotalPrice(totalPrice);
		
		orderRepository.save(order);
		clearCart(cart);
	}
	
	public GetOrderResponse getOrderForCode(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("NO ORDER FOUND"));
		
		GetOrderResponse response = new GetOrderResponse();
		response.setTotalPrice(order.getTotalPrice());

		List<GetOrderResponse.OrderItemDto> itemsDtos =
				OrderMapper.mapToOrderItemDtosForSingleOrder(order.getOrderItems());
		
		response.setItems(itemsDtos);//We place the created itemsDtos list into the response object of type GetOrderResponse.
		return response;
		
	}
	
	public GetAllOrdersResponse getAllOrdersForCustomer(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("NO CUSTOMER FOUND"));
		
		List<Order> orders = customer.getOrders();//I pull the customer's orders into the list.
		
		List<GetAllOrdersResponse.OrderDto> orderDtos = orders.stream()
				.map(OrderMapper::mapToOrderDto)
	            .toList();
		
		GetAllOrdersResponse response = new GetAllOrdersResponse();
		response.setOrders(orderDtos);
		return response;
	}
	
	private Customer validateCustomerAndCart(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("NO CUSTOMER FOUND"));
		
		if(customer.getCart().getItems().isEmpty()) {
			throw new IllegalStateException("BASKET EMPTY NO ORDER CAN BE PLACED");
		}
		return customer;
		
	}
	
	private BigDecimal prepareOrder(List<CartItem> cartItems, Order order) {
		List<OrderItem> orderItems = new ArrayList<>();
		BigDecimal totalPrice = BigDecimal.ZERO;
		
		for(CartItem item: cartItems) {
			Product product =item.getProduct();
			if(product.getStock() < item.getQuantity()) {
				throw new IllegalArgumentException("INSUFFICIENT STOCK: " + product.getName());
			}
			
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setPriceAtPurchase(product.getPrice());
			orderItem.setQuantity(item.getQuantity());
			
			orderItems.add(orderItem);
			
			product.setStock(product.getStock() - item.getQuantity());
			productRepository.save(product);
			
			BigDecimal addPrice = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			totalPrice = totalPrice.add(addPrice);
		}
		order.setOrderItems(orderItems);
		return totalPrice;
	}
	
	private void clearCart(Cart cart) {
		cart.getItems().clear();
		cart.setTotalPrice(BigDecimal.ZERO);
	}
	
}
