package business.managers;

import java.util.List;

public interface AbstractManager<T> {
	/**
	 * Returns a list of all elements in an
	 * easy to display format.
	 * @return A list of easy-to-display elements
	 */
	public List<T> getAll();
	
	/**
	 * Returns the first element in the database
	 * that has the given id.
	 * @param id - the id to search by
	 * @return An easy-to-display element
	 */
	public T getById(int id);
	
	/**
	 * Updates the database with the data
	 * contained in the received element.
	 * This method is expected to modify the
	 * received element to reflect any possible
	 * changes upon update.
	 * @param t - the element to update
	 * @return The received element (possibly modified)
	 */
	public T update(T t);
	
	/**
	 * Deletes the entries in the database
	 * that correspond to the received element.
	 * @param t - the element to delete
	 */
	public void delete(T t);
	
	/**
	 * Inserts the element into the database
	 * and returns the same element, modified
	 * to reflect any changes applied upon insertion
	 * (for example the id received)
	 * @param t - the element to insert
	 * @return The received element (probably modified)
	 */
	public T insert(T t);
}
