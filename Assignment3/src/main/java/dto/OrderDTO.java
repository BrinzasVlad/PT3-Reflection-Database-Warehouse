package dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderDTO {
	private int id;
	private int userId;
	private int noOfItems;
	private BigDecimal totalPrice;
}
