package com.ipeksavas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipeksavas.dto.request.ProductRequest;
import com.ipeksavas.dto.request.UpdateProductRequest;
import com.ipeksavas.dto.response.ProductResponse;
import com.ipeksavas.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;
	
	@PostMapping(path = "/save")
	public ResponseEntity<String> addProduct(@RequestBody ProductRequest request){
		productService.addProduct(request);
		return ResponseEntity.ok("PRODUCT REGISTERED SUCCESSFULLY");
	}
	
	@GetMapping(path = "/{id}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id){
		ProductResponse response = productService.getProductById(id);
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(path = "/update/{id}")
	public ResponseEntity<String> updateProductById(@PathVariable Long id,@RequestBody UpdateProductRequest request){
		productService.updateProductById(id, request);
		return ResponseEntity.ok("PRODUCT UPDATED SUCCESSFULLY");
	}
	
	@DeleteMapping(path = "/delete/{id}")
	public ResponseEntity<String> deleteProductById(@PathVariable Long id){
		productService.deleteProductById(id);
		return ResponseEntity.ok("PRODUCT DELETED SUCCESSFULLY");
	}
}
