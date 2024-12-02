package dbProject.view.screens;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import dbProject.TravelController;

public class ExploreScreen extends JPanel {
  private final TravelController controller;

  public ExploreScreen(TravelController controller) {
    this.controller = controller;
    setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("Explore Places", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    add(titleLabel, BorderLayout.NORTH);

    // List of places to explore
    JList<String> placesList = new JList<>();
    JScrollPane listScrollPane = new JScrollPane(placesList);
    add(listScrollPane, BorderLayout.CENTER);

    // Action buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton viewDetailsButton = new JButton("View Details");
    JButton addToPlannedButton = new JButton("Add to Planned");
    JButton addToWishlistButton = new JButton("Add to Wishlist");

    buttonPanel.add(viewDetailsButton);
    buttonPanel.add(addToPlannedButton);
    buttonPanel.add(addToWishlistButton);
    add(buttonPanel, BorderLayout.SOUTH);

    // Populate the list with places
    refreshPlacesList(placesList);

    // Action listeners
    viewDetailsButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = placesList.getSelectedValue();
      if (selectedPlace != null) {
        String details = controller.getPlaceDetails(selectedPlace);
        JOptionPane.showMessageDialog(this, details, "Place Details", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    addToPlannedButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = placesList.getSelectedValue();
      if (selectedPlace != null) {
        controller.addPlaceToPlannedTrips(selectedPlace);
        JOptionPane.showMessageDialog(this, selectedPlace + " added to planned trips.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    addToWishlistButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = placesList.getSelectedValue();
      if (selectedPlace != null) {
        controller.addPlaceToWishlist(selectedPlace);
        JOptionPane.showMessageDialog(this, selectedPlace + " added to wishlist.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  private void refreshPlacesList(JList<String> placesList) {
    String[] places = controller.getExplorePlaces();
    placesList.setListData(places);
  }
}
