package business.managers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import dao.ItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import dto.OrderDTO;
import model.Order;
import model.OrderItem;

public class OrderManager implements AbstractManager<OrderDTO> {
	private OrderDAO dao = new OrderDAO();
	private OrderItemDAO oiDao = new OrderItemDAO();
	private ItemDAO iDao = new ItemDAO();

	public List<OrderDTO> getAll() {
		List<Order> orders = dao.findAll();
		List<OrderDTO> dtos = new ArrayList<OrderDTO>();
		
		for(Order o : orders) {
			dtos.add(elemToDto(o));
		}
		
		return dtos;
	}
	
	public OrderDTO getById(int id) {
		return elemToDto(dao.findById(id));
	}

	public OrderDTO update(OrderDTO dto) {
		Order o = dtoToElem(dto);
		
		return elemToDto(dao.update(o));
	}

	public void delete(OrderDTO dto) {
		dao.delete(dtoToElem(dto));
	}
	
	public OrderDTO insert(OrderDTO dto) {
		Order o = dtoToElem(dto);
		
		return elemToDto(dao.insert(o));
	}
	
	/**
	 * Converts a DTO to an element.
	 * @param dto - the OrderDTO to convert
	 * @return An Order with the relevant data
	 */
	private Order dtoToElem(OrderDTO dto) {
		Order o = new Order();
		o.setId(dto.getId());
		o.setUser_id(dto.getUserId());
		return o;
	}
	
	/**
	 * Converts an element to a DTO.
	 * @param o - the Order to convert
	 * @return An OrderDTO with the relevant data
	 */
	private OrderDTO elemToDto(Order o) {
		OrderDTO dto = new OrderDTO();
		// Transmitted
		dto.setId(o.getId());
		dto.setUserId(o.getUser_id());
		
		//Computed
		int noOfItems = 0;
		BigDecimal totalPrice = BigDecimal.ZERO;
		
		List<OrderItem> orderItems = oiDao.findByOrderId(o.getId());
		for(OrderItem oi : orderItems) {
			noOfItems++;
			totalPrice = totalPrice.add(iDao.findById(oi.getItem_id()).getPrice()
						   		        .multiply(new BigDecimal( oi.getAmount()) ));
		}
		
		dto.setNoOfItems(noOfItems);
		dto.setTotalPrice(totalPrice);
		
		return dto;
	}

}
