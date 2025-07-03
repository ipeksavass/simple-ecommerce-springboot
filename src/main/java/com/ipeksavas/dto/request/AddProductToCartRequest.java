package com.ipeksavas.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddProductToCartRequest {

	private Long customer_id;
	private Long product_id;
	private int quantity;
}
