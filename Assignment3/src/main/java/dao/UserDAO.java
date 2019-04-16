package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import connection.ConnectionFactory;
import model.User;

public class UserDAO extends AbstractDAO<User> {
	public UserDAO() {
		super(User.class);
	}
	
	/**
	 * Finds a user based on the given e-mail address.
	 * Note that this method assumes that there is at
	 * most one user with this e-mail. Otherwise, the
	 * first one encountered will be returned. If no
	 * matching users are found, this method returns null.
	 * @param email - the e-mail to search by
	 * @return The user, if found, otherwise null
	 */
	public User findByEmail(String email) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("email");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, email);
			resultSet = statement.executeQuery();

			List<User> users = createObjects(resultSet);
			return users.isEmpty() ? null : users.get(0);
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
