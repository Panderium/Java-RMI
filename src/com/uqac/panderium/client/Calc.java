package com.uqac.panderium.client;

import java.io.Serializable;

public class Calc implements Serializable {

    public double add(double a, double b) {
        return a + b;
    }

    public double multiply(double a, double b) {
        return a * b;
    }

    public double substract(double a, double b) {
        return a - b;
    }

    public double divide(double a, double b) {
        return a / b;
    }

}
