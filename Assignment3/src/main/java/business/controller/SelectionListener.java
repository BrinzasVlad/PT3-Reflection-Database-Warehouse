package business.controller;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import presentation.GenericView;
import presentation.MainView;
import presentation.MainView.LabelType;

class SelectionListener<T> implements ListSelectionListener { // Package-visible
	private GenericView<T> view;
	private MainView mainView;
	private LabelType label;
	
	public SelectionListener(GenericView<T> view, MainView mainView, LabelType label) {
		this.view = view;
		this.mainView = mainView;
		this.label = label;
	}
	
	public void valueChanged(ListSelectionEvent e) {
		T t = view.getSelectedRowData();
		mainView.setLabel(t.toString(), label);
	}
}
