package dbProject;

import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class WishlistView extends JFrame {
  private final TravelController controller;
  private final DefaultTableModel wishlistTable;
  private final int userId = 1; // Example user ID

  public WishlistView(TravelController controller) {
    this.controller = controller;
    this.wishlistTable = new DefaultTableModel(new String[]{"ID", "Name", "Country"}, 0);

    // Load data and set up the UI
    loadWishlist();
  }

  private void loadWishlist() {
    List<WishListItem> wishlist = controller.getWishlist(userId);
    wishlistTable.setRowCount(0); // Clear existing rows
    for (WishListItem item : wishlist) {
      wishlistTable.addRow(new Object[]{item.getId(), item.getName(), item.getCountry()});
    }
  }

  private void deleteFromWishlist() {
    int selectedRow = wishlistTable.getRowCount();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this, "Please select an item.");
      return;
    }

    int wishlistId = (int) wishlistTable.getValueAt(selectedRow, 0);
    if (controller.deleteFromWishlist(wishlistId)) {
      JOptionPane.showMessageDialog(this, "Item removed from wishlist!");
      loadWishlist();
    } else {
      JOptionPane.showMessageDialog(this, "Failed to remove item.");
    }
  }

  private void moveToPlannedTrips() {
    int selectedRow = wishlistTable.getRowCount();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this, "Please select an item.");
      return;
    }

    int placeId = (int) wishlistTable.getValueAt(selectedRow, 0);
    if (controller.moveToPlannedTrips(userId, placeId)) {
      JOptionPane.showMessageDialog(this, "Place moved to planned trips!");
      loadWishlist();
    } else {
      JOptionPane.showMessageDialog(this, "Failed to move place.");
    }
  }
}
