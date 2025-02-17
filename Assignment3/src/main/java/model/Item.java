package model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Item {
	private int id;
	private String name;
	private BigDecimal price;
	private int stock;
}
