package business.controller;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import business.managers.AbstractManager;
import presentation.GenericView;

class ChangeListener<T> implements TableModelListener { // Package-visible
	private AbstractManager<T> man;
	private GenericView<T> view;
	
	public ChangeListener(AbstractManager<T> man, GenericView<T> view) {
		this.man = man;
		this.view = view;
	}

	public void tableChanged(TableModelEvent e) {
		// Update the changed element
		int row = e.getFirstRow(); // We neglect the case when multiple rows are changed at once!!
		T elem = view.getDataAt(row);
		elem = man.update(elem);
		
		// Update the table
		view.setDataAt(row, elem);
	}
}
