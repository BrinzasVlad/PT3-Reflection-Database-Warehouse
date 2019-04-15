package presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;


public class GenericView<T> extends JFrame {
	/**
	 * This value has been auto-generated, but is not really
	 * needed, since we don't plan on serializing this class
	 */
	private static final long serialVersionUID = -6301779182891662854L;
	
	
	private final Class<?> type;
	
	private JPanel mainPane;
	
	private JScrollPane tablePane;
	private GenericTableModel tableModel;
	private JTable table;
	
	private JPanel buttonPane;
	private JButton deleteButton;
	private JButton addButton;
	
	/**
	 * Creates a new GenericView initially showing the
	 * given data. The new view will initially be invisible
	 * and must be made visible externally.
	 * @param data - a list of elements to be displayed
	 * @deprecated It is recommended to use the two-argument
	 * 		constructor, which is far more stable. This
	 * 		constructor is only provided for educational purposes.
	 * 		If one must use this one-argument constructor, make
	 * 		sure that the input data list is non-empty!
	 */
	public GenericView(List<T> data) {
		type = data.get(0).getClass();

		initialize(data);
	}
	
	/**
	 * Creates a new GenericView initially showing the
	 * given data. The new view will initially be invisible
	 * and must be made visible externally.
	 * @param data - a list of elements to be displayed
	 * @param type - the type of the elements displayed (e.g.
	 * 			for a list of ItemDTO objects, this would be
	 * 			ItemDTO.class)
	 */
	public GenericView(List<T> data, Class<?> type) {
		this.type = type;
		
		initialize(data);
	}
	
	/**
	 * Attaches the given listener to the data table, so that
	 * it will be activated whenever an entry in the view changes.
	 * @param l - the TableModelListener to attach
	 */
	public void attachChangedListener(TableModelListener l) {
		tableModel.addTableModelListener(l);
	}
	
	/**
	 * Attaches the given listener to the data table, so that
	 * it will be activated whenever the selection in the view
	 * changes.
	 * @param l - the ListSelectionListener to attach
	 */
	public void attachSelectionListener(ListSelectionListener l) {
		table.getSelectionModel().addListSelectionListener(l);
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
	 * Returns the currently selected row in the view's table.
	 * @return The number of the selected row
	 */
	public int getSelectedRow() {
		return table.getSelectedRow();
	}
	
	/**
	 * Returns the data associated to the currently
	 * selected row in the view's table.
	 * @return The currently selected element
	 */
	public T getSelectedRowData() {
		return getDataAt(table.getSelectedRow());
	}
	
	/**
	 * Returns the data stored at the specified row
	 * of the view's table.
	 * @param row - the row number
	 * @return The element at the given row
	 */
	public T getDataAt(int row) {
		return tableModel.getDataAtRow(row);
	}
	
	/**
	 * Updates the element(s) in the view's table
	 * that have the same "id" field as the given
	 * element to the given element's value.
	 * @param data - the element to update
	 */
	public void updateData(T data) {
		tableModel.updateData(data);
		table.repaint();
	}
	
	/**
	 * Deletes the first entry that matches the
	 * given element from the view's table.
	 * @param data - the element to delete
	 */
	public void deleteData(T data) {
		tableModel.deleteData(data);
		table.repaint();
	}
	
	/**
	 * Sets the data at the given row of the view's
	 * table to match that of the given element.<br>
	 * This operation also refreshes the table,
	 * as per {@link #refresh()}.
	 * @param row - a row number
	 * @param data - the new data
	 */
	public void setDataAt(int row, T data) {
		tableModel.setDataAtRow(row, data);
		table.repaint();
	}
	
	/**
	 * Inserts a new row into the view's table
	 * storing the given element.<br>
	 * This operation also refreshes the table,
	 * as per {@link #refresh()}.
	 * @param data - the element to add
	 */
	public void addDataRow(T data) {
		tableModel.addDataRow(data);
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
	 * Re-paints the view's table, displaying any
	 * possible modification to the internal value
	 * that have not yet been displayed.
	 */
	public void refresh() {
		table.repaint(); // Mind you, this will *not* re-read the data!
	}
	
	/**
	 * Arranges all of the view's elements, initialises
	 * any needed components and populates the view's table
	 * with the given data
	 * @param data - a list of elements to populate the
	 * 		view's table with
	 */
	private void initialize(List<T> data) {
		mainPane = new JPanel(new BorderLayout());
		
		tableModel = new GenericTableModel(data, type);
		table = new JTable(tableModel);
		table.setFillsViewportHeight(true);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		tablePane = new JScrollPane(table);
		mainPane.add(tablePane, BorderLayout.CENTER);
		
		deleteButton = new JButton("Delete");
		addButton = new JButton("New");
		
		buttonPane = new JPanel(new GridLayout(1, 0));
		buttonPane.add(deleteButton);
		buttonPane.add(addButton);
		mainPane.add(buttonPane, BorderLayout.SOUTH);
		
		add(mainPane);
		setLocationRelativeTo(null); // Center on screen
		setDefaultCloseOperation(HIDE_ON_CLOSE); // We close the program from the main window
		
		setSize(700, 500);
		setTitle(type.getSimpleName() + " View");
	}
	
	private class GenericTableModel extends AbstractTableModel {
		/**
		 * Also required, also non-needed.
		 */
		private static final long serialVersionUID = -8682578178567079570L;

		private Field[] fields;
		private Class<?> type;
		
		private final List<String> columnNames = new ArrayList<String>();
		private List<T> data;
		
		public GenericTableModel(List<T> data, Class<?> type) {
			this.data = data;
			this.type = type;
			
			// Use reflection to generate the column names
			fields = type.getDeclaredFields();
			for(Field f : fields) {
				columnNames.add(f.getName());
			}
		}
		
		public int getColumnCount() {
            return columnNames.size();
        }

        public int getRowCount() {
            return data.size();
        }
        
        public String getColumnName(int col) {
            return columnNames.get(col);
        }
        
        public T getDataAtRow(int row) {
        	return data.get(row);
        }
        
        public void setDataAtRow(int row, T elem) {
        	data.set(row, elem);
        	//fireTableDataChanged();
        }
        
        public void addDataRow(T elem) {
        	data.add(elem);
        	//fireTableDataChanged();
        }
        
        public void deleteDataRow(int row) {
        	data.remove(row);
        	//fireTableDataChanged();
        }
        
        public void updateData(T elem) {
    		try {
				PropertyDescriptor idDescr = new PropertyDescriptor("id", type);
				Method getId = idDescr.getReadMethod();
	        	for(int i = 0; i < data.size(); ++i) {
	        			T t = data.get(i);
						int id = (Integer) getId.invoke(t);
						int dataId = (Integer) getId.invoke(elem);
						if(id == dataId) data.set(i, elem);
	        	}
			} catch (IntrospectionException e) { // TODO: catches
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
        }
        
        public void deleteData(T data) {
        	this.data.remove(data);
        }

        public Object getValueAt(int row, int col) {
        	Object result = new Object();
        	
            boolean temp = fields[col].isAccessible();
            fields[col].setAccessible(true);
            try {
				result = fields[col].get(data.get(row));
			} catch (IllegalArgumentException e) { // TODO: handle catch blocks, as always!
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
            fields[col].setAccessible(temp);
            
            return result;
        }
        
        public Class<?> getColumnClass(int col) {
        	return toWrapper(fields[col].getType());
        }
        
        public boolean isCellEditable(int row, int col) {
            if(0 == col) return false;
            else return true;
        }
        
        public void setValueAt(Object value, int row, int col) {
        	
            boolean temp = fields[col].isAccessible();
            fields[col].setAccessible(true);
            try {
				fields[col].set(data.get(row), value);
			} catch (IllegalArgumentException e) { // TODO: handle catch blocks, as always!
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
            fields[col].setAccessible(temp);
            
            fireTableCellUpdated(row, col);
        }
        
        private Class<?> toWrapper(Class<?> clazz) {
            if (!clazz.isPrimitive())
                return clazz;

            if (clazz == Integer.TYPE)
                return Integer.class;
            if (clazz == Long.TYPE)
                return Long.class;
            if (clazz == Boolean.TYPE)
                return Boolean.class;
            if (clazz == Byte.TYPE)
                return Byte.class;
            if (clazz == Character.TYPE)
                return Character.class;
            if (clazz == Float.TYPE)
                return Float.class;
            if (clazz == Double.TYPE)
                return Double.class;
            if (clazz == Short.TYPE)
                return Short.class;
            if (clazz == Void.TYPE)
                return Void.class;

            return clazz;
        }
	}
	
}
