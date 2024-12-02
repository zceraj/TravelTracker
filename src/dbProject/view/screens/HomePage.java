package dbProject.view.screens;

import javax.swing.*;

import dbProject.DatabaseManager;
import dbProject.TravelController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HomePage extends JPanel {
  private final TravelController controller;
  private final JFrame mainFrame;

  public HomePage(TravelController controller, JFrame mainFrame) {
    this.controller = controller;
    this.mainFrame = mainFrame;
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);

    // Title Label
    JLabel titleLabel = new JLabel("Travel Tracker Home", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    add(titleLabel, gbc);

    // Navigation Buttons
    JButton exploreButton = new JButton("Explore Places");
    JButton plannedTripsButton = new JButton("Planned Trips");
    JButton wishlistButton = new JButton("Wishlist");
    JButton logoutButton = new JButton("Logout");

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

    // Action Listeners for Buttons
    exploreButton.addActionListener((ActionEvent e) -> {
      mainFrame.setContentPane(new ExploreScreen(controller));
      mainFrame.revalidate();
    });

    plannedTripsButton.addActionListener((ActionEvent e) -> {
      mainFrame.setContentPane(new PlannedTripsScreen(controller));
      mainFrame.revalidate();
    });

    wishlistButton.addActionListener((ActionEvent e) -> {
      mainFrame.setContentPane(new WishlistScreen(controller));
      mainFrame.revalidate();
    });

    logoutButton.addActionListener((ActionEvent e) -> {
      JOptionPane.showMessageDialog(this, "Logged out successfully!", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
      mainFrame.setContentPane(new LoginScreen(controller, mainFrame));
      mainFrame.revalidate();
    });
  }
}

