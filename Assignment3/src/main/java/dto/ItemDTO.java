package dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ItemDTO {
	private int id;
	private String name;
	private BigDecimal price;
	private int stock;
}
