package business.managers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import dao.ItemDAO;
import dao.OrderItemDAO;
import dto.OrderItemDTO;
import model.Item;
import model.OrderItem;

public class OrderItemManager implements AbstractManager<OrderItemDTO> {
	private OrderItemDAO dao = new OrderItemDAO();
	private ItemDAO iDao = new ItemDAO();

	public List<OrderItemDTO> getAll() {
		List<OrderItem> ois = dao.findAll();
		List<OrderItemDTO> dtos = new ArrayList<OrderItemDTO>();
		
		for(OrderItem oi : ois) {
			dtos.add(elemToDto(oi));
		}
		
		return dtos;
	}
	
	public OrderItemDTO getById(int id) {
		return elemToDto(dao.findById(id));
	}
	
	public List<OrderItemDTO> getByOrderId(int id) {
		List<OrderItem> ois = dao.findByOrderId(id);
		List<OrderItemDTO> dtos = new ArrayList<OrderItemDTO>();
		
		for(OrderItem oi : ois) {
			dtos.add(elemToDto(oi));
		}
		
		return dtos;
	}

	public OrderItemDTO update(OrderItemDTO dto) {
		OrderItem oi = dtoToElem(dto);
		
		return elemToDto(dao.update(oi));
	}

	public void delete(OrderItemDTO dto) {
		dao.delete(dtoToElem(dto));
	}

	public OrderItemDTO insert(OrderItemDTO dto) {
		OrderItem oi = dtoToElem(dto);
		
		return elemToDto(dao.insert(oi));
	}

	/**
	 * Converts a DTO to an element.
	 * @param dto - the OrderItemDTO to convert
	 * @return An OrderItem with the relevant data
	 */
	private OrderItem dtoToElem(OrderItemDTO dto) {
		OrderItem oi = new OrderItem();
		oi.setId(dto.getId());
		oi.setItem_id(dto.getItemId());
		oi.setOrder_id(dto.getOrderId());
		oi.setAmount(dto.getAmount());
		return oi;
	}
	
	private OrderItemDTO elemToDto(OrderItem oi) {
		OrderItemDTO dto = new OrderItemDTO();
		// Transmitted
		dto.setId(oi.getId());
		dto.setItemId(oi.getItem_id());
		dto.setOrderId(oi.getOrder_id());
		dto.setAmount(oi.getAmount());
		
		// Computed
		Item i = iDao.findById(oi.getItem_id());
		dto.setItemName(i.getName());
		dto.setSubtotal(i.getPrice()
					    .multiply(new BigDecimal(oi.getAmount())));
		
		return dto;
	}
}
