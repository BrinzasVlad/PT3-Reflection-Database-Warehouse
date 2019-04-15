package dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDTO {
	private int id;
	private int orderId;
	private int itemId;
	private String itemName;
	private int amount;
	private BigDecimal subtotal;
}
