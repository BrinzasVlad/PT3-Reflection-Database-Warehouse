package business.managers;

import java.util.ArrayList;
import java.util.List;

import dao.ItemDAO;
import dto.ItemDTO;
import model.Item;

public class ItemManager implements AbstractManager<ItemDTO> {
	private ItemDAO dao = new ItemDAO();
	
	public List<ItemDTO> getAll() {
		List<Item> items = dao.findAll();
		List<ItemDTO> dtos = new ArrayList<ItemDTO>();
		
		for(Item i : items) {
			dtos.add(elemToDto(i));
		}
		
		return dtos;
	}
	
	public ItemDTO getById(int id) {
		return elemToDto(dao.findById(id));
	}

	public ItemDTO update(ItemDTO dto) {
		Item i = dtoToElem(dto);
		
		return elemToDto(dao.update(i));
	}

	public void delete(ItemDTO dto) {
		dao.delete(dtoToElem(dto));
	}
	
	public ItemDTO insert(ItemDTO dto) {
		Item i = dtoToElem(dto);
		
		return elemToDto(dao.insert(i));
	}
	
	/**
	 * Converts a DTO to an element.
	 * @param dto - the ItemDTO to convert
	 * @return An Item with the relevant data
	 */
	private Item dtoToElem(ItemDTO dto) {
		Item i = new Item();
		i.setId(dto.getId());
		i.setName(dto.getName());
		i.setPrice(dto.getPrice());
		i.setStock(dto.getStock());
		return i;
	}
	
	/**
	 * Converts an element to a DTO.
	 * @param i - the Item to convert
	 * @return An ItemDTO with the relevant data
	 */
	private ItemDTO elemToDto(Item i) {
		ItemDTO dto = new ItemDTO();
		dto.setId(i.getId());
		dto.setName(i.getName());
		dto.setPrice(i.getPrice());
		dto.setStock(i.getStock());
		return dto;
	}
}
