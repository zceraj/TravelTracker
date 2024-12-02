package dbProject.view.screens;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import dbProject.DatabaseManager;
import dbProject.TravelController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WishlistScreen extends JPanel {
  private final TravelController controller;

  public WishlistScreen(TravelController controller) {
    this.controller = controller;
    setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("Wishlist", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    add(titleLabel, BorderLayout.NORTH);

    // List of wishlist places
    JList<String> wishlistList = new JList<>();
    JScrollPane listScrollPane = new JScrollPane(wishlistList);
    add(listScrollPane, BorderLayout.CENTER);

    // Action buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton viewDetailsButton = new JButton("View Details");
    JButton deleteButton = new JButton("Delete");
    JButton addToPlannedButton = new JButton("Add to Planned");

    buttonPanel.add(viewDetailsButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(addToPlannedButton);
    add(buttonPanel, BorderLayout.SOUTH);

    // Populate the list with wishlist places
    refreshWishlist(wishlistList);

    // Action listeners
    viewDetailsButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = wishlistList.getSelectedValue();
      if (selectedPlace != null) {
        String details = controller.getPlaceDetails(selectedPlace);
        JOptionPane.showMessageDialog(this, details, "Place Details", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    deleteButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = wishlistList.getSelectedValue();
      if (selectedPlace != null) {
        controller.deleteFromWishlist(selectedPlace);
        refreshWishlist(wishlistList);
        JOptionPane.showMessageDialog(this, "Place deleted from wishlist.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    addToPlannedButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = wishlistList.getSelectedValue();
      if (selectedPlace != null) {
        controller.addPlaceToPlannedTrips(selectedPlace);
        JOptionPane.showMessageDialog(this, "Place added to planned trips.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  private void refreshWishlist(JList<String> wishlistList) {
    String[] wishlist = controller.getWishlist();
    wishlistList.setListData(wishlist);
  }
}
