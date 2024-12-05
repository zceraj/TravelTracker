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
  private int userId;

  public TravelController() {
    frame = new MainFrame();
//    frame.setContentPane(new WelcomeScreen(this));
    initializeConnection("root", "Me0wmeow"); // TO-DO: DELETE
    frame.setContentPane(new HomePage(this)); // TO-DO: DELETE
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
    boolean worked = initializeConnection(username, password);
    createUser(username, password);
    return worked;//initialize yay

  }

  public boolean createUser(String username, String password) {
    PreparedStatement statement = null;
    ResultSet generatedKeys = null;
    try {
      String sql = "INSERT INTO users (username, users_password) VALUES (?, ?)";
      statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

      // Set parameters for the statement
      statement.setString(1, username);
      statement.setString(2, password); // You may want to hash the password before storing it

      int rowsAffected = statement.executeUpdate();

      if (rowsAffected > 0) {
        // Get the generated user_id
        generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
          int userId = generatedKeys.getInt(1); // Get the first column of the generated keys (user_id)
          // You can now use the userId or store it in a variable, e.g., set it to a field or return it
          System.out.println("User created successfully with user_id: " + userId);
          this.userId = userId;
          return true; // User created successfully
        }
      }

      return false; // User creation failed
    } catch (SQLException e) {
      e.printStackTrace();
      return false; // Return false if there is a SQL exception
    } finally {
      try {
        if (generatedKeys != null) generatedKeys.close();
        if (statement != null) statement.close();
        if (connection != null) connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }


  // ------------------- EXPLORE SCREEN -------------------

  public List<String> getPlacesToExplore() {
    List<String> places = new ArrayList<>();
    String query = "SELECT city FROM places"; // You are selecting the city column

    try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
      while (rs.next()) {
        places.add(rs.getString("city")); // Fetch city, not places_name
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
      String query = "SELECT description FROM places WHERE places_name = ?";
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
      // Query to get the place_id for the given placeName
      String placeQuery = "SELECT location_id FROM places WHERE places_name = ?";
      PreparedStatement stmt = connection.prepareStatement(placeQuery);
      stmt.setString(1, placeName);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int placeId = rs.getInt("place_id");

        // Now insert the place_id into the planned_trips table
        String insertQuery = "INSERT INTO planned_trips (place_id, status) VALUES (?, 'in progress')";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
        insertStmt.setInt(1, placeId);  // Use place_id here
        insertStmt.executeUpdate();
        return true;
      } else {
        System.out.println("Place not added: " + placeName);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }


  public boolean addPlaceToWishlist(String placeName) {
    try {
      // Step 1: Retrieve place_id from places table based on placeName
      String placeQuery = "SELECT location_id FROM places WHERE place_name = ?";
      PreparedStatement placeStmt = connection.prepareStatement(placeQuery);
      placeStmt.setString(1, placeName);
      ResultSet rs = placeStmt.executeQuery();

      if (!rs.next()) {
        // If place doesn't exist, return false (could also handle as an error)
        return false;
      }

      int placeId = rs.getInt("location_id");

      // Step 2: Insert into wishlist table using userId and placeId
      String query = "INSERT INTO wishlist (user_id, place_id) VALUES (?, ?)";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setInt(1, userId);
      stmt.setInt(2, placeId);
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
    String query = "SELECT places.city\n" +
            "FROM wishlist\n" +
            "JOIN places ON wishlist.place_id = places.location_id;\n";

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

  // ------------------PLACES SCREENS ---------------

  public String[] getPlaceInfo(String placeName) {
    String[] placeInfo = new String[4];  // [countryName, rating, activities, food]

    String query = "SELECT p.country, p.calculated_rating, GROUP_CONCAT(a.activities_name) AS activities, p.food "
            + "FROM places p "
            + "LEFT JOIN place_activities pa ON p.location_id = pa.place_id "
            + "LEFT JOIN activities a ON pa.activity_id = a.activity_id "
            + "WHERE p.places_name = ? "
            + "GROUP BY p.location_id";

    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, placeName);  // Set the placeName parameter

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          // Retrieve data from ResultSet
          placeInfo[0] = rs.getString("country");
          placeInfo[1] = String.valueOf(rs.getFloat("calculated_rating"));
          placeInfo[2] = rs.getString("activities");
          placeInfo[3] = rs.getString("food");
        } else {
          // If place not found, return null values or default values
          placeInfo[0] = "Unknown Country";
          placeInfo[1] = "0";
          placeInfo[2] = "No activities available";
          placeInfo[3] = "No food information available";
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      placeInfo[0] = "Error fetching data";
      placeInfo[1] = "Error";
      placeInfo[2] = "Error";
      placeInfo[3] = "Error";
    }

    return placeInfo;
  }

  // ------------------SHOW DIFFERENT SCREENS ---------------


  public void showNext(String next) {
    frame.remove(frame.getContentPane());

    // Split the input string by spaces
    String[] words = next.split(" ");

    // Check if the first word is "place"
    if (words[0].equals("place")) {
      if (words.length > 1) {
        String place = words[1];
        // Handle the case where place is a valid location
        // You might want to validate or process the place here
        System.out.println("Place selected: " + place);
        // Call a method to show details for the selected place (example below)
        frame.setContentPane(new PlacesScreen(place, this));
      } else {
        System.out.println("Invalid place input");
      }
    } else {
      // Handle the regular cases based on the first word
      switch (words[0]) {
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
          break;
        default:
          System.out.println("Unknown action");
          break;
      }
    }

    frame.revalidate();
    frame.repaint();
  }
}
