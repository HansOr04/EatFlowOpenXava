package com.grupo4.Eatflow.util;

public class OrderDiscountCalculator {

    public static double calculateFinalAmount(int totalItems, double subtotal) {
        if (totalItems < 0) {
            throw new IllegalArgumentException("totalItems cannot be negative");
        }
        if (subtotal < 0) {
            throw new IllegalArgumentException("subtotal cannot be negative");
        }

        double discountRate;
        if (totalItems >= 20) {
            discountRate = 0.15;
        } else if (totalItems >= 10) {
            discountRate = 0.10;
        } else if (totalItems >= 5) {
            discountRate = 0.05;
        } else {
            discountRate = 0.0;
        }

        return subtotal * (1 - discountRate);
    }
}
