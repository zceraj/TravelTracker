package dbProject.view.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import dbProject.TravelController;

public class PlacesScreen extends JPanel {
  private TravelController controller;
  String countryName;
  int rating;
  String[]activities;
  String[] foods;



  public PlacesScreen(String cityName, TravelController controller) {
    this.controller = controller;
    String[] info = controller.getPlaceInfo(cityName);

    // Assign the country name (assuming it's the first element in the info array)
    countryName = info[0];

    // Assign the rating (assuming it's the second element and needs to be parsed as an integer)
    rating = Integer.parseInt(info[1]);

    // Determine where the activities start in the info array and assign them
    int activitiesStartIndex = 2;
    int foodsStartIndex = activitiesStartIndex;

    // Assuming activities are in the array after the country and rating
    // You may need to adjust the logic based on the structure of your info array
    while (foodsStartIndex < info.length && info[foodsStartIndex] != null && !info[foodsStartIndex].isEmpty()) {
      foodsStartIndex++;
    }

    activities = Arrays.copyOfRange(info, activitiesStartIndex, foodsStartIndex);
    foods = Arrays.copyOfRange(info, foodsStartIndex, info.length);

    // Set the main layout with a little padding and space between components
    setLayout(new BorderLayout(20, 20));
    setBackground(new Color(255, 228, 238)); // Light pink background for the panel

    // Title Panel: City and Country Name
    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new GridLayout(2, 1));
    titlePanel.setBackground(new Color(255, 182, 193)); // Lighter pink for title background

    JLabel cityLabel = new JLabel(cityName, SwingConstants.CENTER);
    cityLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Larger font for the city name
    cityLabel.setForeground(new Color(255, 105, 180)); // Deep pink color for text

    JLabel countryLabel = new JLabel(countryName, SwingConstants.CENTER);
    countryLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Slightly smaller font for the country
    countryLabel.setForeground(new Color(219, 112, 147)); // Soft pink for text

    titlePanel.add(cityLabel);
    titlePanel.add(countryLabel);

    // Rating Panel
    JPanel ratingPanel = new JPanel();
    ratingPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    ratingPanel.setBackground(new Color(255, 228, 238)); // Same as the background of the main panel

    JLabel ratingLabel = new JLabel("Rating: ");
    ratingLabel.setFont(new Font("Arial", Font.BOLD, 18));
    ratingLabel.setForeground(new Color(255, 105, 180));
    ratingPanel.add(ratingLabel);

    for (int i = 1; i <= 5; i++) {
      JLabel starLabel = new JLabel(i <= rating ? "★" : "☆");
      starLabel.setFont(new Font("Serif", Font.PLAIN, 24));
      starLabel.setForeground(i <= rating ? Color.YELLOW : Color.GRAY); // Gold stars for rating
      ratingPanel.add(starLabel);
    }

    // Activities and Food Panel (Split into two columns)
    JPanel listsPanel = new JPanel();
    listsPanel.setLayout(new GridLayout(1, 2, 20, 20));
    listsPanel.setBackground(new Color(255, 240, 245)); // Lighter pink for list panel background

    // Activities List
    JPanel activitiesPanel = new JPanel(new BorderLayout());
    activitiesPanel.setBackground(new Color(255, 240, 245)); // Match background color
    JLabel activitiesLabel = new JLabel("Activities", SwingConstants.CENTER);
    activitiesLabel.setFont(new Font("Arial", Font.BOLD, 16));
    activitiesLabel.setForeground(new Color(219, 112, 147)); // Soft pink title color

    JList<String> activitiesList = new JList<>(activities);
    activitiesList.setFont(new Font("Arial", Font.PLAIN, 14));
    activitiesList.setBackground(new Color(255, 255, 255)); // White background for the list
    activitiesList.setForeground(new Color(255, 105, 180)); // Pink text color
    JScrollPane activitiesScrollPane = new JScrollPane(activitiesList);

    activitiesPanel.add(activitiesLabel, BorderLayout.NORTH);
    activitiesPanel.add(activitiesScrollPane, BorderLayout.CENTER);

    // Food List
    JPanel foodPanel = new JPanel(new BorderLayout());
    foodPanel.setBackground(new Color(255, 240, 245)); // Match background color
    JLabel foodLabel = new JLabel("Food", SwingConstants.CENTER);
    foodLabel.setFont(new Font("Arial", Font.BOLD, 16));
    foodLabel.setForeground(new Color(219, 112, 147)); // Soft pink title color

    JList<String> foodList = new JList<>(foods);
    foodList.setFont(new Font("Arial", Font.PLAIN, 14));
    foodList.setBackground(new Color(255, 255, 255)); // White background for the list
    foodList.setForeground(new Color(255, 105, 180)); // Pink text color
    JScrollPane foodScrollPane = new JScrollPane(foodList);

    foodPanel.add(foodLabel, BorderLayout.NORTH);
    foodPanel.add(foodScrollPane, BorderLayout.CENTER);

    // Adding the activity and food panels to the lists panel
    listsPanel.add(activitiesPanel);
    listsPanel.add(foodPanel);

    // Adding the main panels to the screen
    add(titlePanel, BorderLayout.NORTH);
    add(ratingPanel, BorderLayout.CENTER);
    add(listsPanel, BorderLayout.SOUTH);

    // **New functionality for buttons**
    // Panel for buttons (Home, Quit, Add to Planned, Add to Wishlist)
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

    // Create buttons
    JButton addToPlannedButton = new JButton("Add to Planned Trips");
    JButton addToWishlistButton = new JButton("Add to Wishlist");
    JButton homeButton = new JButton("Home");
    JButton quitButton = new JButton("Quit");

    buttonPanel.add(homeButton);
    buttonPanel.add(quitButton);
    buttonPanel.add(addToPlannedButton);
    buttonPanel.add(addToWishlistButton);

    add(buttonPanel, BorderLayout.SOUTH); // Add the button panel at the bottom of the main screen

    // Action listener for "Home" button
    homeButton.addActionListener((ActionEvent e) -> {
      // Assuming you have a method that switches to the home screen, e.g.:
      controller.showNext("home");  // Implement showHomeScreen() method in your controller
    });

    // Action listener for "Quit" button
    quitButton.addActionListener((ActionEvent e) -> {
      int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION);
      if (confirm == JOptionPane.YES_OPTION) {
        System.exit(0); // Exit the application
      }
    });

    // Action listener for "Add to Planned Trips" button
    addToPlannedButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = cityName + ", " + countryName;
      if (selectedPlace != null) {
        controller.addPlaceToPlannedTrips(selectedPlace);
        JOptionPane.showMessageDialog(this, selectedPlace + " added to planned trips.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    // Action listener for "Add to Wishlist" button
    addToWishlistButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = cityName + ", " + countryName;
      if (selectedPlace != null) {
        controller.addPlaceToWishlist(selectedPlace);
        JOptionPane.showMessageDialog(this, selectedPlace + " added to wishlist.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
  }
}
