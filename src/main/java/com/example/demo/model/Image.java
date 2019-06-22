package com.example.demo.model;

public class Image {
    public String ID;
    public double height;
    public  double width;

    public Image(String ID, double height, double width) {
        this.ID = ID;
        this.height = height;
        this.width = width;
    }


    @Override
    public String toString() {
        return "{id:"+ ID  + ", height:" + height + ", width:" + width + "}";
    }
}
