package com.akvasoft.market.modal;

import javax.persistence.*;

@Entity
@Table(name = "skipped_product")
public class SkippedProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "upc",length = 2000)
    private String upc;
    @Column(name = "asin")
    private String asin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }
}
