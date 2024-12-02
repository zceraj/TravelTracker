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

public class PlannedTripsScreen extends JPanel {
  private final TravelController controller;

  public PlannedTripsScreen(TravelController controller) {
    this.controller = controller;
    setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("Planned Trips", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    add(titleLabel, BorderLayout.NORTH);

    // List of planned trips
    JList<String> tripsList = new JList<>();
    JScrollPane listScrollPane = new JScrollPane(tripsList);
    add(listScrollPane, BorderLayout.CENTER);

    // Action buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton editButton = new JButton("Edit Trip");
    JButton deleteButton = new JButton("Delete Trip");
    JButton toggleCompletedButton = new JButton("Mark as Completed");

    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(toggleCompletedButton);
    add(buttonPanel, BorderLayout.SOUTH);

    // Populate the list with planned trips
    refreshTripsList(tripsList);

    // Action listeners
    editButton.addActionListener((ActionEvent e) -> {
      String selectedTrip = tripsList.getSelectedValue();
      if (selectedTrip != null) {
        String newDetails = JOptionPane.showInputDialog(this, "Edit trip details:", selectedTrip);
        if (newDetails != null) {
          controller.editPlannedTrip(selectedTrip, newDetails);
          refreshTripsList(tripsList);
          JOptionPane.showMessageDialog(this, "Trip updated.");
        }
      } else {
        JOptionPane.showMessageDialog(this, "Please select a trip!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    deleteButton.addActionListener((ActionEvent e) -> {
      String selectedTrip = tripsList.getSelectedValue();
      if (selectedTrip != null) {
        controller.deletePlannedTrip(selectedTrip);
        refreshTripsList(tripsList);
        JOptionPane.showMessageDialog(this, "Trip deleted.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a trip!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    toggleCompletedButton.addActionListener((ActionEvent e) -> {
      String selectedTrip = tripsList.getSelectedValue();
      if (selectedTrip != null) {
        controller.toggleTripCompletion(selectedTrip);
        JOptionPane.showMessageDialog(this, "Trip completion status toggled.");
      } else {
        JOptionPane.showMessageDialog(this, "Please select a trip!", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  private void refreshTripsList(JList<String> tripsList) {
    String[] trips = controller.getPlannedTrips().toArray(new String[0]);
    tripsList.setListData(trips);
  }
}
