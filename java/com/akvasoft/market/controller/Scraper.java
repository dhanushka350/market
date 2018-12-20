package com.akvasoft.market.controller;

import com.akvasoft.market.config.Scrape;
import com.akvasoft.market.modal.Item;
import com.akvasoft.market.modal.Result;
import com.akvasoft.market.modal.SkippedProducts;
import com.akvasoft.market.repo.ResultRepo;
import com.akvasoft.market.repo.Skipped;
import com.akvasoft.market.service.FileStorageService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "rest/scraper")
public class Scraper implements InitializingBean {

    @Autowired
    ResultRepo repo;
    @Autowired
    Skipped skipped;
    @Autowired
    FileStorageService storageService;

    private static FirefoxDriver driver = null;
    private static String url[] = {"http://www.amazon-asin.com/asincheck/"};
    private static String codes[] = {"Products"};
    private static HashMap<String, String> handlers = new HashMap<>();


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

    @RequestMapping(value = {"/scrape/homedeport"}, method = RequestMethod.POST)
    @ResponseBody
    private List<Result> scrape(@RequestBody Item item) {
        Scrape scrape = new Scrape();
        System.out.println(item.getAsin() + "=" + item.getCode() + "=" + item.getName());
        List<Result> all = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.homedepot.com/");
        if (all.size() > 0) {
            System.err.println("found in database");
            return all;
        }

        try {
            initialize();
            List<Result> list = scrape.scrapeHomeDepot(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin(), driver);
            repo.saveAll(list);
            return list;
        } catch (Exception e) {
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
            if (driver == null) {
                initialize();
            }
            List<Result> list = scrape.scrapeOverStock(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin(), driver);
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
            if (driver == null) {
                initialize();
            }
            List<Result> list = scrape.scrapeBedBath(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin(), driver);
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
            if (driver == null) {
                initialize();
            }
            List<Result> list = scrape.scrapeWalmart(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin(), driver);
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

//        try {
////            return scrape.findAmasonLink(item.getAsin());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    @RequestMapping(value = {"/scrape/excel/status"}, method = RequestMethod.GET)
    @ResponseBody
    private String excelStatus() {
        return storageService.currentProduct;
    }

    public void saveSkippedItems(SkippedProducts products) {
        skipped.save(products);
    }

    public void createExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("market data");
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        Row headerRow = sheet.createRow(0);
        ArrayList<String> list = new ArrayList();

        list.add("UPC Code");
        list.add("Amazon Link");

        list.add("Home Depot Product Link");
        list.add("Home Depot Image Link");
        list.add("Vendor Price");
        list.add("Shipping Cost");
        list.add("COGS");
        list.add("Profit");
        list.add("Margin");
        list.add("ROI");

        list.add("Overstock Product Link");
        list.add("Overstock Image Link");
        list.add("Vendor Price");
        list.add("Shipping Cost");
        list.add("COGS");
        list.add("Profit");
        list.add("Margin");
        list.add("ROI");

        list.add("Walmart Product Link");
        list.add("Walmart Image Link");
        list.add("Vendor Price");
        list.add("Shipping Cost");
        list.add("COGS");
        list.add("Profit");
        list.add("Margin");
        list.add("ROI");

        list.add("Bed Bath & Beyond Product Link");
        list.add("Bed Bath & Beyond Image Link");
        list.add("Vendor Price");
        list.add("Shipping Cost");
        list.add("COGS");
        list.add("Profit");
        list.add("Margin");
        list.add("ROI");

        for (int i = 0; i < list.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(list.get(i));
            cell.setCellStyle(headerCellStyle);
        }


        List<Result> clist = new ArrayList<>();

        for (Result result : repo.findAll()) {
            boolean found = false;

            for (Result result1 : clist) {
                if (result1.getCode().equalsIgnoreCase(result.getCode())) {
                    System.out.println("====================================SKIPPED");
                    found = true;
                    break;
                }
            }

            if (!found) {
                clist.add(result);
                System.err.println("ADDED" + result.getCode());
            }
        }


        int rowNum = 1;
        int max = 0;

        for (Result result : clist) {
            System.err.println(result.getCode() + "===========================");
            List<Result> homeDepot = repo.findAllByCodeEqualsAndWebsiteEquals(result.getCode(), "https://www.homedepot.com/");
            List<Result> oversock = repo.findAllByCodeEqualsAndWebsiteEquals(result.getCode(), "https://www.overstock.com/");
            List<Result> walmart = repo.findAllByCodeEqualsAndWebsiteEquals(result.getCode(), "https://www.walmart.com/");
            List<Result> bedbath = repo.findAllByCodeEqualsAndWebsiteEquals(result.getCode(), "https://www.bedbathandbeyond.com/");

            for (int i = 0; i < 5; i++) {
                Result hh = new Result();
                Result oo = new Result();
                Result ww = new Result();
                Result bb = new Result();
                try {

                    hh = setCodeLength(homeDepot.get(i));
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("array out of bound");
                }

                try {
                    oo = setCodeLength(oversock.get(i));
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("array out of bound");
                }

                try {
                    ww = setCodeLength(walmart.get(i));
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("array out of bound");
                }
                try {
                    bb = setCodeLength(bedbath.get(i));
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("array out of bound");
                }

                Row row = sheet.createRow(rowNum++);
                if (i == 0) {
                    row.createCell(0).setCellValue(result.getCode());
                    row.createCell(1).setCellValue(result.getAmazonLink());
                } else {
                    row.createCell(0).setCellValue("");
                    row.createCell(1).setCellValue("");
                }
                row.createCell(2).setCellValue(hh.getProductlink());
                row.createCell(3).setCellValue(hh.getImageLink());
                row.createCell(4).setCellValue(hh.getVendorprice());
                row.createCell(5).setCellValue(hh.getShippingcost());
                row.createCell(6).setCellValue(hh.getCogs());
                row.createCell(7).setCellValue(hh.getProfit());
                row.createCell(8).setCellValue(hh.getMargin());
                row.createCell(9).setCellValue(hh.getRoi());

                row.createCell(10).setCellValue(oo.getProductlink());
                row.createCell(11).setCellValue(oo.getImageLink());
                row.createCell(12).setCellValue(oo.getVendorprice());
                row.createCell(13).setCellValue(oo.getShippingcost());
                row.createCell(14).setCellValue(oo.getCogs());
                row.createCell(15).setCellValue(oo.getProfit());
                row.createCell(16).setCellValue(oo.getMargin());
                row.createCell(17).setCellValue(oo.getRoi());

                row.createCell(18).setCellValue(ww.getProductlink());
                row.createCell(19).setCellValue(ww.getImageLink());
                row.createCell(20).setCellValue(ww.getVendorprice());
                row.createCell(21).setCellValue(ww.getShippingcost());
                row.createCell(22).setCellValue(ww.getCogs());
                row.createCell(23).setCellValue(ww.getProfit());
                row.createCell(24).setCellValue(ww.getMargin());
                row.createCell(25).setCellValue(ww.getRoi());

                row.createCell(26).setCellValue(bb.getProductlink());
                row.createCell(27).setCellValue(bb.getImageLink());
                row.createCell(28).setCellValue(bb.getVendorprice());
                row.createCell(29).setCellValue(bb.getShippingcost());
                row.createCell(30).setCellValue(bb.getCogs());
                row.createCell(31).setCellValue(bb.getProfit());
                row.createCell(32).setCellValue(bb.getMargin());
                row.createCell(33).setCellValue(bb.getRoi());

            }
            max++;
            if (max > 10) {
                break;
            }

        }

        for (int i = 0; i < list.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        new File("/var/lib/tomcat8/current").mkdir();
        new File("/var/lib/tomcat8/history/market").mkdirs();
        FileOutputStream fileOut = new FileOutputStream("/var/lib/tomcat8/current/market.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private Result setCodeLength(Result result) {

        int length = result.getCode().length();
        if (length < 12) {
            while (result.getCode().length() < 12) {
                System.out.println("adding zero");
                String code = result.getCode();
                result.setCode("0" + code);
            }
        }
        System.out.println(result.getCode() + "===========================================================================");
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        createExcel();
    }
}
