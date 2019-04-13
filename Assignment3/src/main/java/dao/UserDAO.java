package dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import connection.ConnectionFactory;
import model.User;

public class UserDAO extends AbstractDAO<User> {
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
	
//	public User updateByEmail(User user) {
//		Connection connection = null;
//		PreparedStatement statement = null;
//		String query = createUpdateQuery("email");
//		try {
//			connection = ConnectionFactory.getConnection();
//			statement = connection.prepareStatement(query);
//			
//			// Fill in the fields
//			int currentIndex = 1;
//			try {
//				for (Field field : User.class.getDeclaredFields()) {
//					field.setAccessible(true);
//					Object fieldValue = field.get(user);
//					statement.setObject(currentIndex++, fieldValue);
//				}
//			} catch (SecurityException e) { // TODO: don't just catch everything and do nothing!
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			}
//			
//			try {
//				PropertyDescriptor emailDescr = new PropertyDescriptor("email", User.class);
//				Method getEmail = emailDescr.getReadMethod();
//				Object email = getEmail.invoke(user);
//				statement.setObject(currentIndex, email);
//			} catch(IntrospectionException e) { // TODO: do something on catch!
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				e.printStackTrace();
//			}
//
//			statement.executeUpdate();
//		} catch (SQLException e) {
//			LOGGER.log(Level.WARNING, "UserDAO:insert " + e.getMessage());
//		} finally {
//			ConnectionFactory.close(statement);
//			ConnectionFactory.close(connection);
//		}
//		
//		return user;
//	}
}
