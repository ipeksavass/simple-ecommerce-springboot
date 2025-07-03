package com.ipeksavas.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ipeksavas.dto.request.PlaceOrderRequest;
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
    	//Öncelikle müşteriyi getiriyoruz.
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Müşteri bulunamadı"));
		
		//Müşterinin sepetini getiriyoruz.
		Cart cart = customer.getCart();
		List<CartItem> cartItems = cart.getItems();
		
		//Sepette ürün var mı kontrol ediyorum.
		if(cartItems.isEmpty()) {
			throw new IllegalStateException("Sepet boş sipariş verilemez");
		}
		
		//Order entitysinin içinde Customer sınıfından türetilen bir customer fieldı var.
		Order order = new Order();
		order.setCustomer(customer);
		
		//Siparişin başlangıç fiyatı 0dan başlatılıyor.
		BigDecimal totalPrice = BigDecimal.ZERO;
		//Sipariş içindeki ürünleri tutmak için bir array list oluşturdum.
		List<OrderItem> orderItems = new ArrayList<>();
		//  {entity,   x,     list}
		for(CartItem item: cart.getItems()) { 
			Product product = item.getProduct();
			if(product.getStock() < item.getQuantity()) {
				throw new IllegalArgumentException("Stok yetersiz: " + product.getName());
			}
			
			//Order Item fieldlarını dolduruyoruz. Tabloda görücez.
			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(product);
			orderItem.setOrder(order);
			orderItem.setQuantity(item.getQuantity());//sepetteki ürün miktarı kadar ürünü siparişe ekliyoruz.
			orderItem.setPriceAtPurchase(product.getPrice());
			orderItems.add(orderItem);
			
			//sipariş verdiğimiz ürün miktarı stoktan düşürülüyor.
			product.setStock(product.getStock() - item.getQuantity());
			productRepository.save(product);
			
			//fiyat güncellemesi
			BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			totalPrice = totalPrice.add(itemTotal);
			
		}
		
		order.setOrderItems(orderItems);
		order.setTotalPrice(totalPrice);
		
		//Sipariş kaydedildi.
		orderRepository.save(order);
		
		//Sepet boşaltıldı.
		cart.getItems().clear();
		cart.setTotalPrice(BigDecimal.ZERO);
		
	}
}
