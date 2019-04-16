package business.controller;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import business.managers.AbstractManager;
import presentation.GenericView;
import presentation.MainView;
import presentation.MainView.LabelType;

class ChangeListener<T> implements TableModelListener { // Package-visible
	private AbstractManager<T> man;
	private GenericView<T> view;
	private MainView mainView;
	private LabelType label;
	
	public ChangeListener(AbstractManager<T> man, GenericView<T> view, MainView mainView, LabelType label) {
		this.man = man;
		this.view = view;
		this.mainView = mainView;
		this.label = label;
	}

	public void tableChanged(TableModelEvent e) {
		// Update the changed element
		int row = e.getFirstRow(); // Only one row can change at once, so this is okay
		T elem = view.getDataAt(row);
		elem = man.update(elem);
		
		// Update the table
		view.setDataAt(row, elem);
		
		// Update the main view, if needed
		T t = view.getSelectedRowData();
		if(null == t) mainView.setLabel(null, label);
		else mainView.setLabel(t.toString(), label);
	}
}
