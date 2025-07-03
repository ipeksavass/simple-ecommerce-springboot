package com.ipeksavas.service;

import org.springframework.stereotype.Service;

import com.ipeksavas.dto.request.CustomerRequest;
import com.ipeksavas.model.Cart;
import com.ipeksavas.model.Customer;
import com.ipeksavas.repository.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  //@Autowired yerine kullandım daha guvenliymis 
public class CustomerService {
	private final CustomerRepository customerRepository;
	
	@Transactional//herhangi bir islemde hata olursa yaptigi her seyi geri alir ve veri tabani temiz kalmis olur.
	public void addCustomer(CustomerRequest request) {
		if(customerRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Bu eposta adresi sistemde kayıtlıdır.");
		}
		
		Customer customer = new Customer();
		customer.setName(request.getName());
		customer.setEmail(request.getEmail());
		
		//Musteri kaydı tamamlandıgı icin bos bir sepet olusturuyorum
		Cart cart = new Cart();
		cart.setCustomer(customer);
		customer.setCart(cart);//cift taraflı iliski kuruyorum
		
		customerRepository.save(customer);
	}
}
