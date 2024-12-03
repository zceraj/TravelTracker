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

    // Position the buttons
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 1;
    add(exploreButton, gbc);

    gbc.gridx = 1;
    add(plannedTripsButton, gbc);

    gbc.gridx = 2;
    add(wishlistButton, gbc);

    // Logout Button
    gbc.gridx = 1;
    gbc.gridy = 2;
    add(logoutButton, gbc);

    String[] planeImages = {
            "/PlanePic1.png", "/PlanePic2.png", "/PlanePic3.png",
    };

    // Set desired mini size for the images
    int miniWidth = 50;  // Set the width for mini images
    int miniHeight = 50; // Set the height for mini images

    // Display the images in the grid
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 3;
    for (int i = 0; i < 3; i++) {
      // Load each plane image
      ImageIcon planeIcon = new ImageIcon(getClass().getResource("/PlanePic.png"));
      if (planeIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
        System.out.println("Image not loaded successfully: " + planeImages[i]);
      }

      // Scale the image to the mini size
      Image scaledImage = planeIcon.getImage().getScaledInstance(miniWidth, miniHeight, Image.SCALE_SMOOTH);
      planeIcon = new ImageIcon(scaledImage);

      // Create a label for each plane image
      JLabel planeLabel = new JLabel(planeIcon);

      // Add the plane label to the grid
      add(planeLabel, gbc);
      gbc.gridx++;  // Move to the next column for the next image

      // After placing the sixth image, move to the next row
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
}
