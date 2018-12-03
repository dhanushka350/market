package com.akvasoft.market.config;

import com.akvasoft.market.common.calculations;
import com.akvasoft.market.modal.Result;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

//        findAmasonLink("B006YSCA8C");
//        scrapeHomeDepot("ARM & HAMMER Clumping Litter Ultra Last 40lb");
//        scrapeOverStock("Arm & Hammer Clump & Seal Multi-cat Light Litter");
//        scrapeBedBath("Arm & Hammer Clump & Seal Multi-cat Light Litter");
//        scrapeWalmart("Majik 3-in-1 Arcade Sport Center, Basketball, Football, & Baseball");
        return "driver initialized";
    }

    public List<Result> scrapeWalmart(String item,String amasonPrice,String code) throws InterruptedException {
        System.out.println("scraping wallmart?????????????????????????????????????????????????????????????????????????????");
        System.out.println("scraping wallmart?????????????????????????????????????????????????????????????????????????????");
        System.out.println("scraping wallmart?????????????????????????????????????????????????????????????????????????????");
        System.out.println("scraping wallmart?????????????????????????????????????????????????????????????????????????????");
        System.out.println("scraping wallmart?????????????????????????????????????????????????????????????????????????????");
        calculations calculations = new calculations();
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        List<Result> res = new ArrayList<>();
        String product_link;
        String vendor_price;
        String shipping_cost;
        String cogs;
        String profit;
        String margin;
        String roi;
        Result result = null;
        while (true) {
            driver.get("https://www.walmart.com/");
            WebElement searchBox = driver.findElementByCssSelector("#global-search-input");
            searchBox.clear();
            searchBox.sendKeys(item);
            searchBox.sendKeys(Keys.ENTER);
            Thread.sleep(5000);
            int count = 1;
            try {
                for (WebElement product : driver.findElementByCssSelector(".search-result-gridview-items").findElements(By.xpath("./*"))) {

                    vendor_price = product.findElement(By.tagName("div")).findElement(By.className("search-result-gridview-item")).findElements(By.xpath("./*")).get(6).findElement(By.className("price-group")).getAttribute("innerText");
                    product_link = product.findElement(By.tagName("div")).findElement(By.className("search-result-gridview-item")).findElements(By.xpath("./*")).get(4).findElement(By.tagName("a")).getAttribute("href");
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
                    result.setShippingcost(shipping_cost);
                    result.setVendorprice(vendor_price);
                    res.add(result);

                    System.out.println(product_link);
                    System.out.println(vendor_price);
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
                    item = item.substring(0, item.lastIndexOf(" "));
                    System.out.println("SEARCHING ITEM = " + item);
                    continue;
                } catch (StringIndexOutOfBoundsException r) {
                    // no item found
                    break;
                }
            }
            break;

        }
        driver.close();
        return res;
    }

    public List<Result> scrapeBedBath(String item,String amasonPrice,String code) throws InterruptedException {
        calculations calculations = new calculations();
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        List<Result> res = new ArrayList<>();
        Result result = null;
        String product_link;
        String vendor_price;
        String shipping_cost;
        String cogs;
        String profit;
        String margin;
        String roi;
        while (true) {
            driver.get("https://www.bedbathandbeyond.com/");
            Thread.sleep(10000);
            try {
                WebElement alert1 = driver.findElementByCssSelector(".rclCloseBtnWrapper");
                WebElement alert2 = driver.findElementByXPath("//*[@id=\"closeButton\"]");
                jse.executeScript("arguments[0].click();", alert1);
                jse.executeScript("arguments[0].click();", alert2);
                break;
            } catch (Exception f) {
                f.printStackTrace();
            }
        }

        while (true) {
            try {
                WebElement searchBox = driver.findElementByXPath("//*[@id=\"searchInput\"]");
                searchBox.clear();
                searchBox.sendKeys(item);
                searchBox.sendKeys(Keys.ENTER);
            } catch (ElementNotInteractableException e) {
                try {
                    driver.findElementByXPath("/html/body/div[13]/div/div/div[2]/div/button").click();
                    driver.findElementByXPath("//*[@id=\"closeButton\"]").click();
                } catch (Exception f) {
                    System.out.println("ALERT EXCEPTION");
                }
                continue;
            }

            WebElement currency = driver.findElementByCssSelector("button.LinkListHandler_1GDLlO");
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


            Thread.sleep(5000);
            System.out.println(driver.findElementByCssSelector(".SearchResultsFound_11h7WU").getAttribute("innerText"));
            if ("NO SEARCH RESULTS FOR".equalsIgnoreCase(driver.findElementByCssSelector(".SearchResultsFound_11h7WU").getAttribute("innerText"))) {
                item = item.substring(0, item.lastIndexOf(" "));
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
                }
            } catch (Exception d) {
                item = item.substring(0, item.lastIndexOf(" "));
                System.out.println("SEARCHING ITEM = " + item);
                continue;
            }
            for (String link : list) {
                driver.get(link);
                product_link = link;
                vendor_price = "$ " + driver.findElementByCssSelector(".ProductDetailsLayout_7n4K2X > div:nth-child(3) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > span:nth-child(1)").getAttribute("innerText").split(" ")[1];
                shipping_cost = "$ " + calculations.getShippingCost("bedbathandbeyond", vendor_price);
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
                result.setShippingcost(shipping_cost);
                result.setVendorprice(vendor_price);
                res.add(result);
                System.out.println(product_link);
                System.out.println(vendor_price);
                System.out.println(shipping_cost);
                System.out.println(cogs);
                System.out.println(profit);
                System.out.println(margin);
                System.out.println(roi);
            }


            break;
        }
        driver.close();
        return res;
    }

    public List<Result> scrapeOverStock(String item,String amasonPrice,String code) throws InterruptedException {
        driver.get("https://www.overstock.com/");
        Thread.sleep(5000);
        WebElement countryChange = driver.findElementByXPath("/html/body/div[1]/div[1]/header/nav/div[1]/div/div[2]/a");
        driver.get(countryChange.getAttribute("href"));
        WebElement america = driver.findElementByXPath("/html/body/div[3]/div/div/div[1]/div/div[2]/div[1]/div[2]/div[2]/ul/li[24]/a");
        driver.get(america.getAttribute("href"));
        List<Result> res = new ArrayList<>();
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
        while (!found) {
            WebElement searchBox = driver.findElementByXPath("//*[@id=\"search-input\"]");
            WebElement searchButton = driver.findElementByXPath("/html/body/div[1]/div[1]/header/nav/div[2]/div/div[2]/form/fieldset[2]/button");

            searchBox.clear();
            searchBox.sendKeys(item);
            try {
                searchButton.click();
            } catch (ElementClickInterceptedException e) {
                driver.navigate().refresh();
                continue;
            }
            Thread.sleep(1000);

            String message = driver.findElementByXPath("/html/body/div[1]/div[2]/div/div/div[1]").getAttribute("innerText");
            if (!message.contains("returned no results")) {

                WebElement result = driver.findElementByXPath("/html/body/div[1]/div[2]/div/div/div[2]/div[2]/div[2]").findElement(By.id("product-container")).findElements(By.xpath("./*")).get(1);
                List<WebElement> items = result.findElements(By.xpath("./*"));
                int count = 1;
                List<String> list = new ArrayList<>();
                for (WebElement product : items) {
                    try {
                        String url = product.findElement(By.tagName("a")).getAttribute("href");
                        list.add(url);
                    } catch (StaleElementReferenceException r) {
                        r.printStackTrace();
                        continue;
                    }
                    if (count > 4) {
                        break;
                    }
                    count++;
                }

                for (String s : list) {
                    driver.get(s);
                    product_link = driver.getCurrentUrl();
                    vendor_price = "$ " + driver.findElementByXPath("/html/body/div[1]/div[3]/section[1]/section[1]/div/div[2]/div[3]/div/form/div[1]/div[1]/div/section/div[1]/div/span[2]/span").getAttribute("content");
                    shipping_cost = "$ " + calculations.getShippingCost("Overstock", vendor_price);
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
                    result1.setShippingcost(shipping_cost);
                    result1.setVendorprice(vendor_price);
                    res.add(result1);

                    System.out.println("UPC CODE = ");
                    System.out.println("AMAZON LINK = ");
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

            item = item.substring(0, item.lastIndexOf(" "));
            System.out.println("SEARCHING ITEM = " + item);
        }
        driver.close();
        return res;
    }

    public List<Result> scrapeHomeDepot(String item,String amasonPrice,String code) throws InterruptedException {
        driver.get("https://www.homedepot.com/");
        List<Result> res = new ArrayList<>();
        calculations calculations = new calculations();
        boolean found = false;
        String product_link = "";
        String vendor_price;
        String shipping_cost;
        String cogs;
        String profit;
        String margin;
        String roi;
        Result result1 = new Result();
        while (!found) {
            WebElement searchBox = driver.findElementByXPath("//*[@id=\"headerSearch\"]");
            WebElement searchButton = driver.findElementByXPath("//*[@id=\"headerSearchButton\"]");

            searchBox.clear();
            searchBox.sendKeys(item);
            searchButton.click();
            Thread.sleep(5000);

            try {
                String message = driver.findElementByXPath("/html/body/div[1]/div[2]/div/div[2]/div/div[1]/div[1]/div[1]/h1").getAttribute("innerText");
            } catch (NoSuchElementException e) {

                try {
                    WebElement price = driver.findElementByXPath("//*[@id=\"ajaxPrice\"]");
                    List<WebElement> prices = price.findElements(By.xpath("./*"));
                    vendor_price = prices.get(0).getAttribute("innerText") + prices.get(1).getAttribute("innerText") + "." + prices.get(2).getAttribute("innerText");
                    product_link = driver.getCurrentUrl();

                    shipping_cost = "$ " + calculations.getShippingCost("Home Depot", vendor_price);
                    cogs = "$ " + calculations.getCOGS(vendor_price, shipping_cost, amasonPrice);
                    profit = "$ " + calculations.getProfit(amasonPrice, cogs);
                    margin = "$ " + calculations.getMargin(profit, amasonPrice);
                    roi = "$ " + calculations.getROI(profit, vendor_price, shipping_cost);


                    result1.setCogs(cogs);
                    result1.setMargin(margin);
                    result1.setProductlink(product_link);
                    result1.setProfit(profit);
                    result1.setRoi(roi);
                    result1.setShippingcost(shipping_cost);
                    result1.setVendorprice(vendor_price);
                    res.add(result1);

                    System.out.println("UPC CODE = ");
                    System.out.println("AMAZON LINK = ");
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
                        String url = info.findElements(By.xpath("./*")).get(0).findElement(By.tagName("a")).getAttribute("href");
                        urls.add(url);
                        if (count > 4) {
                            break;
                        }
                        count++;
                    }

                    // scrape products
                    Result result = null;
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
                        result.setShippingcost(shipping_cost);
                        result.setVendorprice(vendor_price);
                        res.add(result);

                        System.out.println("UPC CODE = ");
                        System.out.println("AMAZON LINK = ");
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

            item = item.substring(0, item.lastIndexOf(" "));
            System.out.println("SEARCHING ITEM = " + item);
        }
        System.out.println(res.size() + "|||||||||||||||||||||||");
        driver.close();
        return res;
    }

    private String findAmasonLink(String item) throws InterruptedException {
        driver.get("http://www.amazon-asin.com/asincheck/");
        WebElement searchBox = driver.findElementByXPath("/html/body/div[1]/section/div/div/form/div/input");
        searchBox.clear();
        searchBox.sendKeys(item);

        WebElement searchButton = driver.findElementByXPath("/html/body/div[1]/section/div/div/form/div/span");
        searchButton.click();

        Thread.sleep(3000);
        WebElement view = driver.findElementByXPath("/html/body/div[1]/div[1]/div[1]/section[2]/div/div[1]/div/div[2]/div/div[2]/div/div[5]/div/a");
        String link = view.getAttribute("href");
        return link;
    }
}
