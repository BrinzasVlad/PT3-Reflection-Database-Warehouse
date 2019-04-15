package presentation;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainView extends JFrame {
	/**
	 * We have auto-generated it because JFrames want it, but it is not used
	 */
	private static final long serialVersionUID = 4555447427279664116L;
	
	public enum LabelType {
		ITEM,
		USER,
		ORDER
	}
	
	private JPanel mainPane;
	
	private JLabel selItem = new JLabel("No selected item");
	private JLabel selUser = new JLabel("No selected user");
	private JLabel selOrder = new JLabel("No selected order");
	
	private JButton bItem = new JButton("See items");
	private JButton bUser = new JButton("See users");
	private JButton bOrder = new JButton("See orders");
	private JButton bCart = new JButton("See order details");

	/**
	 * Creates a new MainView object that is initially
	 * invisible and must be made visible externally.
	 */
	public MainView() {
		initialize();
	}
	
	/**
	 * Attaches the given listener to the "See items" button, so
	 * that it will be activated whenever the user clicks that
	 * button.
	 * @param l - the ActionListener to attach
	 */
	public void addItemButtonListener(ActionListener l) {
		bItem.addActionListener(l);
	}
	/**
	 * Attaches the given listener to the "See users" button, so
	 * that it will be activated whenever the user clicks that
	 * button.
	 * @param l - the ActionListener to attach
	 */
	public void addUserButtonListener(ActionListener l) {
		bUser.addActionListener(l);
	}
	/**
	 * Attaches the given listener to the "See orders" button, so
	 * that it will be activated whenever the user clicks that
	 * button.
	 * @param l - the ActionListener to attach
	 */
	public void addOrderButtonListener(ActionListener l) {
		bOrder.addActionListener(l);
	}
	/**
	 * Attaches the given listener to the "See order details" button,
	 * so that it will be activated whenever the user clicks that
	 * button.
	 * @param l - the ActionListener to attach
	 */
	public void addCartButtonListener(ActionListener l) {
		bCart.addActionListener(l);
	}
	
	/**
	 * Sets the text for the label given by the provided
	 * LabelType to display given text. The text should
	 * represent the currently selected element.
	 * @param str - a string to be displayed
	 * @param which - a selector of which label to update
	 */
	public void setLabel(String str, LabelType which) {
		switch(which) {
			case ITEM: setItemLabel(str); break;
			case USER: setUserLabel(str); break;
			case ORDER: setOrderLabel(str); break;
		}
	}
	public void setItemLabel(String str) {
		if(null != str)
			selItem.setText("Selected item: " + str);
		else
			selItem.setText("No item selected");
	}
	public void setUserLabel(String str) {
		if(null != str)
			selUser.setText("Selected user: " + str);
		else
			selUser.setText("No user selected");
	}
	public void setOrderLabel(String str) {
		if(null != str)
			selOrder.setText("Selected order: " + str);
		else
			selOrder.setText("No order selected");
	}
	
	/**
	 * Arranges all of the view's elements and initialises
	 * any needed components.
	 */
	private void initialize() {
		mainPane = new JPanel(new GridLayout(0, 1));
		
		mainPane.add(selItem);
		mainPane.add(selUser);
		mainPane.add(selOrder);
		
		mainPane.add(bItem);
		mainPane.add(bUser);
		mainPane.add(bOrder);
		mainPane.add(bCart);
		
		add(mainPane);
		
		setLocationRelativeTo(null); // Center on screen
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(500, 300);
		setTitle("Main Menu");
	}
}
