package main;

import dao.ItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.UserDAO;
import model.Item;
import model.Order;
import model.OrderItem;
import model.User;

public class Main {

	public static void main(String[] args) {
		ItemDAO itemDAO = new ItemDAO();
		UserDAO userDAO = new UserDAO();
		OrderDAO orderDAO = new OrderDAO();
		OrderItemDAO oiDAO = new OrderItemDAO();
		
		Item i = new Item();
		//i.setId(0); // Pointless because it's auto-increment anyway
		i.setName("Candy");
		i.setPrice(4.3);
		itemDAO.insert(i);
		System.out.println(i);
		
		User u = new User();
		u.setEmail("bob@bobson.com");
		userDAO.insert(u);
		System.out.println(u);
		
		Order o = new Order();
		o.setUser_id(u.getId());
		orderDAO.insert(o);
		System.out.println(o);
		
		OrderItem oi = new OrderItem();
		oi.setItem_id(i.getId());
		oi.setOrder_id(o.getId());
		oi.setAmount(3);
		oiDAO.insert(oi);
		System.out.println(oi);
		
		for(OrderItem ohoho : oiDAO.findByOrderId(o.getId())) {
			System.out.println(ohoho);
		}
	}

}
