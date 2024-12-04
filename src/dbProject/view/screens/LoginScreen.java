package dbProject.view.screens;

import javax.swing.*;
import java.awt.*;
import dbProject.TravelController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JPanel {
  private final TravelController controller;

  public LoginScreen(TravelController controller) {
    this.controller = controller;

    // Set layout for the login panel with more spacing for proportional appearance
    setLayout(new GridBagLayout());
    setBackground(Color.decode("#F8BBD0")); // Soft pink background color
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10); // Spacing between components

    // Create components with updated fonts and colors
    JLabel usernameLabel = new JLabel("Username:");
    usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    usernameLabel.setForeground(Color.decode("#D81B60")); // Pink color for text

    JTextField usernameField = new JTextField(20);
    usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
    usernameField.setBackground(Color.decode("#F3E5F5")); // Light pink background for the text field

    JLabel passwordLabel = new JLabel("Password:");
    passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
    passwordLabel.setForeground(Color.decode("#D81B60"));

    JPasswordField passwordField = new JPasswordField(20);
    passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
    passwordField.setBackground(Color.decode("#F3E5F5"));

    JButton loginButton = new JButton("Login");
    loginButton.setFont(new Font("Arial", Font.BOLD, 14));
    loginButton.setBackground(Color.decode("#D81B60")); // Pink background for button
    loginButton.setForeground(Color.decode("#D81B60"));
    loginButton.setFocusPainted(false); // Removes focus border

    // Set GridBagLayout positions for better alignment
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(usernameLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    add(usernameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    add(passwordLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    add(passwordField, gbc);

    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(loginButton, gbc);

    // Add action listener to the login button
    loginButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Notify the controller with the entered credentials
        boolean success = controller.authenticateUser(username, password);
        System.out.println("username: " + username + " password: " + password);

        if (success) {
          JOptionPane.showMessageDialog(LoginScreen.this, "Login Successful!");
          controller.showNext("home");

        } else {
          JOptionPane.showMessageDialog(LoginScreen.this, "Invalid Username or Password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
  }
}
