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
	
	public GetOrderResponse getOrderForCode(Long orderId) {
		Order order = orderRepository.findById(orderId)//İstenilen siparişi id üzerinden çektik order nesnesine koyduk.
				.orElseThrow(() -> new IllegalArgumentException("Sipariş bulunamadı"));
		
		GetOrderResponse response = new GetOrderResponse();
		response.setTotalPrice(order.getTotalPrice());//Orderı zaten çekmiştik direkt içinden totalPrice bilgisini aldık.
		
		List<GetOrderResponse.OrderItemDto> itemsDtos = order.getOrderItems().stream()
			.map(item -> {
				GetOrderResponse.OrderItemDto dto = new GetOrderResponse.OrderItemDto();
				dto.setName(item.getProduct().getName());
				dto.setPriceAtPurchase(item.getPriceAtPurchase());
				dto.setQuantity(item.getQuantity());
				return dto;//oluşturulan ve içi doldurulan her dto itemsDtos listesinin bir parçası haline geliyor.
			}).toList();
		
		response.setItems(itemsDtos);//oluşturulan itemsDtos listesini GetOrderResponse türünde olan response nesnesine yerleştiriyoruz.
		return response;//Ve dönüş tipine uygun bir formatta bilgileri döndürüyoruz.
		
	}
	
	public GetAllOrdersResponse getAllOrdersForCustomer(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("Müşteri bulunamadı"));
		
		List<Order> orders = customer.getOrders();//Müşterinin siparişlerini listeye çekiyorum.
		
		List<GetAllOrdersResponse.OrderDto> orderDtos = orders.stream()//Müşterinin siparişlerinde teker teker gezinip tür dönüşümlerini yapıyorum.
				.map(order -> {
					GetAllOrdersResponse.OrderDto orderDto = new GetAllOrdersResponse.OrderDto();
					orderDto.setOrderId(order.getId());
					orderDto.setTotalPrice(order.getTotalPrice());
					orderDto.setCreatedAt(order.getCreatedAt().toString());
					//orderDtoların içini dolduruyoruz.
					List<GetAllOrdersResponse.OrderItemDto> orderItemsDtos =order.getOrderItems().stream()
							.map(item ->{
								GetAllOrdersResponse.OrderItemDto orderItemDto = new GetAllOrdersResponse.OrderItemDto();
								orderItemDto.setProductName(item.getProduct().getName());
								orderItemDto.setPriceAtPurchase(item.getPriceAtPurchase());
								orderItemDto.setQuantity(item.getQuantity());
								return orderItemDto;//Bir siparişin içindeki ürün satırlarını tutan dtoyu döner.
							}).toList();
					
					orderDto.setItems(orderItemsDtos);
					return orderDto;//Bir siparişin tüm verilerini tutan dtoyu döner.
			
		}).toList();
		GetAllOrdersResponse response = new GetAllOrdersResponse();
		response.setOrders(orderDtos);
		return response;
	}
	
	
}
