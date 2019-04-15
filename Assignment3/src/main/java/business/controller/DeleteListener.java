package business.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import business.managers.AbstractManager;
import presentation.GenericView;

class DeleteListener<T> implements ActionListener {
	private AbstractManager<T> man;
	private GenericView<T> view;
	
	public DeleteListener(AbstractManager<T> man, GenericView<T> view) {
		this.man = man;
		this.view = view;
	}
	
	public void actionPerformed(ActionEvent e) {
		T t = view.getSelectedRowData();
		view.deleteDataRow(view.getSelectedRow());
		man.delete(t);
	}
}
