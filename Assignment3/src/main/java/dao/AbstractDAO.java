package dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import connection.ConnectionFactory;

/**
 * @Author: Technical University of Cluj-Napoca, Romania Distributed Systems
 *          Research Laboratory, http://dsrl.coned.utcluj.ro/
 * @Since: Apr 03, 2017
 * @Source http://www.java-blog.com/mapping-javaobjects-database-reflection-generics
 */
public class AbstractDAO<T> {
	protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

	private final Class<?> type;

	//@SuppressWarnings("unchecked") // It's good to see all of your warnings so you can fix them someday
	public AbstractDAO() {
		this.type = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		// VERY VERY IMPORTANT NOTE: BECAUSE OF THIS LINE, YOU ABSOLUTELY
		// CANNOT INSTANTIATE AN ACTUAL AbstractDAO<T> OBJECT, ELSE IT WILL CRASH!
		// ...
		// Unless you use the other constructor, that is.
	}
	
	public AbstractDAO(Class<?> type) {
		this.type = type;
	}

	/**
	 * Creates an SQL query of the type 		  <br>
	 * <b>SELECT</b> * <b>FROM</b> &lt;table&gt;  <br>
	 *  <b>WHERE</b> &lt;field&gt; = ?			  <br>
	 * to be used for creating a {@link PreparedStatement}
	 * @param field - the name of the field to be used in
	 * 				  the WHERE clause
	 * @return A String containing the SELECT query
	 */
	protected String createSelectQuery(String field) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append("`" + type.getSimpleName() + "`");
		sb.append(" WHERE " + field + " =?");
		return sb.toString();
	}
	
	/**
	 * Creates an SQL query of the type  		<br>
	 * <b>INSERT</b> (&lt;column names&gt;)     <br>
	 * <b>INTO</b> &lt;table&gt; 				<br>
	 * <b>VALUES</b> (&lt;? to be filled&gt;)   <br>
	 * to be used for creating a {@link PreparedStatement}
	 * @return A String containing the INSERT query
	 */
	protected String createInsertQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append("`" + type.getSimpleName() + "`");
		
		// Generate the list of column names reflexively. Also prepare the question marks in advance.
		StringJoiner cols = new StringJoiner(", ", " (", ") ");
		StringJoiner questionMarks = new StringJoiner(", ", " (", ") ");
		for (Field field : type.getDeclaredFields()) {
			cols.add(field.getName());
			questionMarks.add("?");
		}
		sb.append(cols.toString());
		
		sb.append(" VALUES ");
		sb.append(questionMarks.toString());
		
		return sb.toString();
	}
	
	/**
	 * Creates an SQL query of the type  	<br>
	 * <b>UPDATE</b> &lt;table&gt;			<br>
	 * <b>SET</b> (&lt;param&gt; = ?, ...) 	<br>
	 * <b>WHERE</b> &lt;field name&gt; = ?  <br>
	 * to be used for creating a {@link PreparedStatement}
	 * @param whereFieldName - name of the field to be checked for
	 * 						   in the WHERE clause
	 * @return A String containing the UPDATE query
	 */
	protected String createUpdateQuery(String whereFieldName) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append("`" + type.getSimpleName() + "`");
		sb.append(" SET ");
		
		// Generate the list of column names reflexively.
		StringJoiner cols = new StringJoiner(", ");
		for (Field field : type.getDeclaredFields()) {
			cols.add(field.getName() + " = ?");
		}
		sb.append(cols.toString());
		
		sb.append(" WHERE ");
		sb.append(whereFieldName);
		sb.append(" = ?");
		
		return sb.toString();
	}
	
	/**
	 * Creates an SQL query of the type  	<br>
	 * <b>DELETE FROM</b> &lt;table&gt;     <br>
	 * <b>WHERE</b> &lt;field name&gt; = ?  <br>
	 * to be used for creating a {@link PreparedStatement}
	 * @param whereFieldName - name of the field to be checked for
	 * 						   in the WHERE clause
	 * @return A String containing the DELETE query
	 */
	protected String createDeleteQuery(String whereFieldName) {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ");
		sb.append("`" + type.getSimpleName() + "`");
		
		sb.append(" WHERE ");
		sb.append(whereFieldName);
		sb.append(" = ?");
		
		return sb.toString();
	}

	/**
	 * Finds all the elements of this type in the database.
	 * Essentially, selects all the elements from the associated table.
	 * @return A list containing all the elements found
	 */
	public List<T> findAll() {
		// We create the query string here because it will never be re-used
		// We could create it in a method, then feed that method to createSelectQuery,
		// but I think that's excessively complicated.
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append("`" + type.getSimpleName() + "`");
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = sb.toString();
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();

			return createObjects(resultSet);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	/**
	 * Finds an element of the corresponding type in the database
	 * given its id. If no elements are found with the given id,
	 * this method will return null.
	 * @param id - the id of the element to be searched for
	 * @return The element, if found, otherwise null
	 */
	public T findById(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("id");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();

			List<T> elems = createObjects(resultSet);
			return elems.isEmpty() ? null : elems.get(0);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	/**
	 * Inserts the given element into the database. This method will
	 * update the element's id field so that it matches the one it
	 * gained upon being inserted
	 * @param t - the element to be inserted
	 * @return The same element that was inserted (with updated id)
	 */
	public T insert(T t) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createInsertQuery();
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			// Fill in the fields
			try {
				int currentIndex = 1;
				for (Field field : type.getDeclaredFields()) {
					field.setAccessible(true);
					Object fieldValue = field.get(t);
					statement.setObject(currentIndex++, fieldValue);
				}
			} catch (SecurityException e) { // TODO: don't just catch everything and do nothing!
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			
			statement.executeUpdate();
			
			// Update the id value, since it is auto-generated!
			ResultSet generatedKeys = statement.getGeneratedKeys();
			generatedKeys.next();
			int newId = (int) generatedKeys.getLong(1);
			
			try {
				PropertyDescriptor idDescr = new PropertyDescriptor("id", type); // We presume this field exists!!
				Method setId = idDescr.getWriteMethod();
				setId.invoke(t, newId);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		
		return t;
	}

	/**
	 * Updates the database version of the given element so that it
	 * matches the one received as parameter. Note that this method
	 * assumes that an element with the same id as the one received
	 * exists in the database.
	 * @param t - the element to be updated in the database
	 * @return The same element that was updated
	 */
	public T update(T t) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createUpdateQuery("id"); // Update objects by id by default
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			
			// Fill in the fields
			int currentIndex = 1;
			try {
				for (Field field : type.getDeclaredFields()) {
					field.setAccessible(true);
					Object fieldValue = field.get(t);
					statement.setObject(currentIndex++, fieldValue);
				}
			} catch (SecurityException e) { // TODO: don't just catch everything and do nothing!
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			
			try {
				PropertyDescriptor idDescr = new PropertyDescriptor("id", type); // We presume this field exists!!
				Method getId = idDescr.getReadMethod();
				Object id = getId.invoke(t);
				statement.setObject(currentIndex, id);
			} catch(IntrospectionException e) { // TODO: do something on catch!
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		
		return t;
	}
	
	/**
	 * Deletes the given element from the database. Note that this
	 * method assumes that an element with the same id as the one
	 * received exists in the database.
	 * @param t - the element to be deleted from the database
	 */
	public void delete(T t) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createDeleteQuery("id"); // Delete objects by id by default
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			
			try {
				PropertyDescriptor idDescr = new PropertyDescriptor("id", type); // We presume this field exists!!
				Method getId = idDescr.getReadMethod();
				Object id = getId.invoke(t);
				statement.setObject(1, id);
			} catch(IntrospectionException e) { // TODO: do something on catch!
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			statement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
	}

	/**
	 * Uses reflection to convert the {@link ResultSet} received into
	 * a list of objects of the corresponding type.
	 * @param resultSet - the ResultSet to be unpacked
	 * @return A List of objects of the corresponding type
	 */
	protected List<T> createObjects(ResultSet resultSet) {
		List<T> list = new ArrayList<T>();

		try {
			while (resultSet.next()) {
				T instance = (T) type.newInstance(); // We get one warning here in exchange for lots of peace
				for (Field field : type.getDeclaredFields()) {
					Object value = resultSet.getObject(field.getName());
					field.setAccessible(true);
					field.set(instance, value);
				}
				list.add(instance);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
