package com.akvasoft.market.controller;

import com.akvasoft.market.config.Scrape;
import com.akvasoft.market.modal.Item;
import com.akvasoft.market.modal.Result;
import com.akvasoft.market.modal.SkippedProducts;
import com.akvasoft.market.repo.ResultRepo;
import com.akvasoft.market.repo.Skipped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "rest/scraper")
public class Scraper implements InitializingBean {

    @Autowired
    ResultRepo repo;
    @Autowired
    Skipped skipped;

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
            List<Result> list = scrape.scrapeHomeDepot(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin());
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
            List<Result> list = scrape.scrapeOverStock(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin());
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
            List<Result> list = scrape.scrapeBedBath(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin());
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
            List<Result> list = scrape.scrapeWalmart(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin());
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
            return scrape.findAmasonLink(item.getAsin());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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
        list.add("Vendor Price");
        list.add("Shipping Cost");
        list.add("COGS");
        list.add("Profit");
        list.add("Margin");
        list.add("ROI");

        list.add("Overstock Product Link");
        list.add("Vendor Price");
        list.add("Shipping Cost");
        list.add("COGS");
        list.add("Profit");
        list.add("Margin");
        list.add("ROI");

//        list.add("Bed Bath & Beyond Product Link");
//        list.add("Vendor Price");
//        list.add("Shipping Cost");
//        list.add("COGS");
//        list.add("Profit");
//        list.add("Margin");
//        list.add("ROI");

        list.add("Walmart Product Link");
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

            for (int i = 0; i < 5; i++) {
                Result hh = new Result();
                Result oo = new Result();
                Result ww = new Result();
                try {
                    hh = homeDepot.get(i);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("array out of bound");
                }

                try {
                    oo = oversock.get(i);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("array out of bound");
                }

                try {
                    ww = walmart.get(i);
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
                row.createCell(3).setCellValue(hh.getVendorprice());
                row.createCell(4).setCellValue(hh.getShippingcost());
                row.createCell(5).setCellValue(hh.getCogs());
                row.createCell(6).setCellValue(hh.getProfit());
                row.createCell(7).setCellValue(hh.getMargin());
                row.createCell(8).setCellValue(hh.getRoi());

                row.createCell(9).setCellValue(oo.getProductlink());
                row.createCell(10).setCellValue(oo.getVendorprice());
                row.createCell(11).setCellValue(oo.getShippingcost());
                row.createCell(12).setCellValue(oo.getCogs());
                row.createCell(13).setCellValue(oo.getProfit());
                row.createCell(14).setCellValue(oo.getMargin());
                row.createCell(15).setCellValue(oo.getRoi());

                row.createCell(16).setCellValue(ww.getProductlink());
                row.createCell(17).setCellValue(ww.getVendorprice());
                row.createCell(18).setCellValue(ww.getShippingcost());
                row.createCell(19).setCellValue(ww.getCogs());
                row.createCell(20).setCellValue(ww.getProfit());
                row.createCell(21).setCellValue(ww.getMargin());
                row.createCell(22).setCellValue(ww.getRoi());

            }
            max++;
//            if (max > 2) {
//                break;
//            }

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

    @Override
    public void afterPropertiesSet() throws Exception {
//        createExcel();
    }
}
