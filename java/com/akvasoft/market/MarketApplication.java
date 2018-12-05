package com.akvasoft.market;

import com.akvasoft.market.config.Scrape;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MarketApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication.run(MarketApplication.class, args);
        Scrape scrape = new Scrape();
        try {
            scrape.initialize();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MarketApplication.class);
    }
}
