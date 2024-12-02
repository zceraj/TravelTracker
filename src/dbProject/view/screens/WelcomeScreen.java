package dbProject.view.screens;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import dbProject.TravelController;

public class WelcomeScreen extends JPanel {
  TravelController travelController;

  public WelcomeScreen(TravelController travelController) {
    this.travelController = travelController;

    // Set soft pink background
    setBackground(Color.decode("#F8BBD0"));
    setLayout(new BorderLayout());

    // Create welcome message with updated styling
    JLabel welcomeLabel = new JLabel("Hi, Welcome to Zara and Arpitha's Travel Tracker!", SwingConstants.CENTER);
    welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
    welcomeLabel.setForeground(Color.decode("#D81B60")); // Darker pink for text contrast
    welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding for better layout

    // Create login button with a cute design
    JButton loginButton = new JButton("Login Here");
    loginButton.setFont(new Font("Arial", Font.PLAIN, 20));
    loginButton.setBackground(Color.decode("#D81B60")); // Pink background for button
    loginButton.setForeground(Color.WHITE); // White text for better contrast
    loginButton.setFocusPainted(false);
    loginButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    loginButton.setPreferredSize(new Dimension(200, 50)); // Adjust button size

    // Add action listener to button
    loginButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        travelController.showNext("login");
      }
    });

    // Add components to the panel
    add(welcomeLabel, BorderLayout.CENTER);

    // Create a panel for the button with a matching background
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.decode("#F8BBD0")); // Match the background
    buttonPanel.add(loginButton);

    // Add the button panel to the bottom of the screen
    add(buttonPanel, BorderLayout.SOUTH);
  }
}
