package models;

public class FlatTypeDetails {
private int units;
private double price;

public FlatTypeDetails(int units, double price) {
this.units = units;
this.price = price;
}

public int getUnits() {
return units;
}

public void setUnits(int units) {
this.units = units;
}

public double getPrice() {
return price;
}

public void setPrice(double price) {
this.price = price;
}
}