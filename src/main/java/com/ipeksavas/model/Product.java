package com.ipeksavas.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity{

	@Column(name = "name")
	private String name;
	
	@Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
	
	@Column(name = "stock")
	private int stock;
}
