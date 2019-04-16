package business.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import business.managers.AbstractManager;
import presentation.GenericView;
import presentation.MainView;
import presentation.MainView.LabelType;

class DeleteListener<T> implements ActionListener {
	private AbstractManager<T> man;
	private GenericView<T> view;
	private MainView mainView;
	private LabelType label;
	
	public DeleteListener(AbstractManager<T> man, GenericView<T> view, MainView mainView, LabelType label) {
		this.man = man;
		this.view = view;
		this.mainView = mainView;
		this.label = label;
	}
	
	public void actionPerformed(ActionEvent e) {
		// Update our view
		T t = view.getSelectedRowData();
		view.deleteDataRow(view.getSelectedRow());
		man.delete(t);
		
		// Update the main view
		t = view.getSelectedRowData();
		if(null == t) mainView.setLabel(null, label);
		else mainView.setLabel(t.toString(), label);
	}
}
