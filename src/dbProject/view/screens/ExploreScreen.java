package dbProject.view.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import dbProject.TravelController;

public class ExploreScreen extends JPanel {
  private final TravelController controller;
  private List<String> filters;

  public ExploreScreen(TravelController controller) {
    this.controller = controller;
    filters = new ArrayList<>();
    setLayout(new BorderLayout());

    // Set the background color to a light pink
    setBackground(new Color(255, 182, 193)); // Light pink background

    // Create a JPanel with a buffer (padding) around the content
    JPanel outerPanel = new JPanel(new BorderLayout());
    outerPanel.setBackground(new Color(255, 182, 193)); // Light pink background
    outerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adding buffer around


    // Create a panel specifically for the title and add it above everything
    JPanel titlePanel = new JPanel();
    titlePanel.setBackground(new Color(255, 182, 193)); // Match background color

    JLabel titleLabel = new JLabel("Adventure Awaits...", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
    titleLabel.setForeground(new Color(255, 105, 180)); // Hot pink text

    // Add the title label to the title panel
    titlePanel.add(titleLabel);

    // Add the title panel to the outerPanel at the top
    outerPanel.add(titlePanel, BorderLayout.NORTH);

    // Panel for filter buttons (Country, Activity, Rating)
    JPanel filterPanel = new JPanel();
    filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS)); // Stack components vertically
    filterPanel.setBackground(new Color(255, 182, 193)); // Match background color

    JButton filterCountryButton = createStyledButton("Filter by Country");
    JButton filterActivityButton = createStyledButton("Filter by Activity");
    JButton filterRatingButton = createStyledButton("Filter by Rating");

    filterPanel.add(Box.createVerticalStrut(10));
    filterPanel.add(filterCountryButton);
    filterPanel.add(Box.createVerticalStrut(10));
    filterPanel.add(filterActivityButton);
    filterPanel.add(Box.createVerticalStrut(10));
    filterPanel.add(filterRatingButton);

    // Add Eiffel Tower image below the buttons
    try {
      // Load the image from resources
      ImageIcon eiffelTowerIcon = new ImageIcon(getClass().getResource("/src/dbProject/view/screens/eiffelTower.png"));
      JLabel eiffelTowerLabel = new JLabel(eiffelTowerIcon);
      filterPanel.add(eiffelTowerLabel);
    } catch (Exception e) {
      System.err.println("Error loading Eiffel Tower image: " + e.getMessage());
    }


    outerPanel.add(filterPanel, BorderLayout.AFTER_LINE_ENDS);

    // Center panel for the list of places
    JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // Two-column layout
    centerPanel.setBackground(new Color(255, 182, 193)); // Match background color

    // Left column for filtered places
    JPanel filteredPanel = createPlacePanel("Filtered Places");
    JScrollPane filteredScrollPane = (JScrollPane) filteredPanel.getComponent(1);

    // Right column for all places
    JPanel allPlacesPanel = createPlacePanel("All Places");
    JScrollPane allPlacesScrollPane = (JScrollPane) allPlacesPanel.getComponent(1);

    // Add panels to center panel
    centerPanel.add(filteredPanel);
    centerPanel.add(allPlacesPanel);
    outerPanel.add(centerPanel, BorderLayout.CENTER);

    // Action buttons panel
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.setBackground(new Color(255, 182, 193)); // Match background color

    JButton viewDetailsButton = createStyledButton("View Details");
    JButton addToPlannedButton = createStyledButton("Add to Planned");
    JButton addToWishlistButton = createStyledButton("Add to Wishlist");

    buttonPanel.add(viewDetailsButton);
    buttonPanel.add(addToPlannedButton);
    buttonPanel.add(addToWishlistButton);
    outerPanel.add(buttonPanel, BorderLayout.SOUTH);

    // Add the outer panel to the main ExploreScreen panel
    add(outerPanel, BorderLayout.CENTER);

    // Populate the "All Places" list with places from the database
    refreshPlacesList((JList<String>) allPlacesScrollPane.getViewport().getView(), "All Places");
    refreshPlacesList((JList<String>) filteredScrollPane.getViewport().getView(), "Filtered Places");

    // Action listeners for filter buttons
    filterCountryButton.addActionListener((ActionEvent e) -> {
      // Add logic for filtering places by country
      // Example: controller.filterByCountry();
      JOptionPane.showMessageDialog(this, "Filter by Country clicked.");
    });

    filterActivityButton.addActionListener((ActionEvent e) -> {
      // Add logic for filtering places by activity
      // Example: controller.filterByActivity();
      JOptionPane.showMessageDialog(this, "Filter by Activity clicked.");
    });

    filterRatingButton.addActionListener((ActionEvent e) -> {
      // Add logic for filtering places by rating
      // Example: controller.filterByRating();
      JOptionPane.showMessageDialog(this, "Filter by Rating clicked.");
    });

    // Action listeners for other buttons
    viewDetailsButton.addActionListener((ActionEvent e) -> {
      JList<String> allPlacesList = (JList<String>) allPlacesScrollPane.getViewport().getView();
      String selectedPlace = allPlacesList.getSelectedValue();
      if (selectedPlace != null) {
        String details = controller.getPlaceDetails(selectedPlace);
        JOptionPane.showMessageDialog(this, details, "Place Details", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    addToPlannedButton.addActionListener((ActionEvent e) -> {
      JList<String> allPlacesList = (JList<String>) allPlacesScrollPane.getViewport().getView();
      String selectedPlace = allPlacesList.getSelectedValue();
      if (selectedPlace != null) {
        controller.addPlaceToPlannedTrips(selectedPlace);
        JOptionPane.showMessageDialog(this, selectedPlace + " added to planned trips.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    addToWishlistButton.addActionListener((ActionEvent e) -> {
      JList<String> allPlacesList = (JList<String>) allPlacesScrollPane.getViewport().getView();
      String selectedPlace = allPlacesList.getSelectedValue();
      if (selectedPlace != null) {
        controller.addPlaceToWishlist(selectedPlace);
        JOptionPane.showMessageDialog(this, selectedPlace + " added to wishlist.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  // Helper method to create a styled panel for places
  private JPanel createPlacePanel(String title) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(new Color(255, 182, 193)); // Light pink background

    JLabel titleLabel = new JLabel(title, JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    titleLabel.setForeground(new Color(255, 105, 180)); // Hot pink text
    panel.add(titleLabel, BorderLayout.NORTH);

    JList<String> placesList = new JList<>();
    placesList.setBackground(Color.PINK);
    placesList.setForeground(new Color(255, 105, 180)); // Hot pink text
    JScrollPane scrollPane = new JScrollPane(placesList);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  // Helper method to create a styled button
  private JButton createStyledButton(String text) {
    JButton button = new JButton(text);
    button.setBackground(new Color(255, 105, 180)); // Hot pink background
    button.setForeground(Color.PINK); // White text
    button.setFocusPainted(false);
    button.setFont(new Font("Arial", Font.BOLD, 14));
    return button;
  }

  // Refresh the list with places from the database
  private void refreshPlacesList(JList<String> placesList, String listType) {
    String[] places;
    if (listType.equals("All Places")) {
      places = controller.getPlacesToExplore().toArray(new String[0]);
    } else {
      places = controller.getFilteredPlaces(filters).toArray(new String[0]); // You can adjust this as needed
    }

    if (places.length == 0) {
      placesList.setListData(new String[]{"Looks like this list is empty for now"});
    } else {
      placesList.setListData(places);
    }
  }
}
