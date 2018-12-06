package com.akvasoft.market.modal;

import javax.persistence.*;

@Entity
@Table(name = "result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "amazon_link",length = 2000)
    private String amazonLink;
    @Column(name = "image_link",length = 2000)
    private String imageLink;
    @Column(name = "code")
    private String code;
    @Column(name = "asin")
    private String asin;
    @Column(name = "website",length = 2000)
    private String website;
    @Column(name = "product_link",length = 2000)
    private String productlink;
    @Column(name = "vendor_price")
    private String vendorprice;
    @Column(name = "shipping_cost")
    private String shippingcost;
    @Column(name = "cogs")
    private String cogs;
    @Column(name = "profit")
    private String profit;
    @Column(name = "margin")
    private String margin;
    @Column(name = "roi")
    private String roi;
    @Column(name = "search_date")
    private String date;

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAmazonLink() {
        return amazonLink;
    }

    public void setAmazonLink(String amazonLink) {
        this.amazonLink = amazonLink;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getProductlink() {
        return productlink;
    }

    public void setProductlink(String productlink) {
        this.productlink = productlink;
    }

    public String getVendorprice() {
        return vendorprice;
    }

    public void setVendorprice(String vendorprice) {
        this.vendorprice = vendorprice;
    }

    public String getShippingcost() {
        return shippingcost;
    }

    public void setShippingcost(String shippingcost) {
        this.shippingcost = shippingcost;
    }

    public String getCogs() {
        return cogs;
    }

    public void setCogs(String cogs) {
        this.cogs = cogs;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
