package business.controller;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import business.managers.ItemManager;
import business.managers.OrderItemManager;
import business.managers.OrderManager;
import business.managers.UserManager;
import business.receipt.ReceiptWriter;
import dto.ItemDTO;
import dto.OrderDTO;
import dto.OrderItemDTO;
import dto.UserDTO;
import presentation.CartView;
import presentation.GenericView;
import presentation.MainView;
import presentation.MainView.LabelType;

public class Controller {
	private ItemManager itemMan    = new ItemManager();
	private UserManager userMan    = new UserManager();
	private OrderManager orderMan  = new OrderManager();
	private OrderItemManager oiMan = new OrderItemManager();
	
	private ReceiptWriter writer = new ReceiptWriter();
	
	private GenericView<ItemDTO>  itemView;
	private GenericView<UserDTO>  userView;
	private GenericView<OrderDTO> orderView;
	private MainView 		   	  mainView;
	private CartView			  cartView = null;
	
	/**
	 * Creates and starts a new Controller, which handles
	 * one session of mediating interaction between the user
	 * and the database / logic.
	 */
	public Controller() {
		itemView = new GenericView<ItemDTO>(itemMan.getAll(), ItemDTO.class);
		userView = new GenericView<UserDTO>(userMan.getAll(), UserDTO.class);
		orderView = new GenericView<OrderDTO>(orderMan.getAll(), OrderDTO.class);
		mainView = new MainView();
		
		addListeners();
		
		mainView.setVisible(true);
	}
	
	/**
	 * Adds the needed listeners to all the non-changing views.
	 */
	private void addListeners() {
		itemView.attachChangedListener(new ChangeListener<ItemDTO>(itemMan, itemView, mainView, LabelType.ITEM));
		userView.attachChangedListener(new ChangeListener<UserDTO>(userMan, userView, mainView, LabelType.USER));
		orderView.attachChangedListener(new ChangeListener<OrderDTO>(orderMan, orderView, mainView, LabelType.ORDER));
		
		itemView.attachSelectionListener(new SelectionListener<ItemDTO>(itemView, mainView, LabelType.ITEM));
		userView.attachSelectionListener(new SelectionListener<UserDTO>(userView, mainView, LabelType.USER));
		orderView.attachSelectionListener(new SelectionListener<OrderDTO>(orderView, mainView, LabelType.ORDER));
		
		itemView.attachDeleteListener(new DeleteListener<ItemDTO>(itemMan, itemView, mainView, LabelType.ITEM));
		userView.attachDeleteListener(new DeleteListener<UserDTO>(userMan, userView, mainView, LabelType.USER));
		orderView.attachDeleteListener(new DeleteListener<OrderDTO>(orderMan, orderView, mainView, LabelType.ORDER));
		
		itemView.attachAddListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ItemDTO i = new ItemDTO(); // Add an empty item that the user customises
				i = itemMan.insert(i);
				itemView.addDataRow(i);
			}
		});
		userView.attachAddListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserDTO u = new UserDTO(); // Add an empty user that the user customises
				u = userMan.insert(u);
				userView.addDataRow(u);
			}
		});
		orderView.attachAddListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int userId; // We need a valid user id for a new order!
					userId = Integer.parseInt(JOptionPane.showInputDialog("Insert the user id for the order"));
					OrderDTO o = new OrderDTO();
					o.setUserId(userId);
					o = orderMan.insert(o);
					orderView.addDataRow(o);
				} catch (NumberFormatException e1) {
					; // Wrong format, or user simply clicked cancel; just ignore it
				} catch (HeadlessException e1) {
					e1.printStackTrace(); // User somehow has no keyboard, shouldn't happen
				}
			}
		});
		
		mainView.addItemButtonListener(e -> {
			itemView.setVisible(true);
		});
		mainView.addUserButtonListener(e -> {
			userView.setVisible(true);
		});
		mainView.addOrderButtonListener(e -> {
			orderView.setVisible(true);
		});
		mainView.addCartButtonListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(null != cartView) {
					cartView.dispose();
					cartView = null;
				}
				try {
					OrderDTO selectedOrder = orderView.getSelectedRowData();
					cartView = new CartView(oiMan.getByOrderId(selectedOrder.getId()),
											selectedOrder.getId());
					addCartViewListeners();
					cartView.setVisible(true);
				} catch (IndexOutOfBoundsException ex) {
					; // If we got here, it means we couldn't select a good Order
					// In that case, simply abort and that's it
				}
			}
		});
	}
	
	/**
	 * Adds the needed listeners to the currently active
	 * CartView, if any. This method should be called every time
	 * a new CartView is created.
	 */
	private void addCartViewListeners() {
		if(null != cartView) {
			cartView.attachAddListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						ItemDTO selectedItem = itemView.getSelectedRowData();
						int amount = Integer.parseInt(JOptionPane.showInputDialog("How many items?"));
						
						OrderItemDTO newCartElem = new OrderItemDTO();
						newCartElem.setItemId(selectedItem.getId());
						newCartElem.setOrderId(cartView.getOrderId());
						newCartElem.setAmount(amount);
						
						// De-stock the items that are now allocated to the Order
						ItemDTO i = itemMan.getById(newCartElem.getItemId());
						if(i.getStock() < newCartElem.getAmount()) { // Validate, very coarsely
							throw new IllegalArgumentException("Item " + i.getName() + " is under stocked!");
						}
						i.setStock(i.getStock() - newCartElem.getAmount());
						i = itemMan.update(i);
						itemView.updateData(i);
						
						// Update the system with the new OrderItem
						newCartElem = oiMan.insert(newCartElem);
						cartView.addDataRow(newCartElem);
						
						// Update the Order
						orderView.updateData(orderMan.getById(cartView.getOrderId()));
						
						// Update the MainView
						OrderDTO selectedOrder = orderView.getSelectedRowData();
						if(null == selectedOrder) mainView.setOrderLabel(null);
						else mainView.setOrderLabel(selectedOrder.toString());
					} catch (IndexOutOfBoundsException ex) {
						; // If we got here, it means we couldn't select a good Order
						// In that case, simply abort and that's it
					} catch (NumberFormatException ex) {
						; // If we got here, the user hit cancel or gave us a
						// gibberish amount; simply abort
					} catch (IllegalArgumentException ex) {
						// If we got here, it means we ran into a validation error
						JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			
			cartView.attachDeleteListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					OrderItemDTO toDelete = cartView.getSelectedRowData();
					
					// Re-stock the items that had been allocated to the Order
					ItemDTO i = itemMan.getById(toDelete.getItemId());
					i.setStock(i.getStock() + toDelete.getAmount());
					i = itemMan.update(i);
					itemView.updateData(i);
					
					// Update the system to account for the removal
					cartView.deleteDataRow(cartView.getSelectedRow());
					oiMan.delete(toDelete);

					// Update the Order
					orderView.updateData(orderMan.getById(cartView.getOrderId()));
					
					// Update the MainView
					OrderDTO selectedOrder = orderView.getSelectedRowData();
					if(null == selectedOrder) mainView.setOrderLabel(null);
					else mainView.setOrderLabel(selectedOrder.toString());
				}
			});
			
			cartView.attachReceiptListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					OrderDTO order = orderMan.getById(cartView.getOrderId());
					String userEmail = (userMan.getById(order.getUserId())).getEmail();
					List<OrderItemDTO> cartEntries = cartView.getAllData();
					
					writer.writeReceipt(userEmail, order, cartEntries);
				}
			});
		}
	}
}
