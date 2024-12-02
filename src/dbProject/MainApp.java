package dbProject;

import java.awt.*;

import javax.swing.*;

import dbProject.view.screens.ExploreScreen;
import dbProject.view.screens.LoginScreen;
import dbProject.view.screens.PlannedTripsScreen;
import dbProject.view.screens.WishlistScreen;

public class MainApp {
  public static void main(String[] args) {
    // Create the main application frame
    JFrame mainFrame = new JFrame("Travel Tracker");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(800, 600);

    // Initialize the controller
    TravelController controller = new TravelController();

    // Set the initial screen to the LoginScreen
    mainFrame.setContentPane(new LoginScreen(controller, mainFrame));
    mainFrame.setVisible(true);
  }
}
