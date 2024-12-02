package dbProject;

public class WishListItem {
  private final int id;
  private final String name;
  private final String country;

  public WishListItem(int id, String name, String country) {
    this.id = id;
    this.name = name;
    this.country = country;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCountry() {
    return country;
  }

  @Override
  public String toString() {
    return name + " (" + country + ")";
  }
}
