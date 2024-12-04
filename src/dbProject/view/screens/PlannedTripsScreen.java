package dbProject.view.screens;

import javax.swing.*;
import java.awt.*;
import dbProject.TravelController;
import java.awt.event.ActionEvent;
import java.util.List;

public class PlannedTripsScreen extends JPanel {
  private final TravelController controller;

  public PlannedTripsScreen(TravelController controller) {
    this.controller = controller;
    setLayout(new BorderLayout(20, 20));  // Add padding for proportional layout
    setBackground(new Color(255, 240, 245));  // Light pink background for the whole screen

    // Title Label
    JLabel titleLabel = new JLabel("Planned Trips", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 26));  // Larger font size for title
    titleLabel.setForeground(new Color(255, 105, 180));  // Deep pink for title
    add(titleLabel, BorderLayout.NORTH);

    // List of Planned Trips
    JList<String> tripsList = new JList<>();
    tripsList.setFont(new Font("Arial", Font.PLAIN, 16));  // Set font for the list
    JScrollPane listScrollPane = new JScrollPane(tripsList);
    listScrollPane.setPreferredSize(new Dimension(400, 200));  // Fixed size for the list
    add(listScrollPane, BorderLayout.CENTER);

    // Action Buttons Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));  // Add spacing between buttons
    buttonPanel.setBackground(new Color(255, 240, 245));  // Same light pink background
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Add padding around button panel

    // Button styling
    JButton editButton = new JButton("Edit Trip");
    JButton deleteButton = new JButton("Delete Trip");
    JButton toggleCompletedButton = new JButton("Mark as Completed");
    JButton homeButton = new JButton("Home");
    JButton quitButton = new JButton("Quit");

    // Style the buttons
    editButton.setBackground(new Color(255, 182, 193));  // Soft pink button color
    editButton.setFont(new Font("Arial", Font.BOLD, 14));
    editButton.setPreferredSize(new Dimension(140, 40));  // Button size

    deleteButton.setBackground(new Color(255, 182, 193));  // Soft pink button color
    deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
    deleteButton.setPreferredSize(new Dimension(140, 40));  // Button size

    toggleCompletedButton.setBackground(new Color(255, 182, 193));  // Soft pink button color
    toggleCompletedButton.setFont(new Font("Arial", Font.BOLD, 14));
    toggleCompletedButton.setPreferredSize(new Dimension(180, 40));  // Button size

    homeButton.setBackground(new Color(255, 182, 193));  // Soft pink button color
    homeButton.setFont(new Font("Arial", Font.BOLD, 14));
    homeButton.setPreferredSize(new Dimension(120, 40));  // Button size

    quitButton.setBackground(new Color(255, 182, 193));  // Soft pink button color
    quitButton.setFont(new Font("Arial", Font.BOLD, 14));
    quitButton.setPreferredSize(new Dimension(120, 40));  // Button size

    // Add buttons to button panel
    buttonPanel.add(homeButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(toggleCompletedButton);
    buttonPanel.add(quitButton);
    add(buttonPanel, BorderLayout.SOUTH);

    // Populate the list with planned trips
    refreshTripsList(tripsList);

    // Action listeners
    editButton.addActionListener((ActionEvent e) -> {
      String selectedTrip = tripsList.getSelectedValue();
      if (selectedTrip != null) {
        String newDetails = JOptionPane.showInputDialog(this, "Edit trip details:", selectedTrip);
        if (newDetails != null) {
          controller.editPlannedTrip(selectedTrip, newDetails);
          refreshTripsList(tripsList);
          JOptionPane.showMessageDialog(this, "Trip updated.");
        }
      } else {
        JOptionPane.showMessageDialog(this, "Please select a trip!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    deleteButton.addActionListener((ActionEvent e) -> {
      String selectedTrip = tripsList.getSelectedValue();
      if (selectedTrip != null) {
        controller.deletePlannedTrip(selectedTrip);
        refreshTripsList(tripsList);
        JOptionPane.showMessageDialog(this, "Trip deleted.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a trip!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    toggleCompletedButton.addActionListener((ActionEvent e) -> {
      String selectedTrip = tripsList.getSelectedValue();
      if (selectedTrip != null) {
        controller.toggleTripCompletion(selectedTrip);
        JOptionPane.showMessageDialog(this, "Trip completion status toggled.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a trip!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    // Action listener for "Home" button
    homeButton.addActionListener((ActionEvent e) -> {
      // Assuming you have a method that switches to the home screen:
      controller.showNext("home");  // Implement showHomeScreen() method in your controller
    });

    // Action listener for "Quit" button
    quitButton.addActionListener((ActionEvent e) -> {
      int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION);
      if (confirm == JOptionPane.YES_OPTION) {
        System.exit(0); // Exit the application
      }
    });
  }

  private void refreshTripsList(JList<String> tripsList) {
    List<String> trips = controller.getPlannedTrips(false);

    if (trips.isEmpty()) {
      tripsList.setListData(new String[] {"List is empty"});
    } else {
      // Format each trip with name and notes
      String[] formattedTrips = new String[trips.size()];
      for (int i = 0; i < trips.size(); i++) {
        String trip = trips.get(i);
        String tripNotes = controller.getTripNotes(trip);  // Assuming you have a method that fetches the notes for each trip
        formattedTrips[i] = trip + " - Notes: " + (tripNotes != null ? tripNotes : "No notes available");
      }
      tripsList.setListData(formattedTrips);
    }
  }
}
