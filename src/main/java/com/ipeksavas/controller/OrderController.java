package com.ipeksavas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipeksavas.dto.request.PlaceOrderRequest;
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
}
