package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import connection.ConnectionFactory;
import model.Item;

public class ItemDAO extends AbstractDAO<Item> {
	/**
	 * Finds an Item in the database given its name. Note that
	 * this method assumes that at most one such item exists.
	 * If multiple objects with the same name are found, the
	 * first one encountered will be returned. If no such elements
	 * are found, the method will return null.
	 * @param name - the name of the Item to find
	 * @return The item, if found, otherwise null
	 */
	public Item findByName(String name) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("name");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, name);
			resultSet = statement.executeQuery();

			List<Item> items = createObjects(resultSet);
			return items.isEmpty() ? null : items.get(0);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "ItemDAO:findByName " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}
}
