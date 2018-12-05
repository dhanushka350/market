package com.akvasoft.market.controller;

import com.akvasoft.market.config.Scrape;
import com.akvasoft.market.modal.Item;
import com.akvasoft.market.modal.Result;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.util.List;

@RestController
@RequestMapping(value = "rest/scraper")
public class Scraper implements InitializingBean {


    @RequestMapping(value = {"/scrape/homedeport"}, method = RequestMethod.POST)
    @ResponseBody
    private List<Result> scrape(@RequestBody Item item) {
        Scrape scrape = new Scrape();
        try {
            System.out.println(scrape.initialize());
            return scrape.scrapeHomeDepot(item.getName(), item.getPrice(), item.getCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = {"/scrape/overstock"}, method = RequestMethod.POST)
    @ResponseBody
    private List<Result> scrapeOverStock(@RequestBody Item item) {
        Scrape scrape = new Scrape();
        try {
            System.out.println(scrape.initialize());
            return scrape.scrapeOverStock(item.getName(), item.getPrice(), item.getCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = {"/scrape/bedbath"}, method = RequestMethod.POST)
    @ResponseBody
    private List<Result> scrapeBedBath(@RequestBody Item item) {
        Scrape scrape = new Scrape();
        try {
            System.out.println(scrape.initialize());
            return scrape.scrapeBedBath(item.getName(), item.getPrice(), item.getCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = {"/scrape/walmart"}, method = RequestMethod.POST)
    @ResponseBody
    private List<Result> scrapeWalmart(@RequestBody Item item) {
        Scrape scrape = new Scrape();
        try {
            System.out.println(scrape.initialize());
            return scrape.scrapeWalmart(item.getName(), item.getPrice(), item.getCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = {"/scrape/amazon/price"}, method = RequestMethod.POST)
    @ResponseBody
    private String scrapeAmazonPrice(@RequestBody Item item) {
        Scrape scrape = new Scrape();
        try {
            System.out.println(scrape.initialize());
            return scrape.findAmasonLink(item.getCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        this.scrape();
    }
}
