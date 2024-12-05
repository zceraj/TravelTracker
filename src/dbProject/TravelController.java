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
    String query = "SELECT places_name FROM places";

    try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
      while (rs.next()) {
        places.add(rs.getString("places_name"));
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
      String placeQuery = "SELECT places_name FROM places WHERE places_name = ?";
      PreparedStatement stmt = connection.prepareStatement(placeQuery);
      stmt.setString(1, placeName);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int placeId = rs.getInt("location_id");  // Corrected column name here

        // Now insert the place_id into the planned_trips table
        String insertQuery = "INSERT INTO planned_trips (places_name, completed) VALUES (?, 'in progress')";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
        insertStmt.setInt(1, placeId);  // Use place_id here
        insertStmt.executeUpdate();
        return true;
      } else {
        System.out.println("No place found with name: " + placeName);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    System.out.println("Place not added: " + placeName);
    return false;
  }



  // ------------------- PLANNED TRIPS SCREEN -------------------

  public List<String> getPlannedTrips(boolean completed) {
    List<String> trips = new ArrayList<>();
    String query = "SELECT p.places_name AS place_name FROM planned_trips pt JOIN places p ON pt.place_id = p.location_id WHERE pt.completed = ? ";

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
      String query = "UPDATE planned_trips SET completed = 'completed' WHERE place_name = ?";
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
    // Step 1: Query to fetch place_id based on placeName from the places table
    String selectPlaceIdQuery = "SELECT location_id FROM places WHERE places_name = ?";
    // Step 2: Query to delete the planned trip from planned_trips using place_id
    String deleteQuery = "DELETE FROM planned_trips WHERE place_id = ?";

    try (
            PreparedStatement selectPlaceIdStmt = connection.prepareStatement(selectPlaceIdQuery);
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)
    ) {
      // Fetch place_id for the given placeName
      selectPlaceIdStmt.setString(1, placeName);
      try (ResultSet rs = selectPlaceIdStmt.executeQuery()) {
        if (rs.next()) {
          int placeId = rs.getInt("location_id");

          // Use the resolved place_id to delete the planned trip
          deleteStmt.setInt(1, placeId);
          int rowsDeleted = deleteStmt.executeUpdate();

          // Return true if a row was deleted
          return rowsDeleted > 0;
        } else {
          System.out.println("Place name not found: " + placeName);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Return false if the operation failed
    return false;
  }


  public boolean editPlannedTrip(String placeName, String description) {
    // Step 1: Query to fetch place_id based on placeName from the places table
    String selectPlaceIdQuery = "SELECT location_id FROM places WHERE places_name = ?";
    // Step 2: Update additional_information in planned_trips using place_id
    String updatePlannedTripQuery = "UPDATE planned_trips SET additional_information = ? WHERE place_id = ?";

    try (
            PreparedStatement selectPlaceIdStmt = connection.prepareStatement(selectPlaceIdQuery);
            PreparedStatement updatePlannedTripStmt = connection.prepareStatement(updatePlannedTripQuery)
    ) {
      // Fetch place_id for the given placeName
      selectPlaceIdStmt.setString(1, placeName);
      try (ResultSet rs = selectPlaceIdStmt.executeQuery()) {
        if (rs.next()) {
          int placeId = rs.getInt("location_id");

          // Update additional_information in planned_trips using the place_id
          updatePlannedTripStmt.setString(1, description);
          updatePlannedTripStmt.setInt(2, placeId);
          int rowsUpdated = updatePlannedTripStmt.executeUpdate();

          // Show updated table and return success status
          showNext("planned_trips");
          return rowsUpdated > 0;
        } else {
          System.out.println("Place name not found: " + placeName);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Show table and return false if operation fails
    showNext("planned_trips");
    return false;
  }


  public boolean toggleTripCompletion(String placeName) {
    // Query to fetch location_id and current completion status based on places_name
    String selectQuery = "SELECT place_id, completed FROM planned_trips WHERE places_name = ?";
    // Query to update the completion status based on location_id
    String updateQuery = "UPDATE planned_trips SET completed = ? WHERE place_id = ?";

    try (
            // Prepare statements for both SELECT and UPDATE queries
            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery)
    ) {
      // Step 1: Fetch the location_id and current completion status
      selectStmt.setString(1, placeName); // Bind placeName to the SELECT query
      try (ResultSet rs = selectStmt.executeQuery()) {
        if (rs.next()) {
          // Retrieve location_id and current completion status
          int locationId = rs.getInt("location_id");
          boolean currentStatus = rs.getBoolean("completed");
          // Toggle the status
          boolean newStatus = !currentStatus;

          // Step 2: Update the new completion status
          updateStmt.setBoolean(1, newStatus); // Bind new completion status
          updateStmt.setInt(2, locationId);    // Bind location_id to the UPDATE query
          int rowsUpdated = updateStmt.executeUpdate();

          // Return true if the update was successful
          return rowsUpdated > 0;
        } else {
          // Handle the case where no record is found
          System.out.println("Place name not found in the database: " + placeName);
        }
      }
    } catch (SQLException e) {
      // Print the stack trace for debugging
      e.printStackTrace();
    }

    // Return false if toggling fails
    return false;
  }



  public String getTripNotes(String tripName) {
    String additionalInfo = "";
    String query = " SELECT additional_information FROM planned_trips WHERE place_id = ( SELECT location_id FROM places WHERE places_name = ?)";

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

  // ------------------- WISHLIST SCREEN --------------------

  public List<String> getWishlist() {
    List<String> wishlist = new ArrayList<>();
    String query = "SELECT places.places_name " +
            "FROM wishlist " +
            "JOIN places ON wishlist.place_id = places.location_id;";

    try (PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        // Fetch the correct column name
        wishlist.add(rs.getString("places_name"));
      }
    } catch (SQLException e) {
      // Log a meaningful error message
      System.err.println("Error fetching wishlist: " + e.getMessage());
      e.printStackTrace();
    }
    return wishlist; // Return the list of wishlist place names
  }


  public boolean moveWishlistToPlanned(String placeName) {
    if (addPlaceToPlannedTrips(placeName)) {
      return deleteWishlistPlace(placeName);
    }
    return false;
  }

  public boolean addPlaceToWishlist(String placeName) {
    String placeQuery = "SELECT location_id FROM places WHERE places_name = ?";
    String userCheckQuery = "SELECT user_id FROM users WHERE user_id = ?";
    String insertQuery = "INSERT INTO wishlist (user_id, place_id) VALUES (?, ?)";

    try (PreparedStatement placeStmt = connection.prepareStatement(placeQuery);
         PreparedStatement userCheckStmt = connection.prepareStatement(userCheckQuery);
         PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

      // Step 1: Check if userId exists in the users table
      userCheckStmt.setInt(1, userId);
      try (ResultSet userRs = userCheckStmt.executeQuery()) {
        if (!userRs.next()) {
          System.out.println("User ID does not exist.");
          return false;
        }
      }

      // Step 2: Retrieve place_id from places table based on placeName
      placeStmt.setString(1, placeName);
      try (ResultSet placeRs = placeStmt.executeQuery()) {
        if (!placeRs.next()) {
          System.out.println("Place does not exist.");
          return false;
        }
        int placeId = placeRs.getInt("location_id");

        // Step 3: Insert into wishlist table using userId and placeId
        insertStmt.setInt(1, userId);
        insertStmt.setInt(2, placeId);
        insertStmt.executeUpdate();
      }

      // Optional: Display or process wishlist
      showNext("wishlist");
      return true;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }


  public boolean deleteWishlistPlace(String placeName) {
    String placeQuery = "SELECT location_id FROM places WHERE places_name = ?";
    String deleteQuery = "DELETE FROM wishlist WHERE user_id = ? AND place_id = ?";

    try (PreparedStatement placeStmt = connection.prepareStatement(placeQuery);
         PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {

      // Step 1: Retrieve place_id from places table based on placeName
      placeStmt.setString(1, placeName);
      try (ResultSet rs = placeStmt.executeQuery()) {
        if (!rs.next()) {
          // If place doesn't exist, return false
          return false;
        }
        int placeId = rs.getInt("location_id");

        // Step 2: Delete from wishlist where user_id and place_id match
        deleteStmt.setInt(1, userId);
        deleteStmt.setInt(2, placeId);
        int rowsAffected = deleteStmt.executeUpdate();

        showNext("wishlist");
        // Check if a row was deleted
        return rowsAffected > 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
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
