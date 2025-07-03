package com.ipeksavas.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RemoveProductFromCartRequest {

	private Long customerId;
	
	private Long productId;
	
	private int quantity;
}
