package business.receipt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;

import dto.OrderDTO;
import dto.OrderItemDTO;

public class ReceiptWriter {
	private final String defaultDirectory;
	
	/**
	 * Creates a new ReceiptWriter with a default directory
	 * of "./receipts".
	 */
	public ReceiptWriter() {
		defaultDirectory = "./receipts";
	}
	
	/**
	 * Creates a new ReceiptWriter with the given
	 * default directory.
	 * @param defaultDir - a default directory that is
	 * 		suggested to the user when selecting where
	 * 		to save a receipt
	 */
	public ReceiptWriter(String defaultDir) {
		defaultDirectory = defaultDir;
	}
	
	/**
	 * Aggregates the given information into a receipt which
	 * is then stored as a .txt file at a location of the
	 * end user's choosing.
	 * @param clientName - the name that will be displayed
	 * 		for the client (e.g. if John buys 5 apples, then
	 * 		the client name is "John")
	 * @param order - the OrderDTO object associated with this
	 * 		receipt
	 * @param entries - a list of OrderItemDTO objects representing
	 * 		the entries in this order (e.g. in the above scenario,
	 * 		there would be an entry for apples)
	 */
	public void writeReceipt(String clientName, OrderDTO order, List<OrderItemDTO> entries) {
		JFileChooser f = new JFileChooser(defaultDirectory);
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.showSaveDialog(null);
        
        String receiptTitle = "Receipt order " + order.getId() + " - "
        					  + new SimpleDateFormat("dd MMM HH-mm-ss").format(new Date())
        					  + ".txt";
        //String receiptTitle = "receipt";
        File receiptFile = new File(f.getSelectedFile(), receiptTitle);
        
        try {
			receiptFile.createNewFile();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(receiptFile));
			writer.write("Receipt"); writer.newLine();
			writer.write("Order " + order.getId()); writer.newLine();
			writer.write("Date: " + new Date()); writer.newLine();
			writer.write("Client: " + clientName); writer.newLine();
			writer.newLine();
			for(OrderItemDTO entry : entries) {
				String entryWithDots = entry.getAmount() + " x " + entry.getItemName() + 
									   "...............................................................";
				writer.write(entryWithDots, 0, 40); writer.write(entry.getSubtotal().toString());
				writer.newLine();
			}
			writer.newLine();
			writer.write("Total: " + order.getTotalPrice());
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace(); // Something went wrong at IO; just let it go for now
		}
	}
}
