package presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import dto.OrderItemDTO;

public class CartView extends JFrame {
	private static final long serialVersionUID = 3101400345731002304L;
	
	private int orderId;

	private JPanel mainPane;
	private JScrollPane tablePane;
	private JTable table;
	private CartTableModel tableModel;
	private JPanel buttonPane;
	private JButton deleteButton;
	private JButton addButton;
	private JButton receiptButton;
	
	/**
	 * Creates a new CartView displaying the given
	 * data pertaining to the order associated to the
	 * provided id.The new view will initially be invisible
	 * and must be made visible externally.
	 * @param data - a list of entries associated to the order
	 * @param orderId - the order's id
	 */
	public CartView(List<OrderItemDTO> data, int orderId) {
		this.orderId = orderId;
		
		initialize(data);
	}
	
	/**
	 * Returns the id of the order associated
	 * with this CartView.
	 * @return An order id
	 */
	public int getOrderId() {
		return orderId;
	}
	
	/**
	 * Attaches the given listener to the "Delete" button, so
	 * that it will be activated whenever the user clicks that
	 * button.
	 * @param l - the ActionListener to attach
	 */
	public void attachDeleteListener(ActionListener l) {
		deleteButton.addActionListener(l);
	}
	
	/**
	 * Attaches the given listener to the "New" button, so
	 * that it will be activated whenever the user clicks that
	 * button.
	 * @param l - the ActionListener to attach
	 */
	public void attachAddListener(ActionListener l) {
		addButton.addActionListener(l);
	}
	
	/**
	 * Attaches the given listener to the "Generate Receipt" button,
	 * so that it will be activated whenever the user clicks that
	 * button.
	 * @param l - the ActionListener to attach
	 */
	public void attachReceiptListener(ActionListener l) {
		receiptButton.addActionListener(l);
	}
	
	/**
	 * Inserts a new row into the view's table
	 * storing the given element.<br>
	 * This operation also refreshes the table,
	 * as per {@link #refresh()}.
	 * @param data - the element to add
	 */
	public void addDataRow(OrderItemDTO dto) {
		tableModel.addDataRow(dto);
		table.repaint();
	}
	
	/**
	 * Deletes the specified row from the view's table.<br>
	 * This operation also refreshes the table,
	 * as per {@link #refresh()}.
	 * @param row - a row number
	 */
	public void deleteDataRow(int row) {
		tableModel.deleteDataRow(row);
		table.repaint();
	}
	
	/**
	 * Returns the currently selected row in the view's table.
	 * Returns -1 if no row is selected
	 * @return The number of the selected row or -1
	 */
	public int getSelectedRow() {
		int row = table.getSelectedRow();
		if(row < 0 || row >= table.getRowCount()) return -1; // Garbage data, somehow, or unselected
		else return row;
	}
	
	/**
	 * Returns the data associated to the currently
	 * selected row in the view's table or null if
	 * there is no currently selected row.
	 * @return The currently selected element or null
	 */
	public OrderItemDTO getSelectedRowData() {
		if(-1 == getSelectedRow()) return null;
		else return tableModel.getValueAtRow(getSelectedRow());
	}
	
	/**
	 * Returns a list containing all of the data
	 * stored in the view's table.
	 * @return A list storing all the elements
	 */
	public List<OrderItemDTO> getAllData() {
		return tableModel.getAllData();
	}
	
	/**
	 * Re-paints the view's table, displaying any
	 * possible modification to the internal value
	 * that have not yet been displayed.
	 */
	public void refresh() {
		table.repaint();
	}
	
	/**
	 * Arranges all of the view's elements, initialises
	 * any needed components and populates the view's table
	 * with the given data
	 * @param data - a list of elements to populate the
	 * 		view's table with
	 */
	private void initialize(List<OrderItemDTO> data) {
		mainPane = new JPanel(new BorderLayout());
		
		tableModel = new CartTableModel(data);
		table = new JTable(tableModel);
		table.setFillsViewportHeight(true);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		tablePane = new JScrollPane(table);
		mainPane.add(tablePane, BorderLayout.CENTER);
		
		deleteButton = new JButton("Delete");
		addButton = new JButton("New");
		receiptButton = new JButton("Generate Receipt");
		
		buttonPane = new JPanel(new GridLayout(1, 0));
		buttonPane.add(deleteButton);
		buttonPane.add(addButton);
		buttonPane.add(receiptButton);
		mainPane.add(buttonPane, BorderLayout.SOUTH);
		
		add(mainPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		setSize(700, 500);
		setTitle("Viewing Order " + orderId);
	}
	
	private class CartTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -600475350400234826L;
		
		private final String[] columnNames = {
				"id",
				"orderId",
				"itemName",
				"amount",
				"subtotal"
		};
		private List<OrderItemDTO> data;
		
		public CartTableModel(List<OrderItemDTO> data) {
			this.data = data;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.size();
		}
		
		public String getColumnName(int col) {
			return columnNames[col];
		}
		
		public OrderItemDTO getValueAtRow(int row) {
			return data.get(row);
		}
		
		public List<OrderItemDTO> getAllData() {
			return data;
		}
        
        public void addDataRow(OrderItemDTO elem) {
        	data.add(elem);
        	//fireTableDataChanged();
        }
        
        public void deleteDataRow(int row) {
        	data.remove(row);
        	//fireTableDataChanged();
        }

		public Object getValueAt(int row, int col) {
			switch(col) {
				case 0: return data.get(row).getId();
				case 1: return data.get(row).getOrderId();
				case 2: return data.get(row).getItemName();
				case 3: return data.get(row).getAmount();
				case 4: return data.get(row).getSubtotal();
				default: return "ERROR";
			}
		}
		
		public Class<?> getColumnClass(int col) {
			switch(col) {
				case 0: return Integer.class; // Id
				case 1: return Integer.class; // OrderId
				case 2: return String.class; // ItemName
				case 3: return Integer.class; // Amount
				case 4: return BigDecimal.class; // Subtotal
				default: return null;
			}
        }
		
		public boolean isCellEditable(int row, int col) {
//            if(3 == col) return true; // We can only edit the amount for an item and nothing else
//            else return false;
			return false;
        }
		
		public void setValueAt(Object value, int row, int col) {
			if(3 == col) { // Only the amount can be edited!
				data.get(row).setAmount((Integer) value);
				fireTableCellUpdated(row, col);
			}
		}
	}
}
