package dbProject.view.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import dbProject.TravelController;

public class HomePage extends JPanel {
  private final TravelController controller;

  public HomePage(TravelController controller) {
    this.controller = controller;
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);

    // Set background color to a soft pink
    setBackground(new Color(255, 182, 193));

    // Title Label
    JLabel titleLabel = new JLabel("Travel Tracker Home", JLabel.CENTER);
    titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30)); // Choose a fun, playful font
    titleLabel.setForeground(new Color(255, 105, 180)); // Soft pink color for the title
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    add(titleLabel, gbc);

    // Navigation Buttons with custom styling
    JButton exploreButton = createStyledButton("Explore Places");
    JButton plannedTripsButton = createStyledButton("Planned Trips");
    JButton wishlistButton = createStyledButton("Wishlist");
    JButton logoutButton = createStyledButton("Logout");

    // "Make New Place" button
    JButton makeNewPlaceButton = createStyledButton("Make New Place");

    // Position the buttons
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 1;
    add(exploreButton, gbc);

    gbc.gridx = 1;
    add(plannedTripsButton, gbc);

    gbc.gridx = 2;
    add(wishlistButton, gbc);

    // Make New Place Button
    gbc.gridx = 0;
    gbc.gridy = 2;
    add(makeNewPlaceButton, gbc);

    // Logout Button
    gbc.gridx = 1;
    gbc.gridy = 3;
    add(logoutButton, gbc);

    // Set plane images (same as your original code)
    String[] planeImages = {"/PlanePic1.png", "/PlanePic2.png", "/PlanePic3.png"};
    int miniWidth = 50;  // Set the width for mini images
    int miniHeight = 50; // Set the height for mini images
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 4;
    for (int i = 0; i < 3; i++) {
      ImageIcon planeIcon = new ImageIcon(getClass().getResource("/PlanePic.png"));
      if (planeIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
        System.out.println("Image not loaded successfully: " + planeImages[i]);
      }
      Image scaledImage = planeIcon.getImage().getScaledInstance(miniWidth, miniHeight, Image.SCALE_SMOOTH);
      planeIcon = new ImageIcon(scaledImage);
      JLabel planeLabel = new JLabel(planeIcon);
      add(planeLabel, gbc);
      gbc.gridx++;  // Move to the next column for the next image
      if ((i + 1) % 3 == 0) {
        gbc.gridx = 0;
        gbc.gridy++;
      }
    }

    // Action Listeners for Buttons
    exploreButton.addActionListener((ActionEvent e) -> {
      controller.showNext("explore");
    });

    plannedTripsButton.addActionListener((ActionEvent e) -> {
      controller.showNext("planned");
    });

    wishlistButton.addActionListener((ActionEvent e) -> {
      controller.showNext("wishlist");
    });

    logoutButton.addActionListener((ActionEvent e) -> {
      JOptionPane.showMessageDialog(this, "Logged out successfully!", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
      controller.showNext("logout");
    });

    // Action listener for the "Make New Place" button
    makeNewPlaceButton.addActionListener((ActionEvent e) -> {
      createNewPlace();
    });

    repaint();
  }

  // Helper method to create styled buttons
  private JButton createStyledButton(String text) {
    JButton button = new JButton(text);
    button.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font size
    button.setBackground(new Color(255, 105, 180)); // Soft pink button color
    button.setForeground(Color.WHITE); // White text for contrast
    button.setFocusPainted(false); // Remove focus border
    button.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 2)); // Border color
    button.setPreferredSize(new Dimension(180, 40)); // Set proportional button size
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor for interactivity
    return button;
  }

  // Method to handle the creation of a new place
  private void createNewPlace() {
    // Show a dialog to collect place details
    JPanel panel = new JPanel(new GridLayout(5, 2));

    JTextField nameField = new JTextField();
    JTextField countryField = new JTextField();
    JTextField foodField = new JTextField();
    JTextField ratingField = new JTextField();

    panel.add(new JLabel("Place Name:"));
    panel.add(nameField);
    panel.add(new JLabel("Country:"));
    panel.add(countryField);
    panel.add(new JLabel("Food:"));
    panel.add(foodField);
    panel.add(new JLabel("Calculated Rating (float):"));
    panel.add(ratingField);

    int option = JOptionPane.showConfirmDialog(this, panel, "Enter New Place Information", JOptionPane.OK_CANCEL_OPTION);

    if (option == JOptionPane.OK_OPTION) {
      String placeName = nameField.getText();
      String country = countryField.getText();
      String food = foodField.getText();
      String ratingText = ratingField.getText();

      // Validate the rating input to ensure it's a valid float
      float calculatedRating = 0f;
      try {
        calculatedRating = Float.parseFloat(ratingText);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid rating input. Please enter a valid float.", "Error", JOptionPane.ERROR_MESSAGE);
        return; // Exit the method if the rating is invalid
      }

      // Call the addNewPlace method to insert the place into the database
      controller.addNewPlace(placeName, country, food, calculatedRating);

      // Notify the user of success
      JOptionPane.showMessageDialog(this, "New place added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
  }

}
