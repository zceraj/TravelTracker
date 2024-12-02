package dbProject;

import java.sql.*;

public class DatabaseManager {
  private static final String URL = "jdbc:mysql://localhost:3306/travel_tracker";
  private static final String USER = "root";
  private static final String PASSWORD = "password";
  private Connection connection;

  public DatabaseManager(String s, String root, String password) {
    try {
      connection = DriverManager.getConnection(URL, USER, PASSWORD);
      System.out.println("Connected to the database.");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Connection getConnection() {
    return connection;
  }

  public void closeConnection() {
    try {
      if (connection != null) connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
