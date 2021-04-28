package space.fedorenko.dbsmartphones;

import com.google.firebase.database.Exclude;

public class Smartphone {
    private String company;
    private String model;
    private double screen;
    private int price;
    private String address;
    private String imageUrl;
    private String key;

    public Smartphone() {}

    public Smartphone(String company, String model, double screen, int price, String address, String imageUrl) {
        this.company = company;
        this.model = model;
        this.screen = screen;
        this.address = address;
        this.price = price;
        this.imageUrl = imageUrl;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public double getScreen() {
        return screen;
    }
    public void setScreen(double screen) {
        this.screen = screen;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
