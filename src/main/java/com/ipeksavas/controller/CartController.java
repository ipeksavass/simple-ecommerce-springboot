package com.ipeksavas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipeksavas.dto.request.AddProductToCartRequest;
import com.ipeksavas.dto.request.EmptyCartRequest;
import com.ipeksavas.dto.request.RemoveProductFromCartRequest;
import com.ipeksavas.dto.request.UpdateCartRequest;
import com.ipeksavas.dto.response.GetCartResponse;
import com.ipeksavas.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;
	
	@PostMapping(path = "/add-product")
	public ResponseEntity<String> addProductToCart(@RequestBody AddProductToCartRequest request){
		cartService.addProductToCart(request);
		return ResponseEntity.ok("ÜRÜN SEPETE BAŞARIYLA EKLENMİŞTİR");
	}
	
	@GetMapping(path = "/{customerId}")
	public ResponseEntity<GetCartResponse> getCartByCustomerId(@PathVariable Long customerId){
		GetCartResponse response = cartService.getCartByCustomerId(customerId);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping(path = "/remove-product")
	public ResponseEntity<String> removeProductFromCart(@RequestBody RemoveProductFromCartRequest request){
		cartService.removeProductFromCart(request);
		return ResponseEntity.ok("ÜRÜN SEPETTEN BAŞARIYLA ÇIKARILMIŞTIR");
	}
	
	@PutMapping(path = "/update")
	public ResponseEntity<String> updateCartByCustomerId(@RequestBody UpdateCartRequest request){
		cartService.updateCartByCustomerId(request);
		return ResponseEntity.ok("SEPET GÜNCELLENMİŞTİR");
	}
	
	@PostMapping(path = "/empty")
	public ResponseEntity<String> emptyCartByCustomerId(@RequestBody EmptyCartRequest request) {
		cartService.emptyCartByCustomerId(request);
		return ResponseEntity.ok("SEPET BOŞALTILMIŞTIR");
	}
	
	
	
}
