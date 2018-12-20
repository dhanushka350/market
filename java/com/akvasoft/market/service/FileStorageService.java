package com.akvasoft.market.service;

import com.akvasoft.market.config.Scrape;
import com.akvasoft.market.exception.FileStorageException;
import com.akvasoft.market.exception.MyFileNotFoundException;
import com.akvasoft.market.modal.Item;
import com.akvasoft.market.modal.Result;
import com.akvasoft.market.modal.SkippedProducts;
import com.akvasoft.market.property.FileStorageProperties;
import com.akvasoft.market.repo.ResultRepo;
import com.akvasoft.market.repo.Skipped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    private static FirefoxDriver driver = null;
    private static String url[] = {"http://www.amazon-asin.com/asincheck/"};
    private static String codes[] = {"Products"};
    private static HashMap<String, String> handlers = new HashMap<>();
    public static String currentProduct = "scraper is idle mode";

    @Autowired
    private Scrape scrape;
    @Autowired
    private Skipped skipped;
    @Autowired
    private ResultRepo repo;

    public String initialize() throws InterruptedException {
        System.setProperty("webdriver.gecko.driver", "/var/lib/tomcat8/geckodriver");
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
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

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        System.out.println(file.getName() + "========================");
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                currentProduct = "Sorry! Filename contains invalid path sequence ";
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            currentProduct = fileName + " uploaded.";
            saveList(fileName);
            return fileName;
        } catch (IOException | InterruptedException ex) {
            currentProduct = "Could not store file " + fileName + ". Please try again!";
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

    public void saveList(String file) throws IOException, InterruptedException {
        currentProduct = "initializing...";
        List<Item> read = scrape.scrapeExcel(file);
        currentProduct = "reading file..";
        Result last = repo.findFirstByOrderByIdDesc();
        boolean savedPointFound = false;
        int e = 0;
        SkippedProducts products = null;


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

        try {
            this.initialize();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        for (int i = 0; i < list.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(list.get(i));
            cell.setCellStyle(headerCellStyle);
        }
        int rowNum = 1;
        int max = 0;
        List<Result> homeDepot = null;
        List<Result> oversock = null;
        List<Result> bedbath = null;
        List<Result> walmart = null;
        int size = read.size();
        currentProduct = "found " + size + " products";
        Thread.sleep(1000);
        for (Item item : read) {

            currentProduct = "scraping :- https://www.amazon.com/dp/" + item.getAsin() + " - " + (size - max) + " items left.";

            if (max == 0) {
                max++;
                continue;
            }

            if (item.getCode().contains("skipped")) {
                currentProduct = "skipping https://www.amazon.com/dp/" + item.getAsin() + " - price not specified.";
                continue;
            }

            homeDepot = new ArrayList<>();
            oversock = new ArrayList<>();
            bedbath = new ArrayList<>();
            walmart = new ArrayList<>();


            try {

                homeDepot = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.homedepot.com/");
                if (homeDepot.size() > 0) {
                    System.out.println("HOME DEPORT ALREADY SCRAPED.");
                } else {
                    homeDepot = scrape.scrapeHomeDepot(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin(), driver);
                    repo.saveAll(homeDepot);

                }
                currentProduct = "HOME DEPOT IS DONE!";

                oversock = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.overstock.com/");
                if (oversock.size() > 0) {
                    System.out.println("HOME OVERSTOCK ALREADY SCRAPED.");
                } else {
                    oversock = scrape.scrapeOverStock(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin(), driver);
                    repo.saveAll(oversock);
                }
                currentProduct = "OVERSTOCK IS DONE!";

                bedbath = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.bedbathandbeyond.com/");
                if (bedbath.size() > 0) {
                    System.out.println("HOME BEDBATH ALREADY SCRAPED.");
                } else {
                    bedbath = scrape.scrapeBedBath(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin(), driver);
                    repo.saveAll(bedbath);
                }
                currentProduct = "BEDBATHHANDBEYOND IS DONE!";

                walmart = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.walmart.com/");
                if (walmart.size() > 0) {
                    System.out.println("HOME WALMART ALREADY SCRAPED.");
                } else {
                    walmart = scrape.scrapeWalmart(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin(), driver);
                    repo.saveAll(walmart);
                }
                currentProduct = "WALMART IS DONE!";
//                driver.close();

            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.out.println("CODE == " + item.getCode());
            System.out.println("ASIN == " + item.getAsin());
            System.out.println("NAME == " + item.getName());
            System.out.println("PRICE == " + item.getPrice());


            for (int i = 0; i < 5; i++) {
                Result hh = new Result();
                Result oo = new Result();
                Result ww = new Result();
                Result bb = new Result();
                try {

                    hh = setCodeLength(homeDepot.get(i));
                } catch (IndexOutOfBoundsException u) {
                    System.out.println("array out of bound");
                }

                try {
                    oo = setCodeLength(oversock.get(i));
                } catch (IndexOutOfBoundsException u) {
                    System.out.println("array out of bound");
                }

                try {
                    ww = setCodeLength(walmart.get(i));
                } catch (IndexOutOfBoundsException u) {
                    System.out.println("array out of bound");
                }
                try {
                    bb = setCodeLength(bedbath.get(i));
                } catch (IndexOutOfBoundsException u) {
                    System.out.println("array out of bound");
                }

                Row row = sheet.createRow(rowNum++);
                if (i == 0) {
                    row.createCell(0).setCellValue(item.getCode());
                    row.createCell(1).setCellValue("https://www.amazon.com/dp/" + item.getAsin());
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
            if (max > 50) {
                break;
            }
        }

        for (int i = 0; i < list.size(); i++) {
            sheet.autoSizeColumn(i);
        }

//        new File("/var/lib/tomcat8/current").mkdir();
//        new File("/var/lib/tomcat8/history/market").mkdirs();
        currentProduct = "creating result sheet...";
        FileOutputStream fileOut = new FileOutputStream("/var/lib/tomcat8/uploads/" + file);
        workbook.write(fileOut);
        currentProduct = "writing to excel...";
        fileOut.close();
        workbook.close();
        driver.close();
        currentProduct = "DONE!";
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
}
