package com.ipeksavas.controller;

import java.nio.channels.NonReadableChannelException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipeksavas.dto.request.PlaceOrderRequest;
import com.ipeksavas.dto.response.GetAllOrdersResponse;
import com.ipeksavas.dto.response.GetOrderResponse;
import com.ipeksavas.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	
	@PostMapping("/place")
	public ResponseEntity<String> placeOrder(@RequestBody PlaceOrderRequest request) {
		orderService.placeOrder(request);
		return ResponseEntity.ok("Sipariş oluşturulmuştur");
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity<GetOrderResponse> getOrderForCode(@PathVariable Long orderId){
		GetOrderResponse response = orderService.getOrderForCode(orderId);
		return ResponseEntity.ok(response);
		
	}
	
	@GetMapping("/customer/{customerId}")
	public ResponseEntity<GetAllOrdersResponse> getAllOrdersForCustomer(@PathVariable Long customerId){
		GetAllOrdersResponse response = orderService.getAllOrdersForCustomer(customerId);
		return ResponseEntity.ok(response);
	}
	
}
