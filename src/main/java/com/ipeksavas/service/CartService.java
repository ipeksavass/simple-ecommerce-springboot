package com.ipeksavas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ipeksavas.dto.request.AddProductToCartRequest;
import com.ipeksavas.dto.request.EmptyCartRequest;
import com.ipeksavas.dto.request.RemoveProductFromCartRequest;
import com.ipeksavas.dto.request.UpdateCartRequest;
import com.ipeksavas.dto.response.GetCartResponse;
import com.ipeksavas.mapper.CartMapper;
import com.ipeksavas.model.Cart;
import com.ipeksavas.model.CartItem;
import com.ipeksavas.model.Customer;
import com.ipeksavas.model.OperationType;
import com.ipeksavas.model.Product;
import com.ipeksavas.repository.CartRepository;
import com.ipeksavas.repository.CustomerRepository;
import com.ipeksavas.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;
	private final CartRepository cartRepository;
	
	@Transactional
	public void addProductToCart(AddProductToCartRequest request) {
		Customer customer = validateCustomer(request.getCustomer_id());
		Product product = validateProduct(request.getProduct_id());
		
		if(product.getStock() < request.getQuantity()) {
			throw new IllegalArgumentException("INSUFFICIENT STOCKS");
		}
		
		Cart cart = customer.getCart();
	
		addOrCreateCartItem(cart, product, request.getQuantity());
		updateCartTotalPrice(cart, product, request.getQuantity(),OperationType.ADD);

	}
	
	public GetCartResponse getCartByCustomerId(Long customerId){
		Customer customer = validateCustomer(customerId);
		Cart cart = customer.getCart();//customer has a field derived from the cart class.
		
		GetCartResponse response = new GetCartResponse();
		response.setTotalPrice(cart.getTotalPrice());
		
		//type conversion
		List<GetCartResponse.CartItemDto> itemDto = cart.getItems().stream()
				.map(CartMapper::mapToCartItemDto)
				.toList();
		
		response.setItems(itemDto);
		return response;
	}
	
	@Transactional
	public void removeProductFromCart(RemoveProductFromCartRequest request) {
		Customer customer = validateCustomer(request.getCustomerId());
		Product product = validateProduct(request.getProductId());
		Cart cart = customer.getCart();
		
		CartItem cartItem = findCartItem(cart, product.getId());
		if(cartItem.getQuantity() < request.getQuantity()) {
			throw new IllegalArgumentException("NOT THIS MANY ITEMS IN THE CART!!!");
		}
		cartItem.setQuantity(cartItem.getQuantity() - request.getQuantity());//the name is removed if there is no more product in the cart
		if(cartItem.getQuantity() == 0) {
			cart.getItems().remove(cartItem);
		}
		updateCartTotalPrice(cart, product, request.getQuantity(),OperationType.SUBTRACT);
	}
	
	@Transactional
	public void updateCartByCustomerId(UpdateCartRequest request) {
		Customer customer = validateCustomer(request.getCustomerId());
		Product product = validateProduct(request.getProductId());
		
		Cart cart = customer.getCart();
		CartItem cartItem = findCartItem(cart, request.getProductId());
		cartItem.setQuantity(request.getQuantity());
		updateCartTotalPrice(cart, null, 0, OperationType.CHANGE);
	}
	
	@Transactional
	public void emptyCartByCustomerId(EmptyCartRequest request) {
		Customer customer = validateCustomer(request.getCustomerId());
	
		Cart cart = customer.getCart();
		cart.getItems().clear();
		cart.setTotalPrice(BigDecimal.ZERO);
		customerRepository.save(customer); // updates database
	}
	
	private Customer validateCustomer(Long customerId) {
	    return customerRepository.findById(customerId)
	        .orElseThrow(() -> new IllegalArgumentException("NO CUSTOMER FOUND!!!"));
	}
	
	private Product validateProduct(Long productId) {
	    return productRepository.findById(productId)
	        .orElseThrow(() -> new IllegalArgumentException("NO PRODUCT FOUND!!!"));
	}
	
	private void addOrCreateCartItem(Cart cart, Product product, int quantity) {
		//if the product we are adding is already in the cart, we just need to increase its number.
		Optional<CartItem> existingItemOptional = cart.getItems().stream()
			.filter( item -> item.getProduct().getId().equals(product.getId()))
			.findFirst();
				
			if(existingItemOptional.isPresent()) {
				CartItem existItem = existingItemOptional.get();
				existItem.setQuantity(existItem.getQuantity() +quantity);
			}else{
				CartItem newItem = new CartItem();
				newItem.setQuantity(quantity);
				newItem.setProduct(product);//product and cart fields are derived from their own classes
				newItem.setCart(cart);
				cart.getItems().add(newItem);//There is a list of objects derived from cart class, here I added to the list.
			}
	}
	
	private CartItem findCartItem(Cart cart, Long productId) {
	    return cart.getItems().stream()
	            .filter(item -> item.getProduct().getId().equals(productId))
	            .findFirst()
	            .orElseThrow(() -> new IllegalArgumentException("THIS PRODUCT WAS NOT FOUND IN CART!!!"));
	}

	private void updateCartTotalPrice(Cart cart, Product product, int quantity, OperationType operation) {
		
		switch(operation) {
			case ADD -> {
				BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(quantity));
				cart.setTotalPrice(cart.getTotalPrice().add(amount));
			}
			case SUBTRACT -> {
				BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(quantity));
				cart.setTotalPrice(cart.getTotalPrice().subtract(amount));
			}
			case CHANGE -> {
				BigDecimal newTotal = cart.getItems().stream()
	                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
	                .reduce(BigDecimal.ZERO, BigDecimal::add);
	            cart.setTotalPrice(newTotal);
			}
		}
	}
}
