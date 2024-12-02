package dbProject.view.screens;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import dbProject.DatabaseManager;
import dbProject.TravelController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginScreen extends JPanel {
  private final TravelController controller;
  private final JFrame mainFrame;

  public LoginScreen(TravelController controller, JFrame mainFrame) {
    this.controller = controller;
    this.mainFrame = mainFrame;
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);

    // Title Label
    JLabel titleLabel = new JLabel("Travel Tracker Login", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    add(titleLabel, gbc);

    // Username Field
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 1;
    add(new JLabel("Username:"), gbc);

    JTextField usernameField = new JTextField(20);
    gbc.gridx = 1;
    add(usernameField, gbc);

    // Password Field
    gbc.gridx = 0;
    gbc.gridy = 2;
    add(new JLabel("Password:"), gbc);

    JPasswordField passwordField = new JPasswordField(20);
    gbc.gridx = 1;
    add(passwordField, gbc);

    // Login Button
    JButton loginButton = new JButton("Login");
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    add(loginButton, gbc);

    // Error Label
    JLabel errorLabel = new JLabel("", JLabel.CENTER);
    errorLabel.setForeground(Color.RED);
    gbc.gridy = 4;
    add(errorLabel, gbc);

    // Action Listener for Login Button
    loginButton.addActionListener((ActionEvent e) -> {
      String username = usernameField.getText().trim();
      String password = new String(passwordField.getPassword()).trim();

      if (controller.authenticateUser(username, password)) {
        // Navigate to HomeScreen on successful login
        JOptionPane.showMessageDialog(this, "Login successful!", "Welcome", JOptionPane.INFORMATION_MESSAGE);
        mainFrame.setContentPane(new HomeScreen(controller, mainFrame));
        mainFrame.revalidate();
      } else {
        // Display error message on failed login
        errorLabel.setText("Invalid username or password. Please try again.");
      }
    });
  }
}
