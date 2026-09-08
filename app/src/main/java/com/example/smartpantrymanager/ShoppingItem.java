package com.example.smartpantrymanager;

public class ShoppingItem {

    private String id;
    private String name;
    private int quantity;
    private boolean purchased;

    public ShoppingItem() {
        // Required empty constructor for Firebase
    }

    public ShoppingItem(
            String id,
            String name,
            int quantity,
            boolean purchased
    ) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.purchased = purchased;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}