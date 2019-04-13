package model;

import lombok.Data;

@Data
public class OrderItem {
	private int id;
	private int order_id;
	private int item_id;
	private int amount;
}
