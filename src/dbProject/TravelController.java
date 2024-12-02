package dbProject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TravelController {
  private Connection connection;

  public TravelController() {
    try {
      // Establish database connection (adjust credentials as needed)
      String url = "jdbc:mysql://localhost:3306/travel_tracker";
      String username = "root";
      String password = "password";
      connection = DriverManager.getConnection(url, username, password);
      System.out.println("Database connection established.");
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to connect to the database.");
    }
  }

  // ------------------- AUTHENTICATION -------------------

  public boolean authenticateUser(String username, String password) {
    try {
      String query = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, username);
      stmt.setString(2, password);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt(1) > 0; // User exists
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // ------------------- EXPLORE SCREEN -------------------

  public List<String> getPlacesToExplore() {
    List<String> places = new ArrayList<>();
    try {
      String query = "SELECT name FROM places";
      PreparedStatement stmt = connection.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        places.add(rs.getString("name"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return places;
  }

  public String getPlaceDetails(String placeName) {
    try {
      String query = "SELECT description FROM places WHERE name = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, placeName);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getString("description");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return "Details not found.";
  }

  public boolean addPlaceToPlannedTrips(String placeName) {
    try {
      String query = "INSERT INTO planned_trips (place_name, status) VALUES (?, 'in progress')";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, placeName);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean addPlaceToWishlist(String placeName) {
    try {
      String query = "INSERT INTO wishlist (place_name) VALUES (?)";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, placeName);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // ------------------- PLANNED TRIPS SCREEN -------------------

  public List<String> getPlannedTrips() {
    List<String> trips = new ArrayList<>();
    try {
      String query = "SELECT place_name FROM planned_trips WHERE status = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, status);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        trips.add(rs.getString("place_name"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return trips;
  }

  public boolean markTripAsCompleted(String placeName) {
    try {
      String query = "UPDATE planned_trips SET status = 'completed' WHERE place_name = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, placeName);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean deletePlannedTrip(String placeName) {
    try {
      String query = "DELETE FROM planned_trips WHERE place_name = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, placeName);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // **NEW**: Edit a Planned Trip
  public boolean editPlannedTrip(String oldPlaceName, String newPlaceName) {
    try {
      String query = "UPDATE planned_trips SET place_name = ? WHERE place_name = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, newPlaceName);
      stmt.setString(2, oldPlaceName);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // **NEW**: Toggle a Planned Trip's Completion Status
  public boolean toggleTripCompletion(String placeName) {
    try {
      String query = "SELECT status FROM planned_trips WHERE place_name = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, placeName);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        String currentStatus = rs.getString("status");
        String newStatus = currentStatus.equalsIgnoreCase("completed") ? "in progress" : "completed";

        String updateQuery = "UPDATE planned_trips SET status = ? WHERE place_name = ?";
        PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
        updateStmt.setString(1, newStatus);
        updateStmt.setString(2, placeName);
        updateStmt.executeUpdate();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // ------------------- WISHLIST SCREEN -------------------

  public List<String> getWishlist() {
    List<String> wishlist = new ArrayList<>();
    try {
      String query = "SELECT place_name FROM wishlist";
      PreparedStatement stmt = connection.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        wishlist.add(rs.getString("place_name"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return wishlist;
  }

  public boolean moveWishlistToPlanned(String placeName) {
    if (addPlaceToPlannedTrips(placeName)) {
      return deleteWishlistPlace(placeName);
    }
    return false;
  }

  public boolean deleteWishlistPlace(String placeName) {
    try {
      String query = "DELETE FROM wishlist WHERE place_name = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, placeName);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  // ------------------- CLOSE CONNECTION -------------------

  public void closeConnection() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
        System.out.println("Database connection closed.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
