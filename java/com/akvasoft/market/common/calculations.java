package com.akvasoft.market.common;

import java.text.DecimalFormat;

import static jdk.nashorn.internal.objects.NativeMath.round;

public class calculations {

    private double salesTax = 10.00;
    private double amasonFee = 15.00;
    DecimalFormat df = new DecimalFormat("#.##");

    public Double getCOGS(String vendor_price, String shipping_cost, String amason_price) {
        double amasonPrice = Double.parseDouble(amason_price.replace("$", ""));
        double vendorPrice = Double.parseDouble(vendor_price.replace("$", ""));
        double shippingCost = Double.parseDouble(shipping_cost.replace("$", ""));
        double v = (vendorPrice + shippingCost) * (1 + salesTax) + (amasonPrice * amasonFee);
        return Double.valueOf(df.format(v));
    }

    public Double getProfit(String amason_price, String cogs) {
        double amasonPrice = Double.parseDouble(amason_price.replace("$", ""));
        double Cogz = Double.parseDouble(cogs.replace("$", ""));
        return Double.valueOf(df.format(amasonPrice - Cogz));
    }

    public Double getMargin(String profit, String amason_price) {
        double amasonPrice = Double.parseDouble(amason_price.replace("$", ""));
        double prof = Double.parseDouble(profit.replace("$", ""));

        return Double.valueOf(df.format(prof / amasonPrice * 100));
    }

    public Double getROI(String profit, String vendor_price, String shipping_cost) {
        double prof = Double.parseDouble(profit.replace("$", ""));
        double vendorPrice = Double.parseDouble(vendor_price.replace("$", ""));
        double shippingCost = Double.parseDouble(shipping_cost.replace("$", ""));
        System.out.println(Double.valueOf(df.format((prof * (1 + salesTax)) / (vendorPrice + shippingCost))) + "|||||||||||||");
        return Double.valueOf(df.format((prof * (1 + salesTax)) / (vendorPrice + shippingCost)));
    }

    public Double getShippingCost(String site, String vendor_price) {
        double shippingCost = 0.0;
        double vendorPrice = 0.0;
        try {
            vendorPrice = Double.parseDouble(vendor_price.replace("$", ""));
        } catch (NumberFormatException c) {
            System.out.println("NUMBER FORMAT EXCEPTION " + vendor_price);
            vendorPrice = Double.parseDouble(vendor_price.split(" ")[0].replace(",", "."));
            System.out.println("RESET TO " + vendorPrice);
        }
        if ("Home Depot".equalsIgnoreCase(site)) {
            if (vendorPrice < 45) {
                shippingCost = 5.99;
            } else {
                shippingCost = 0.0;
            }
        }

        if ("Overstock".equalsIgnoreCase(site)) {
            if (vendorPrice < 45) {
                shippingCost = 4.95;
            } else {
                shippingCost = 0.0;
            }
        }
        if ("walmart".equalsIgnoreCase(site)) {
            if (vendorPrice < 35) {
                shippingCost = 5.99;
            } else {
                shippingCost = 0.0;
            }
        }
        if ("bedbathandbeyond".equalsIgnoreCase(site)) {
            if (vendorPrice < 19) {
                shippingCost = 5.99;
            } else {
                shippingCost = 0.0;
            }
        }

        return shippingCost;
    }
}
