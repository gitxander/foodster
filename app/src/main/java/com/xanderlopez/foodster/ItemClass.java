package com.xanderlopez.foodster;

public class ItemClass {

    private int id;
    private String name, description, image;
    private Double price;

    public ItemClass() {}

    /* Constructor */
    public ItemClass(String name, String description, String image) {
        //this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
