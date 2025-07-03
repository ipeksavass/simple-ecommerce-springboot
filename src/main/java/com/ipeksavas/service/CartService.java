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
import com.ipeksavas.model.Cart;
import com.ipeksavas.model.CartItem;
import com.ipeksavas.model.Customer;
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
		Customer customer = customerRepository.findById(request.getCustomer_id())
				.orElseThrow(()-> new IllegalArgumentException("MÜŞTERİ BULUNAMADI!!!"));
		
		Product product = productRepository.findById(request.getProduct_id())
				.orElseThrow(() -> new IllegalArgumentException("ÜRÜN BULUNAMADI!!!"));
		
		if(product.getStock() < request.getQuantity()) {
			throw new IllegalArgumentException("YETERLİ STOK YOK");
		}
		
		Cart cart = customer.getCart();
		
		//eklediğimiz ürün zaten sepetteyse sadece sayısını arttırmalıyız.
		//Bir stream başlatıp streamin özellikleriyle tüm itemları teker teker gezerek hepsinin idsini product id
		//ile kıyaslıyorum eğer eşitlik varsa sadece sepetteki sayısı arttırılıcak.
		Optional<CartItem> existingItemOptional = cart.getItems().stream()
				.filter( item -> item.getProduct().getId().equals(product.getId()))
				.findFirst();
		
		if(existingItemOptional.isPresent()) {
			CartItem existItem = existingItemOptional.get();
			existItem.setQuantity(existItem.getQuantity() + request.getQuantity());
			//var olan ürün sayısı ile eklenmek istenen ürün sayısı toplanıyor ve sepetteki sayı güncelleniyor.
		}else {// yani içeride eklemek istediğimiz üründen yok ise yeni bir ürün eklicez.
			CartItem newItem = new CartItem();
			newItem.setQuantity(request.getQuantity());// quantity özelliği direkt carItem classına ait
			newItem.setProduct(product);//product ve cart özellikleri kendi classlarından türetildiği için bu şekilde oldu
			newItem.setCart(cart);
			cart.getItems().add(newItem);//cart classından türetilen neslerin bir listesi var burada  listeye ekleme yaptım.
		}
		//Sepete ürün eklemiş olduk yani sepetin toplam fiyatı değişti onu güncellemeliyiz.
		BigDecimal addedPrice = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
		cart.setTotalPrice(cart.getTotalPrice().add(addedPrice));
		
//		//sepete eklenince stok azalmamalı o yüzden bu kodu kaldırdım.
//		// Stoktan düşer ve veri tabanına yansıtır
//	    int updatedStock = product.getStock() - request.getQuantity();
//	    product.setStock(updatedStock);
//	    productRepository.save(product); // Stok güncellemesini veri tabanına yansıtır.
		
	}
	
	public GetCartResponse getCartByCustomerId(Long customerId){
		Customer customer = customerRepository.findById(customerId)//customer repodan verilen idli müşteriyi çektim.
		.orElseThrow(() -> new IllegalArgumentException("MÜŞTERİ BULUNAMADI!!!"));
		//Bu id ile bir müşteri yoksa exception fırlatıyoruz.
		
		Cart cart = customer.getCart();//customerın cart classından türetilmiş fieldı var.
		
		GetCartResponse response = new GetCartResponse();
		response.setTotalPrice(cart.getTotalPrice());//toplam sepet tutarını çektim.
		
		//cart classına ait nesleri cartItemDtoya döndürmem gerektiği için tür dönüşümü yapıyorum. 
		List<GetCartResponse.CartItemDto> itemDto = cart.getItems().stream()
				.map(item -> {
					GetCartResponse.CartItemDto dto = new GetCartResponse.CartItemDto();
					
					dto.setProductName(item.getProduct().getName());
					dto.setUnitPrice(item.getProduct().getPrice());
					dto.setQuantity(item.getQuantity());
					
					return dto;
				
				})
				.toList();
		response.setItems(itemDto);
		return response;
	}
	
	@Transactional
	public void removeProductFromCart(RemoveProductFromCartRequest request) {
		Customer customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new IllegalArgumentException("MÜŞTERİ BULUNMADI!!!"));
		
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new IllegalArgumentException("ÜRÜN TABLOSUNDA BÖYLE BİR ÜRÜN YOK"));
		
		Cart cart = customer.getCart();
		
		Optional<CartItem> optItem = cart.getItems().stream()
				.filter(item -> item.getProduct().getId().equals(product.getId()))
				.findFirst();
		
		if(optItem.isEmpty()) {
			throw new IllegalArgumentException("BU ÜRÜN SEPETTE BULUNAMADI!!!");
		}
		
		CartItem cartItem = optItem.get();
		//optItem aslında bir kabuk o nesne üzerinden işlem yapmak için get() ile dışarı çıkartmalıyız.
		
		if(cartItem.getQuantity() < request.getQuantity()) {
			throw new IllegalArgumentException("SEPETTE BU KADAR ÜRÜN YOK!!!");
		}
		
		//sepetten çıkartılmak istenen ürünler çıkartıldı.
		cartItem.setQuantity(cartItem.getQuantity() - request.getQuantity());
		
		if(cartItem.getQuantity() == 0) {//eger sepette o üründen hiç kalmadıysa o ürünün
			cart.getItems().remove(cartItem);   // adını sepetimizden silmemiz gerekir.
		}
		
		BigDecimal removedPrice = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
		cart.setTotalPrice(cart.getTotalPrice().subtract(removedPrice));//subtract kullanıyoruz çünkü bigdecimal türünde sayılar - kullanılmaz burada
		
		//sepetten bir şey çıkarılması stokları etkilemediği için bu kodu kaldırdım.
//		int updatedStock = product.getStock() + request.getQuantity();
//		product.setStock(updatedStock);
//		productRepository.save(product);
		
	}
	
	@Transactional
	public void updateCartByCustomerId(UpdateCartRequest request) {
		Customer customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new IllegalArgumentException("MÜŞTERİ BULUNAMADI!!!"));
		
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new IllegalArgumentException("ÜRÜN KAYDI YOK!!!"));
		
		Cart cart = customer.getCart();
		
		Optional<CartItem> optItem = cart.getItems().stream()
				.filter(item -> item.getProduct().getId().equals(request.getProductId()))
				.findFirst();
		
		if( optItem.isEmpty()) {
			throw new IllegalArgumentException("SEPETTE BU ÜRÜNDEN YOK!!!");
		}
		
		CartItem cartItem = optItem.get();
		BigDecimal oldPrice = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
		BigDecimal newPrice = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
		
		cartItem.setQuantity(request.getQuantity());
		cart.setTotalPrice(cart.getTotalPrice().subtract(oldPrice).add(newPrice));
	}
	
	@Transactional
	public void emptyCartByCustomerId(EmptyCartRequest request) {
		Customer customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new IllegalArgumentException("KULLANICI BULUNAMADI!!!"));
		
		//müşterinin sepetini çektim ve boşalttım.
		Cart cart = customer.getCart();
		cart.getItems().clear();
		cart.setTotalPrice(BigDecimal.ZERO);
		customerRepository.save(customer); // Bu satır veritabanını günceller
		
	}
	
}
