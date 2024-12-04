package dbProject.view.screens;

import javax.swing.*;
import java.awt.*;
import dbProject.TravelController;
import java.awt.event.ActionEvent;
import java.util.List;

public class WishlistScreen extends JPanel {
  private final TravelController controller;

  public WishlistScreen(TravelController controller) {
    this.controller = controller;
    setLayout(new BorderLayout(20, 20));  // Add padding for proportional layout
    setBackground(new Color(255, 240, 245));  // Light pink background for the whole screen

    // Title Label
    JLabel titleLabel = new JLabel("Wishlist", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 26));  // Larger font size for title
    titleLabel.setForeground(new Color(255, 105, 180));  // Deep pink for title
    add(titleLabel, BorderLayout.NORTH);

    // List of Wishlist Items
    JList<String> wishlistList = new JList<>();
    wishlistList.setFont(new Font("Arial", Font.PLAIN, 16));  // Set font for the list
    JScrollPane listScrollPane = new JScrollPane(wishlistList);
    listScrollPane.setPreferredSize(new Dimension(400, 200));  // Fixed size for the list
    add(listScrollPane, BorderLayout.CENTER);

    // Action Buttons Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));  // Add spacing between buttons
    buttonPanel.setBackground(new Color(255, 240, 245));  // Same light pink background
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Add padding around button panel

    // Buttons
    JButton homeButton = new JButton("Home");
    JButton deleteButton = new JButton("Delete from Wishlist");
    JButton addToPlannedButton = new JButton("Add to Planned Trips");
    JButton viewButton = new JButton("View Place");

    // Style the buttons
    for (JButton button : new JButton[]{homeButton, deleteButton, addToPlannedButton, viewButton}) {
      button.setBackground(new Color(255, 182, 193));  // Soft pink button color
      button.setFont(new Font("Arial", Font.BOLD, 14));
      button.setPreferredSize(new Dimension(180, 40));  // Button size
      buttonPanel.add(button);
    }

    add(buttonPanel, BorderLayout.SOUTH);

    // Populate the list with wishlist items
    refreshWishlist(wishlistList);

    // Action listeners
    homeButton.addActionListener((ActionEvent e) -> controller.showNext("home"));

    deleteButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = wishlistList.getSelectedValue();
      if (selectedPlace != null) {
        controller.deleteWishlistPlace(selectedPlace);
        refreshWishlist(wishlistList);
        JOptionPane.showMessageDialog(this, "Place removed from wishlist.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    addToPlannedButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = wishlistList.getSelectedValue();
      if (selectedPlace != null) {
        controller.addPlaceToPlannedTrips(selectedPlace);
        controller.deleteWishlistPlace(selectedPlace);
        refreshWishlist(wishlistList);
        JOptionPane.showMessageDialog(this, "Place added to planned trips.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    viewButton.addActionListener((ActionEvent e) -> {
      String selectedPlace = wishlistList.getSelectedValue();
      if (selectedPlace != null) {
        String details = controller.getPlaceDetails(selectedPlace);
        JOptionPane.showMessageDialog(this, details, "Place Details", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(this, "Please select a place!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  private void refreshWishlist(JList<String> wishlistList) {
    List<String> wishlist = controller.getWishlist();

    if (wishlist.isEmpty()) {
      wishlistList.setListData(new String[]{"Wishlist is empty"});
    } else {
      wishlistList.setListData(wishlist.toArray(new String[0]));
    }
  }
}
