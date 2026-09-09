package com.example.smartpantrymanager;

public class PantryItem {

    private String id;
    private String name;
    private String category;
    private int quantity;
    private String unit;
    private String expiryDate;
    private int lowStockLevel;
    private String location;

    public PantryItem() {
        // Required empty constructor for Firebase.
    }

    public PantryItem(
            String id,
            String name,
            String category,
            int quantity,
            String expiryDate,
            int lowStockLevel,
            String location
    ) {
        this(
                id,
                name,
                category,
                quantity,
                "",
                expiryDate,
                lowStockLevel,
                location
        );
    }

    public PantryItem(
            String id,
            String name,
            String category,
            int quantity,
            String unit,
            String expiryDate,
            int lowStockLevel,
            String location
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.lowStockLevel = lowStockLevel;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getLowStockLevel() {
        return lowStockLevel;
    }

    public void setLowStockLevel(int lowStockLevel) {
        this.lowStockLevel = lowStockLevel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}