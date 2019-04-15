package business.managers;

import java.util.ArrayList;
import java.util.List;

import dao.UserDAO;
import dto.UserDTO;
import model.User;

public class UserManager implements AbstractManager<UserDTO> {
	private UserDAO dao = new UserDAO();

	public List<UserDTO> getAll() {
		List<User> orders = dao.findAll();
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		
		for(User u : orders) {
			dtos.add(elemToDto(u));
		}
		
		return dtos;
	}
	
	public UserDTO getById(int id) {
		return elemToDto(dao.findById(id));
	}

	public UserDTO update(UserDTO dto) {
		User u = dtoToElem(dto);
		
		return elemToDto(dao.update(u));
	}

	public void delete(UserDTO dto) {
		dao.delete(dtoToElem(dto));
	}
	
	public UserDTO insert(UserDTO dto) {
		User u = dtoToElem(dto);
		
		return elemToDto(dao.insert(u));
	}
	
	/**
	 * Converts a DTO to an element.
	 * @param dto - the UserDTO to convert
	 * @return A User with the relevant data
	 */
	private User dtoToElem(UserDTO dto) {
		User u = new User();
		u.setId(dto.getId());
		u.setEmail(dto.getEmail());
		return u;
	}
	
	/**
	 * Converts an element to a DTO.
	 * @param u - the User to convert
	 * @return A UserDTO with the relevant data
	 */
	private UserDTO elemToDto(User u) {
		UserDTO dto = new UserDTO();
		dto.setId(u.getId());
		dto.setEmail(u.getEmail());
		return dto;
	}
}
