package com.akvasoft.market.config;

import com.akvasoft.market.common.calculations;
import com.akvasoft.market.modal.Item;
import com.akvasoft.market.modal.Result;
import com.akvasoft.market.repo.ResultRepo;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class Scrape {


    private static FirefoxDriver driver = null;
    private static String url[] = {"http://www.amazon-asin.com/asincheck/"};
    private static String codes[] = {"Products"};
    private static HashMap<String, String> handlers = new HashMap<>();
    JavascriptExecutor jse = (JavascriptExecutor) driver;


    public String initialize() throws InterruptedException {
        System.setProperty("webdriver.gecko.driver", "/var/lib/tomcat8/geckodriver");
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(false);
        driver = new FirefoxDriver(options);

        for (int i = 0; i < url.length - 1; i++) {
            driver.executeScript("window.open()");
        }

        ArrayList<String> windowsHandles = new ArrayList<>(driver.getWindowHandles());

        for (int i = 0; i < url.length; i++) {
            handlers.put(codes[i], windowsHandles.get(i));
        }

        return "driver initialized";
    }

    public List<Result> scrapeWalmart(String item, String amasonPrice, String code, String image, String asin) throws InterruptedException {
        String proName = item;
        int cc = proName.split(" ").length;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        calculations calculations = new calculations();
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        List<Result> res = new ArrayList<>();
        boolean firstAttempt = true;
        String product_link;
        String vendor_price;
        String shipping_cost;
        String cogs;
        String profit;
        String margin;
        String roi;
        Result result = null;
        int search_count = 0;
        try {
            while (true) {
                try {
                    driver.get("https://www.walmart.com/");
                } catch (NullPointerException g) {
                    this.initialize();
                    driver.get("https://www.walmart.com/");
                }
                WebElement searchBox = driver.findElementByCssSelector("#global-search-input");
                searchBox.clear();
                if (search_count == 0) {
                    searchBox.sendKeys(code);
                    searchBox.sendKeys(Keys.ENTER);
                    System.out.println("SEARCH USING UPC CODE BED WALL");
                } else if (search_count == 1) {
                    searchBox.sendKeys(asin);
                    searchBox.sendKeys(Keys.ENTER);
                    System.out.println("SEARCH USING ASIN CODE BED WALL");
                } else {
                    searchBox.sendKeys(item);
                    searchBox.sendKeys(Keys.ENTER);
                }
                Thread.sleep(5000);
                int count = 1;
                search_count++;
                try {
                    for (WebElement product : driver.findElementByXPath("/html/body/div[1]/div/div/div/div[1]/div/section/div[3]/div/div/div[4]/div[2]/div[2]/div").findElements(By.xpath("./*"))) {

                        vendor_price = product.findElement(By.className("tile-content")).findElement(By.className("tile-aside")).findElement(By.className("price-group")).getAttribute("innerText");
                        product_link = product.findElement(By.className("arrange-fill")).findElement(By.className("tile-content")).findElement(By.tagName("a")).getAttribute("href");
                        image = product.findElement(By.tagName("div")).findElement(By.className("arrange-fill")).findElement(By.tagName("img")).getAttribute("src");
                        shipping_cost = "$ " + calculations.getShippingCost("walmart", vendor_price);
                        cogs = "$ " + calculations.getCOGS(vendor_price, shipping_cost, amasonPrice);
                        profit = "$ " + calculations.getProfit(amasonPrice, cogs);
                        margin = "$ " + calculations.getMargin(profit, amasonPrice);
                        roi = "$ " + calculations.getROI(profit, vendor_price, shipping_cost);

                        result = new Result();
                        result.setCogs(cogs);
                        result.setMargin(margin);
                        result.setProductlink(product_link);
                        result.setProfit(profit);
                        result.setRoi(roi);
                        result.setAsin(asin);
                        result.setImageLink(image);
                        result.setShippingcost(shipping_cost);
                        result.setVendorprice(vendor_price);
                        result.setCode(code);
                        result.setWebsite("https://www.walmart.com/");
                        result.setDate(dateFormat.format(date));
                        result.setAmazonLink("https://www.amazon.com/dp/" + code);
                        res.add(result);

                        System.out.println(product_link);
                        System.out.println(result.getImageLink());
                        System.out.println(shipping_cost);
                        System.out.println(cogs);
                        System.out.println(profit);
                        System.out.println(margin);
                        System.out.println(roi);
                        if (count > 4) {
                            break;
                        }
                        count++;
                    }
                } catch (NoSuchElementException e) {
                    try {
                        int ct = 0;
                        for (WebElement element : driver.findElementByXPath("/html/body/div[1]/div/div/div/div[1]/div/section/div[3]/div/div/div[4]/div[2]/div[2]/ul").findElements(By.xpath("./*"))) {
                            String imageLink = element.findElement(By.className("search-result-gridview-item")).findElements(By.xpath("./*")).get(1).findElement(By.tagName("img")).getAttribute("src");
                            vendor_price = element.findElement(By.className("search-result-gridview-item")).findElements(By.xpath("./*")).get(6).findElement(By.tagName("span")).findElement(By.className("price-main-block")).findElement(By.className("price-group")).getAttribute("innerText");
                            String link = element.findElement(By.className("search-result-gridview-item")).findElements(By.xpath("./*")).get(4).findElement(By.tagName("a")).getAttribute("href");

                            shipping_cost = "$ " + calculations.getShippingCost("walmart", vendor_price);
                            cogs = "$ " + calculations.getCOGS(vendor_price, shipping_cost, amasonPrice);
                            profit = "$ " + calculations.getProfit(amasonPrice, cogs);
                            margin = "$ " + calculations.getMargin(profit, amasonPrice);
                            roi = "$ " + calculations.getROI(profit, vendor_price, shipping_cost);

                            result = new Result();
                            result.setCogs(cogs);
                            result.setMargin(margin);
                            result.setProductlink(link);
                            result.setProfit(profit);
                            result.setRoi(roi);
                            result.setAsin(asin);
                            result.setImageLink(imageLink);
                            result.setShippingcost(shipping_cost);
                            result.setVendorprice(vendor_price);
                            result.setCode(code);
                            result.setWebsite("https://www.walmart.com/");
                            result.setDate(dateFormat.format(date));
                            result.setAmazonLink("https://www.amazon.com/dp/" + code);
                            res.add(result);

                            System.out.println(link);
                            System.out.println(result.getImageLink());
                            System.out.println(shipping_cost);
                            System.out.println(cogs);
                            System.out.println(profit);
                            System.out.println(margin);
                            System.out.println(roi);
                            if (ct > 4) {
                                break;
                            }
                            ct++;

                        }
                    } catch (NoSuchElementException b) {
                        //b.printStackTrace();
                        System.out.println("no products found here");
                        try {

                            if (search_count > 1) {
                                if (firstAttempt) {
                                    firstAttempt = false;
                                    continue;
                                }
                                if (item.split(" ").length < 2) {
                                    break;
                                }
                                item = item.substring(0, item.lastIndexOf(" "));
                                System.out.println("SEARCHING ITEM = " + item);
                            }
                            continue;
                        } catch (StringIndexOutOfBoundsException r) {
                            // no item found
                            break;
                        }
                    }
                    e.printStackTrace();

                }
                break;

            }
            driver.quit();
            driver.close();
            return res;
        } catch (Exception t) {
            t.printStackTrace();
            return res;
        }
    }

    public List<Result> scrapeBedBath(String item, String amasonPrice, String code, String image, String asin) throws InterruptedException {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        calculations calculations = new calculations();
        boolean firstAttempt = true;
        List<Result> res = new ArrayList<>();
        Result result = null;
        String product_link;
        String vendor_price;
        String shipping_cost;
        String cogs;
        String profit;
        String margin;
        String roi;

        try {
            boolean al1 = false;
            boolean al2 = false;

            while (true) {
                try {
                    driver.get("https://www.bedbathandbeyond.com/");
                } catch (NullPointerException g) {
                    this.initialize();
                    driver.get("https://www.bedbathandbeyond.com/");
                }

                Thread.sleep(5000);
                try {
                    WebElement addCloseOne = driver.findElementByCssSelector(".rclCloseBtnWrapper");
                    addCloseOne.click();
                    WebElement addCloseTwo = driver.findElementByXPath("//*[@id=\"closeButton\"]");
                    addCloseTwo.click();
                    break;

                } catch (Exception f) {
                    try {
                        driver.findElementByXPath("//*[@id=\"searchInput\"]");
                    } catch (Exception n) {
                        System.out.println("can not click on alert");
                        continue;
                    }
                }
            }
            JavascriptExecutor jse = ((JavascriptExecutor) driver);
            int search_count = -1;
            while (true) {
                try {
                    WebElement searchBox = driver.findElementByXPath("//*[@id=\"searchInput\"]");
                    searchBox.clear();
                    search_count++;
                    if (search_count == 0) {
                        searchBox.sendKeys(code);
                        searchBox.sendKeys(Keys.ENTER);
                        System.out.println("SEARCH USING UPC CODE BED BAT");
                    } else if (search_count == 1) {
                        searchBox.sendKeys(asin);
                        searchBox.sendKeys(Keys.ENTER);
                        System.out.println("SEARCH USING ASIN CODE BED BAT");
                    } else {
                        searchBox.sendKeys(item);
                        searchBox.sendKeys(Keys.ENTER);
                    }
                } catch (ElementNotInteractableException e) {

                    try {
                        driver.findElementByXPath("/html/body/div[13]/div/div/div[2]/div/button").click();
                        driver.findElementByXPath("//*[@id=\"closeButton\"]").click();
                    } catch (Exception f) {
                        try {
                            WebElement addCloseOne = driver.findElementByCssSelector(".rclCloseBtnWrapper");
                            addCloseOne.click();
                            WebElement addCloseTwo = driver.findElementByXPath("//*[@id=\"closeButton\"]");
                            addCloseTwo.click();
                            System.out.println("ALERT EXCEPTION");
                        } catch (Exception v) {
                        }
                    }
                    continue;
                }


                try {
                    WebElement currency = driver.findElementByXPath("/html/body/div[2]/div[2]/div[5]/footer/div/div[2]/div[2]/ul[1]").findElement(By.className("accordion-collapsed")).findElement(By.className("Button_5b9DYQ"));
                    System.out.println(currency.getAttribute("class"));
//                    jse.executeScript("arguments[0].scrollIntoView(true);", currency);
                    jse.executeScript("arguments[0].click();", currency);
                    WebElement btn = driver.findElementByCssSelector("#currencyDropdown-button");
                    jse.executeScript("arguments[0].click();", btn);

                    WebElement usd = driver.findElementByCssSelector("#currencyDropdown > div:nth-child(2) > ul:nth-child(1)");

                    Thread.sleep(5000);
                    for (WebElement element : usd.findElements(By.xpath("./*"))) {
                        if (element.getAttribute("innerText").equalsIgnoreCase("US Dollar")) {
                            element = element.findElement(By.tagName("button"));
                            jse.executeScript("arguments[0].click();", element);
                            System.out.println(element.getAttribute("innerText"));
                            driver.findElementByCssSelector("#updateCountryCrncy").click();
                            break;
                        }
                    }
                } catch (Exception v) {
                    v.printStackTrace();
                    break;
                }


                Thread.sleep(5000);
                System.out.println(driver.findElementByCssSelector(".SearchResultsFound_11h7WU").getAttribute("innerText"));
                if ("NO SEARCH RESULTS FOR".equalsIgnoreCase(driver.findElementByCssSelector(".SearchResultsFound_11h7WU").getAttribute("innerText"))) {
                    try {


                        if (item.split(" ").length < 2) {
                            break;
                        }

                        if (search_count > 1) {
                            if (firstAttempt) {
                                firstAttempt = false;
                                continue;
                            }
                            item = item.substring(0, item.lastIndexOf(" "));
                        }

                    } catch (StringIndexOutOfBoundsException f) {
                        return res;
                    }
                    System.out.println("SEARCHING ITEM = " + item);
                    continue;
                }

                int count = 1;
                List<String> list = new ArrayList<>();
                try {
                    for (WebElement product : driver.findElementByCssSelector(".mt0").findElements(By.xpath("./*"))) {
                        String url = product.findElement(By.tagName("article")).findElement(By.tagName("a")).getAttribute("href");
                        list.add(url);
                        if (count > 4) {
                            break;
                        }
                        count++;
                    }
                } catch (Exception d) {
                    if (search_count > 1) {
                        if (item.split(" ").length < 2) {
                            break;
                        }
                        if (firstAttempt == false) {
                            item = item.substring(0, item.lastIndexOf(" "));
                        }
                        System.out.println("SEARCHING ITEM = " + item);

                    }
                    continue;
                }
                for (String link : list) {
                    try {
                        driver.get(link);
                        Thread.sleep(2000);
                        product_link = link;
                        vendor_price = "$ " + driver.findElementByCssSelector(".ProductDetailsLayout_7n4K2X > div:nth-child(3) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > span:nth-child(1)").getAttribute("innerText").split(" ")[1];
                        shipping_cost = "$ " + calculations.getShippingCost("bedbathandbeyond", vendor_price);
                        image = driver.findElementByXPath("/html/body/div[2]/div[2]/div[3]/main/div/div[1]/div/div/div[1]/div[1]/div/div[1]/div[1]/div[1]/div[2]/div/div/div/a[1]/div").findElement(By.tagName("img")).getAttribute("src");
                        cogs = "$ " + calculations.getCOGS(vendor_price, shipping_cost, amasonPrice);
                        profit = "$ " + calculations.getProfit(amasonPrice, cogs);
                        margin = "$ " + calculations.getMargin(profit, amasonPrice);
                        roi = "$ " + calculations.getROI(profit, vendor_price, shipping_cost);

                        result = new Result();
                        result.setCogs(cogs);
                        result.setMargin(margin);
                        result.setImageLink(image);
                        result.setProductlink(product_link);
                        result.setProfit(profit);
                        result.setRoi(roi);
                        result.setAsin(asin);
                        result.setShippingcost(shipping_cost);
                        result.setVendorprice(vendor_price);
                        result.setCode(code);
                        result.setWebsite("https://www.bedbathandbeyond.com/");
                        result.setDate(dateFormat.format(date));
                        result.setAmazonLink("https://www.amazon.com/dp/" + code);
                        res.add(result);
                        System.out.println(product_link);
                        System.out.println(vendor_price);
                        System.out.println(image + " BET=D =================================");
                        System.out.println(shipping_cost);
                        System.out.println(cogs);
                        System.out.println(profit);
                        System.out.println(margin);
                        System.out.println(roi);
                    } catch (NoSuchElementException c) {
                        continue;
                    }

                }


                break;
            }
            return res;
        } catch (
                Exception t) {
            t.printStackTrace();
            return res;
        }

    }

    public List<Result> scrapeOverStock(String item, String amasonPrice, String code, String image, String asin) throws InterruptedException {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        List<Result> res = new ArrayList<>();
        boolean firstAttempt = true;
        try {
            try {
                driver.get("https://www.overstock.com/");
            } catch (NullPointerException g) {
                this.initialize();
                driver.get("https://www.overstock.com/");
            }

            Thread.sleep(5000);
            try {
                WebElement countryChange = driver.findElementByXPath("/html/body/div[1]/div[1]/header/nav/div[1]/div/div[2]/a");
                driver.get(countryChange.getAttribute("href"));
                WebElement america = driver.findElementByXPath("/html/body/div[3]/div/div/div[1]/div/div[2]/div[1]/div[2]/div[2]/ul/li[24]/a");
                driver.get(america.getAttribute("href"));
            } catch (NoSuchElementException t) {
                System.out.println("country not found");
            }

            calculations calculations = new calculations();
            boolean found = false;
            String product_link;
            String vendor_price;
            String shipping_cost;
            String cogs;
            String profit;
            String margin;
            String roi;
            Result result1 = null;
            int search_count = 0;
            while (!found) {
                WebElement searchBox = driver.findElementByXPath("//*[@id=\"search-input\"]");
                WebElement searchButton = driver.findElementByXPath("/html/body/div[1]/div[1]/header/nav/div[2]/div/div[2]/form/fieldset[2]/button");

                searchBox.clear();
                if (search_count == 0) {
                    searchBox.sendKeys(code);
                    System.out.println("SEARCH USING UPC CODE");
                } else if (search_count == 1) {
                    searchBox.sendKeys(asin);
                    System.out.println("SEARCH USING ASIN CODE");
                } else {
                    searchBox.sendKeys(item);
                }

                try {
                    searchButton.click();
                } catch (ElementClickInterceptedException e) {
                    driver.navigate().refresh();
                    continue;
                }

                search_count++;
                Thread.sleep(10000);
                String message = null;
                try {
                    message = driver.findElementByXPath("/html/body/div[1]/div[2]/div/div/div[1]").getAttribute("innerText");
                } catch (NoSuchElementException t) {
                    message = "ok";
                }
                if (!message.contains("returned no results")) {

                    WebElement result = driver.findElementByXPath("//*[@id=\"1\"]");
                    List<WebElement> items = result.findElements(By.xpath("./*"));
                    System.out.println(items.size());
                    int count = 1;
                    List<String> list = new ArrayList<>();
                    for (WebElement product : items) {
                        try {
                            String url = product.findElement(By.tagName("a")).getAttribute("href");
                            list.add(url);
                            System.out.println("add link");
                            if (count > 4) {
                                break;
                            }
                            count++;
                        } catch (StaleElementReferenceException r) {
                            r.printStackTrace();
                            continue;
                        }

                    }

                    for (String s : list) {
                        driver.get(s);
                        product_link = driver.getCurrentUrl();
                        Thread.sleep(2000);
                        vendor_price = "$ " + driver.findElementByXPath("/html/body/div[1]/div[3]/section[1]/section[1]/div/div[2]/div[3]/div/form/div[1]/div[1]/div/section/div[1]/div/span[2]/span").getAttribute("content");
                        shipping_cost = "$ " + calculations.getShippingCost("Overstock", vendor_price);
                        image = driver.findElementByXPath("/html/body/div[1]/div[3]/section[1]/section[1]/div/div[1]/div[1]/div/div[1]/div[2]/div/div[1]").findElement(By.tagName("img")).getAttribute("src");
                        cogs = "$ " + calculations.getCOGS(vendor_price, shipping_cost, amasonPrice);
                        profit = "$ " + calculations.getProfit(amasonPrice, cogs);
                        margin = "$ " + calculations.getMargin(profit, amasonPrice);
                        roi = "$ " + calculations.getROI(profit, vendor_price, shipping_cost);
                        result1 = new Result();
                        result1.setCogs(cogs);
                        result1.setMargin(margin);
                        result1.setProductlink(product_link);
                        result1.setProfit(profit);
                        result1.setRoi(roi);
                        result1.setAsin(asin);
                        result1.setImageLink(image);
                        result1.setShippingcost(shipping_cost);
                        result1.setVendorprice(vendor_price);
                        result1.setCode(code);
                        result1.setWebsite("https://www.overstock.com/");
                        result1.setDate(dateFormat.format(date));
                        result1.setAmazonLink("https://www.amazon.com/dp/" + code);
                        res.add(result1);

                        System.out.println("UPC CODE = ");
                        System.out.println("AMAZON LINK = " + image);
                        System.out.println("LINK = " + product_link);
                        System.out.println("VENDOR PRICE = " + vendor_price);
                        System.out.println("SHIPPING COST = " + shipping_cost);
                        System.out.println("COGS = " + cogs);
                        System.out.println("PROFIT = " + profit);
                        System.out.println("MARGIN = " + margin);
                        System.out.println("ROI = " + roi);
                        System.err.println("========================================");
                        System.err.println("========================================");
                        System.err.println("========================================");
                        found = true;
                    }


                }
                if (search_count > 1) {
                    try {
                        if (item.split(" ").length < 2) {
                            break;
                        }
                        if (firstAttempt) {
                            firstAttempt = false;
                            continue;
                        }
                        item = item.substring(0, item.lastIndexOf(" "));
                        System.out.println("SEARCHING ITEM = " + item);
                    } catch (StringIndexOutOfBoundsException s) {
                        return res;
                    }
                }
            }
            return res;
        } catch (Exception t) {
            t.printStackTrace();
            return res;
        }
    }

    public List<Result> scrapeHomeDepot(String item, String amasonPrice, String code, String image, String asin) throws InterruptedException {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        this.initialize();
        List<Result> res = new ArrayList<>();
        boolean firstAttempt = true;
        try {

            driver.get("https://www.homedepot.com/");
            Thread.sleep(2000);
            calculations calculations = new calculations();
            Result result = null;
            boolean found = false;
            String product_link = "";
            String vendor_price;
            String shipping_cost;
            String cogs;
            String profit;
            String margin;
            String roi;
            Result result1 = null;
            int search_count = 0;
            while (!found) {


                WebElement searchBox = driver.findElementByXPath("//*[@id=\"headerSearch\"]");
                WebElement searchButton = driver.findElementByXPath("//*[@id=\"headerSearchButton\"]");

                searchBox.clear();
                if (search_count == 0) {
                    System.out.println("SEARCH UPC CODE IN HOMEDEPOT");
                    searchBox.sendKeys(code);
                } else if (search_count == 1) {
                    System.out.println("SEARCH UPC ASIN IN HOMEDEPOT");
                    searchBox.sendKeys(asin);
                } else {
                    searchBox.sendKeys(item);
                }

                searchButton.click();
                Thread.sleep(5000);
                search_count++;
                try {
                    String message = driver.findElementByXPath("/html/body/div[1]/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/h1").getAttribute("innerText");
                } catch (NoSuchElementException e) {

                    try {
                        WebElement price = driver.findElementByXPath("//*[@id=\"ajaxPrice\"]");
                        List<WebElement> prices = price.findElements(By.xpath("./*"));
                        vendor_price = prices.get(0).getAttribute("innerText") + prices.get(1).getAttribute("innerText") + "." + prices.get(2).getAttribute("innerText");
                        product_link = driver.getCurrentUrl();
                        image = driver.findElementByXPath("//*[@id=\"mediaPlayer\"]").findElement(By.tagName("img")).getAttribute("src");
                        shipping_cost = "$ " + calculations.getShippingCost("Home Depot", vendor_price);
                        cogs = "$ " + calculations.getCOGS(vendor_price, shipping_cost, amasonPrice);
                        profit = "$ " + calculations.getProfit(amasonPrice, cogs);
                        margin = "$ " + calculations.getMargin(profit, amasonPrice);
                        roi = "$ " + calculations.getROI(profit, vendor_price, shipping_cost);

                        result1 = new Result();
                        result1.setCogs(cogs);
                        result1.setMargin(margin);
                        result1.setProductlink(product_link);
                        result1.setProfit(profit);
                        result1.setRoi(roi);
                        result1.setAsin(asin);
                        result1.setShippingcost(shipping_cost);
                        result1.setVendorprice(vendor_price);
                        result1.setCode(code);
                        result1.setImageLink(image);
                        result1.setWebsite("https://www.homedepot.com/");
                        result1.setDate(dateFormat.format(date));
                        result1.setAmazonLink("https://www.amazon.com/dp/" + code);
                        res.add(result1);

                        System.out.println("UPC CODE = ");
                        System.out.println("IMAGE = " + image);
                        System.out.println("LINK = " + product_link);
                        System.out.println("VENDOR PRICE = " + vendor_price);
                        System.out.println("SHIPPING COST = " + shipping_cost);
                        System.out.println("COGS = " + cogs);
                        System.out.println("PROFIT = " + profit);
                        System.out.println("MARGIN = " + margin);
                        System.out.println("ROI = " + roi);
                        System.err.println("========================================");
                        System.err.println("========================================");
                        System.err.println("========================================");

                    } catch (NoSuchElementException r) {
                        // collect five product urls..
                        WebElement list = driver.findElementByXPath("/html/body/div[1]/div[2]/div/div[1]/div[5]/div[2]/div[2]/div[1]/div[1]/div/div");
                        List<String> urls = new ArrayList<>();
                        int count = 1;
                        for (WebElement product : list.findElements(By.xpath("./*"))) {
                            WebElement info = product.findElement(By.className("plp-pod__info"));
                            WebElement imageElement = product.findElement(By.className("plp-pod__image"));
                            image = imageElement.findElement(By.tagName("a")).getAttribute("href");
                            String url = info.findElements(By.xpath("./*")).get(0).findElement(By.tagName("a")).getAttribute("href");
                            urls.add(url);
                            if (count > 4) {
                                break;
                            }
                            count++;
                        }

                        // scrape products

                        for (String link : urls) {
                            result = new Result();
                            driver.get(link);
                            Thread.sleep(2000);
                            WebElement price = driver.findElementByXPath("//*[@id=\"ajaxPrice\"]");
                            List<WebElement> prices = price.findElements(By.xpath("./*"));
                            vendor_price = prices.get(0).getAttribute("innerText") + prices.get(1).getAttribute("innerText") + "." + prices.get(2).getAttribute("innerText");
                            product_link = driver.getCurrentUrl();
                            System.err.println(product_link + vendor_price);

                            shipping_cost = "$ " + calculations.getShippingCost("Home Depot", vendor_price);
                            cogs = "$ " + calculations.getCOGS(vendor_price, shipping_cost, amasonPrice);
                            profit = "$ " + calculations.getProfit(amasonPrice, cogs);
                            margin = "$ " + calculations.getMargin(profit, amasonPrice);
                            roi = "$ " + calculations.getROI(profit, vendor_price, shipping_cost);

                            result.setCogs(cogs);
                            result.setMargin(margin);
                            result.setProductlink(product_link);
                            result.setProfit(profit);
                            result.setRoi(roi);
                            result.setAsin(asin);
                            result.setShippingcost(shipping_cost);
                            result.setVendorprice(vendor_price);
                            result.setCode(code);
                            result.setImageLink(image);
                            result.setWebsite("https://www.homedepot.com/");
                            result.setDate(dateFormat.format(date));
                            result.setAmazonLink("https://www.amazon.com/dp/" + code);
                            res.add(result);

                            System.out.println("UPC CODE = ");
                            System.out.println("IMAGE = ====" + image);
                            System.out.println("LINK = " + product_link);
                            System.out.println("VENDOR PRICE = " + vendor_price);
                            System.out.println("SHIPPING COST = " + shipping_cost);
                            System.out.println("COGS = " + cogs);
                            System.out.println("PROFIT = " + profit);
                            System.out.println("MARGIN = " + margin);
                            System.out.println("ROI = " + roi);
                            System.err.println("========================================");
                            System.err.println("========================================");
                            System.err.println("========================================");

                        }

                    }
                    break;
                }

                if (search_count > 1) {
                    if (item.split(" ").length < 2) {
                        break;
                    }
                    if (firstAttempt) {
                        firstAttempt = false;
                        continue;
                    }
                    item = item.substring(0, item.lastIndexOf(" "));
                    System.out.println("SEARCHING ITEM = " + item);
                }
            }
            System.out.println(res.size() + "|||||||||||||||||||||||");
            return res;
        } catch (Exception t) {
            t.printStackTrace();
            return res;
        }
    }


    public String findAmasonLink(String item) throws InterruptedException {

        System.out.println("AMASON SEARCH CODE " + item);
        driver.get("https://www.amazon.com/dp/" + item);
        WebElement searchBox = driver.findElementByCssSelector("#priceblock_ourprice");
        String href = searchBox.getAttribute("innerText");
        driver.close();
        return "https://www.amazon.com/dp/" + item + "  " + href;
    }

    public List<Item> scrapeExcel(String file) {
        System.out.println("scaping excel");
        ExcelReader reader = new ExcelReader();
        List<Item> read = null;
        try {
            read = reader.read(file);
            System.out.println(read.size());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return read;
    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        this.initialize();
//    }
}
