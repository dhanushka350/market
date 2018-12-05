package com.akvasoft.market.controller;

import com.akvasoft.market.config.Scrape;
import com.akvasoft.market.modal.Item;
import com.akvasoft.market.modal.Result;
import com.akvasoft.market.repo.ResultRepo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.util.List;

@RestController
@RequestMapping(value = "rest/scraper")
public class Scraper {

    @Autowired
    ResultRepo repo;

    @RequestMapping(value = {"/scrape/homedeport"}, method = RequestMethod.POST)
    @ResponseBody
    private List<Result> scrape(@RequestBody Item item) {
        Scrape scrape = new Scrape();

        List<Result> all = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.homedepot.com/");
        if (all.size() > 0) {
            System.err.println("found in database");
            return all;
        }
        System.out.println(item.getImage());
        try {
            List<Result> list = scrape.scrapeHomeDepot(item.getName(), item.getPrice(), item.getCode(), item.getImage());
            repo.saveAll(list);
            return list;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = {"/scrape/overstock"}, method = RequestMethod.POST)
    @ResponseBody
    private List<Result> scrapeOverStock(@RequestBody Item item) {
        Scrape scrape = new Scrape();
        List<Result> all = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.overstock.com/");
        if (all.size() > 0) {
            System.err.println("found in database");
            return all;
        }
        try {
            List<Result> list = scrape.scrapeOverStock(item.getName(), item.getPrice(), item.getCode(), item.getImage());
            repo.saveAll(list);
            return list;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = {"/scrape/bedbath"}, method = RequestMethod.POST)
    @ResponseBody
    private List<Result> scrapeBedBath(@RequestBody Item item) {
        Scrape scrape = new Scrape();
        List<Result> all = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.bedbathandbeyond.com/");
        if (all.size() > 0) {
            System.err.println("found in database");
            return all;
        }
        try {
            List<Result> list = scrape.scrapeBedBath(item.getName(), item.getPrice(), item.getCode(), item.getImage());
            repo.saveAll(list);
            return list;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = {"/scrape/walmart"}, method = RequestMethod.POST)
    @ResponseBody
    private List<Result> scrapeWalmart(@RequestBody Item item) {
        Scrape scrape = new Scrape();
        List<Result> all = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.walmart.com/");
        if (all.size() > 0) {
            System.err.println("found in database");
            return all;
        }
        try {
            List<Result> list = scrape.scrapeWalmart(item.getName(), item.getPrice(), item.getCode(), item.getImage());
            repo.saveAll(list);
            return list;
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
            return scrape.findAmasonLink(item.getCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


}
