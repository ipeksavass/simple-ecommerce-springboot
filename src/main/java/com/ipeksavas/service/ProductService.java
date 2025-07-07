package com.ipeksavas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ipeksavas.dto.request.ProductRequest;
import com.ipeksavas.dto.request.UpdateProductRequest;
import com.ipeksavas.dto.response.ProductResponse;
import com.ipeksavas.model.CartItem;
import com.ipeksavas.model.Product;
import com.ipeksavas.repository.CartItemRepository;
import com.ipeksavas.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final CartItemRepository cartItemRepository;
	
	@Transactional
	public void addProduct(ProductRequest request) {
		Product product = new Product();
		product.setName(request.getName());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());
		productRepository.save(product);
		
	}
	
	public ProductResponse getProductById(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("PRODUCT NOT FOUND"));
		
		ProductResponse response = new ProductResponse();
		response.setName(product.getName());
		response.setPrice(product.getPrice());
		response.setStock(product.getStock());
		return response;
	}
	
	public void updateProductById(Long id, UpdateProductRequest request) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("PRODUCT NOT FOUND"));
		
		product.setName(request.getName());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());
		
		productRepository.save(product);
	}
	
	public void deleteProductById(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("PRODUCT NOT FOUND"));
		
		//A product cannot be deleted directly from the product table, it must first be removed from the baskets
		List<CartItem> relatedCartItems = cartItemRepository.findByProductId(productId);
		cartItemRepository.deleteAll(relatedCartItems);
		productRepository.delete(product);
	}
}
