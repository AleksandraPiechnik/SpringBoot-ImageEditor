package com.example.demo.model;






public class Histogram {
    private float R[];
    private float G[];
    private float B[];

    public Histogram(float[] r, float[] g, float[] b) {
        R = r;
        G = g;
        B = b;
    }

    public float[] getR() {
        return R;
    }

    public void setR(float[] r) {
        R = r;
    }

    public float[] getG() {
        return G;
    }

    public void setG(float[] g) {
        G = g;
    }

    public float[] getB() {
        return B;
    }

    public void setB(float[] b) {
        B = b;
    }
}
