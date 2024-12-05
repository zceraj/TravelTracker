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
    frame.setContentPane(new WelcomeScreen(this));
//    initializeConnection("root", "Me0wmeow"); // TO-DO: DELETE
//    frame.setContentPane(new HomePage(this)); // TO-DO: DELETE
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
      e.toString();
    } finally {
      // Removed connection close to prevent premature closing
      try {
        if (generatedKeys != null) generatedKeys.close();
        if (statement != null) statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return false;
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

    // Base SQL query
    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM places WHERE 1=1");

    // List to store parameter values for prepared statement
    List<Object> parameters = new ArrayList<>();

    // Dynamically build the WHERE conditions based on the filters provided
    for (String filter : filters) {
      // Assume the filter format is key=value (e.g., "name=Paris")
      String[] filterParts = filter.split(" ");
      if (filterParts.length == 2) {
        String key = filterParts[0];
        String value = filterParts[1];

        // Add filter condition to the query
        queryBuilder.append(" AND ").append(key).append(" = ?");
        parameters.add(value);
      }
    }

    // Convert the queryBuilder to a String
    String query = queryBuilder.toString();

    // Execute the query using PreparedStatement
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Set the parameters for the PreparedStatement
      for (int i = 0; i < parameters.size(); i++) {
        stmt.setObject(i + 1, parameters.get(i));
      }

      // Execute the query and process the result set
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          // Process the result set, for example, adding the place_id to the list
          places.add(rs.getObject("places_name"));  // Or any other relevant data from the result set
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();  // Log the exception for debugging
    }

    return places;
  }

  // Method to get all activities for a specific place by place name
  public List<String> getActivities(String placeName) {
    List<String> activities = new ArrayList<>();

    // SQL query to fetch activities for a place by its name
    String query = "SELECT activity_id " +
            "FROM place_activities a " +
            "JOIN places p ON a.place_id = p.places_name " +
            "WHERE p.places_name = ?";

    // Using try-with-resources to automatically close resources
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      // Set the placeName parameter in the query
      statement.setString(1, placeName);

      // Execute the query
      try (ResultSet resultSet = statement.executeQuery()) {
        // Iterate over the result set and add activities to the list
        while (resultSet.next()) {
          String activity = resultSet.getString("activity_id");
          activities.add(activity);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();  // Handle any SQL exceptions
    }

    return activities;  // Return the list of activities
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
      String placeQuery = "SELECT places_name FROM places WHERE places_name = ?";  // Select place_id instead of places_name
      PreparedStatement stmt = connection.prepareStatement(placeQuery);
      stmt.setString(1, placeName);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        String placeId = rs.getString("places_name");  // Correctly fetch place_id as an int

        // Now insert the place_id into the planned_trips table
        String insertQuery = "INSERT INTO planned_trips (place_id, completed) VALUES (?, false)";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
        insertStmt.setString(1, placeId);  // Use place_id here as an int
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

    // Adjust the query to use the completed parameter
    String query = "SELECT place_id FROM planned_trips WHERE completed = ?;";

    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      // Set the boolean parameter for completed status
      stmt.setBoolean(1, completed); // Use the passed 'completed' parameter

      // Execute the query and process the result set
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          // Retrieve the "place_id" from the result set
          trips.add(rs.getString("place_id"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace(); // Log the exception for debugging
    }

    return trips; // Return the list of trips
  }

  public boolean markTripAsCompleted(String placeName) {
    try {
      String query = "UPDATE planned_trips SET completed = 'completed' WHERE place_id = ?";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, placeName);
      stmt.executeUpdate();
      showNext("planned_trips");
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean deletePlannedTrip(String placeName) {
    // Step 1: Query to fetch place_id based on placeName from the places table
    String selectPlaceIdQuery = "SELECT places_name FROM places WHERE places_name = ?";
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
          String placeId = rs.getString("places_name");

          // Use the resolved place_id to delete the planned trip
          deleteStmt.setString(1, placeId);
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
    System.out.println("Editing trip: " + placeName + " with new details: " + description);

    // Step 1: Query to fetch place_id based on placeName from the places table
    String selectPlaceIdQuery = "SELECT places_name FROM places WHERE places_name = ?";

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
          // Get the place_id from the result set
          String placeId = rs.getString("places_name");

          // Step 3: Update additional_information in planned_trips using place_id
          updatePlannedTripStmt.setString(1, description);  // Set the description to be updated
          updatePlannedTripStmt.setString(2, placeId);  // Set the correct place_id

          int rowsUpdated = updatePlannedTripStmt.executeUpdate();

          // If update was successful, return true
          if (rowsUpdated > 0) {
            System.out.println("Trip updated successfully.");
            showNext("planned");  // Update the UI
            return true;
          } else {
            System.out.println("No rows were updated.");
          }
        } else {
          System.out.println("Place name not found: " + placeName);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Return false if update failed
    showNext("planned");
    return false;
  }


  public String getTripNotes(String tripName) {
    String additionalInfo = "";
    String query = " SELECT additional_information FROM planned_trips WHERE place_id = ( SELECT places_name FROM places WHERE places_name = ?)";

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

    if (additionalInfo == null) {
      additionalInfo = "";
    }
    return additionalInfo.isEmpty() ? "No additional information available" : additionalInfo;
  }

  // ------------------- WISHLIST SCREEN --------------------

  public List<String> getWishlist() {
    List<String> wishlist = new ArrayList<>();
    String query = "SELECT places.places_name " +
            "FROM wishlist " +
            "JOIN places ON wishlist.place_id = places.places_name;";

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
    // SQL queries
    String placeQuery = "SELECT places_name FROM places WHERE places_name = ?";
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

        String placeId = rs.getString("places_name");  // Retrieve the place_id (integer type)

        // Step 2: Delete from wishlist where user_id and place_id match
        deleteStmt.setInt(1, userId);  // Set user_id parameter
        deleteStmt.setString(2, placeId); // Set place_id parameter (integer type)
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
            + "LEFT JOIN place_activities pa ON p.places_name = pa.place_id "
            + "LEFT JOIN activities a ON pa.activity_id = a.activity_id "
            + "WHERE p.places_name = ? "
            + "GROUP BY p.places_name";

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

  public void addNewPlace(String placeName, String country, String food, float calculatedRating) {
    // SQL query to insert new place
    String query = "INSERT INTO places (places_name, country, food, calculated_rating) VALUES (?, ?, ?, ?)";

    // Assuming 'connection' is already defined and established outside this method
    try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

      // Set the parameters for the query
      preparedStatement.setString(1, placeName);
      preparedStatement.setString(2, country);
      preparedStatement.setString(3, food);
      preparedStatement.setFloat(4, calculatedRating);

      // Execute the query
      int rowsAffected = preparedStatement.executeUpdate();

      if (rowsAffected > 0) {
        System.out.println("New place added successfully!");
      } else {
        System.out.println("Failed to add the place.");
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
