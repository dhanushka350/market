package com.akvasoft.market.modal;

import javax.persistence.*;

@Entity
@Table(name = "items")
public class Products {
    @Id
    @Column(name = "id")
    int id;
    @Column(name = "UPC_Code")
    private String UPC_Code;
    @Column(name = "ASIN")
    private String ASIN;
    @Column(name = "Title")
    private String Title;
    @Column(name = "Price")
    private String Price;
    @Column(name = "status")
    private String status;

    public String getUPC_Code() {
        return UPC_Code;
    }

    public void setUPC_Code(String UPC_Code) {
        this.UPC_Code = UPC_Code;
    }

    public String getASIN() {
        return ASIN;
    }

    public void setASIN(String ASIN) {
        this.ASIN = ASIN;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
