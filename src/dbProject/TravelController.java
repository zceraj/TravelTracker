package dbProject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dbProject.view.MainFrame;
import dbProject.view.screens.ExploreScreen;
import dbProject.view.screens.HomePage;
import dbProject.view.screens.LoginScreen;
import dbProject.view.screens.PlacesScreen;
import dbProject.view.screens.PlannedTripsScreen;
import dbProject.view.screens.WelcomeScreen;
import dbProject.view.screens.WishlistScreen;

public class TravelController {
  private Connection connection;
  private MainFrame frame;

  public TravelController() {
    frame = new MainFrame();
//    frame.setContentPane(new WelcomeScreen(this));
    initializeConnection("root", "Me0wmeow"); // TO-DO: DELETE
    frame.setContentPane(new WishlistScreen(this)); // TO-DO: DELETE
    frame.setVisible(true);
  }


  private boolean initializeConnection(String user, String password) {
    try {
      String url = "jdbc:mysql://localhost:3306/TravelTracker";
      // Establish connection
      connection = DriverManager.getConnection(url, user, password);
      System.out.println("Database connection established.");
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("Failed to establish database connection.");
      return false;
    }
  }

  // ------------------- AUTHENTICATION -------------------

  public boolean authenticateUser(String username, String password) {
    return initializeConnection(username, password); //initialize yay
  }

  // ------------------- EXPLORE SCREEN -------------------

  public List<String> getPlacesToExplore() {
    List<String> places = new ArrayList<>();
    String query = "SELECT city FROM places";

    try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
      while (rs.next()) {
        places.add(rs.getString("name"));
      }
    } catch (SQLException e) {
      e.printStackTrace();  // Handle exception properly
    }

    return places;
  }

  public Collection<Object> getFilteredPlaces(List<String> filters) {
    Collection<Object> places = new ArrayList<>();
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

  public List<String> getPlannedTrips(boolean completed) {
    List<String> trips = new ArrayList<>();
    String query = "SELECT p.name AS place_name FROM planned_trips pt JOIN places p ON pt.place_id = p.location_id WHERE pt.completed = ? ";

    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Set the completed parameter in the query
      stmt.setBoolean(1, completed);

      try (ResultSet rs = stmt.executeQuery()) {
        // Iterate through the result set and add place names to the trips list
        while (rs.next()) {
          trips.add(rs.getString("place_name"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace(); // Log the exception
    }

    return trips; // Return the list of trips
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

  public String getTripNotes(String tripName) {
    String additionalInfo = "";
    String query = " SELECT additional_information FROM planned_trips WHERE place_id = ( SELECT location_id FROM places WHERE name = ?)";

    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Set the trip name parameter
      stmt.setString(1, tripName);

      try (ResultSet rs = stmt.executeQuery()) {
        // Retrieve the additional information from the result set
        if (rs.next()) {
          additionalInfo = rs.getString("additional_information");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace(); // Log the exception
    }

    return additionalInfo.isEmpty() ? "No additional information available" : additionalInfo;
  }

  // ------------------- WISHLIST SCREEN -------------------

  public List<String> getWishlist() {
    List<String> wishlist = new ArrayList<>();
    String query = "SELECT places.name\n" +
            "FROM wishlist\n" +
            "JOIN places ON wishlist.location_id = places.location_id;\n";

    try (PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        wishlist.add(rs.getString("place_name"));
      }
    } catch (SQLException e) {
      e.printStackTrace(); // Log the exception
    }

    return wishlist; // Return the list of wishlist place names
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

  // ------------------SHOW DIFFERENT SCREENS ---------------


  public void showNext(String next){
    frame.remove(frame.getContentPane());
    switch (next) {
      case "login":
        frame.setContentPane(new LoginScreen(this));
        break;
      case "home":
        frame.setContentPane(new HomePage(this));
        break;
      case "explore":
        frame.setContentPane(new ExploreScreen(this));
        break;
      case "planned":
        frame.setContentPane(new PlannedTripsScreen(this));
        break;
      case "wishlist":
        frame.setContentPane(new WishlistScreen(this));
        break;
      case "logout":
        closeConnection();
        frame.setContentPane(new WelcomeScreen(this));
    }
    frame.revalidate();
    frame.repaint();
  }
}
