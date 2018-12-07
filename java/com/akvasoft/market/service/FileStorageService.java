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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    @Autowired
    private Scrape scrape;
    @Autowired
    private Skipped skipped;
    @Autowired
    private ResultRepo repo;

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
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            saveList(fileName);
            return fileName;
        } catch (IOException ex) {
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

    public void saveList(String file) {
        List<Item> read = scrape.scrapeExcel(file);
        Result last = repo.findFirstByOrderByIdDesc();
        boolean savedPointFound = false;
        int e = 0;
        SkippedProducts products = null;
        for (Item item : read) {
            if (e == 0) {
                e++;
                continue;
            }


            if (last != null) {
                if (last.getCode().equalsIgnoreCase(item.getCode()) && last.getAsin().equalsIgnoreCase(item.getAsin())) {
                    System.out.println("found last saved point");
                } else {
                    System.out.println("looking for a save point");
                    continue;
                }
            }

            if (item.getCode().contains("skipped")) {
                products = new SkippedProducts();
                products.setUpc(item.getCode());
                products.setAsin(item.getAsin());
                skipped.save(products);
                System.out.println("skipped saved");
                continue;
            }
            try {

                List<Result> homede = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.homedepot.com/");
                if (homede.size() > 0) {
                    System.out.println("HOME DEPORT ALREADY SCRAPED.");
                } else {
                    List<Result> list = scrape.scrapeHomeDepot(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin());
                    repo.saveAll(list);
                }

                List<Result> over = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.overstock.com/");
                if (over.size() > 0) {
                    System.out.println("HOME OVERSTOCK ALREADY SCRAPED.");
                } else {
                    List<Result> list1 = scrape.scrapeOverStock(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin());
                    repo.saveAll(list1);
                }

                List<Result> bed = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.bedbathandbeyond.com/");
                if (bed.size() > 0) {
                    System.out.println("HOME BEDBATH ALREADY SCRAPED.");
                } else {
                    List<Result> list2 = scrape.scrapeBedBath(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin());
                    repo.saveAll(list2);
                }

                List<Result> walmart = repo.findAllByCodeEqualsAndWebsiteEquals(item.getCode(), "https://www.walmart.com/");
                if (walmart.size() > 0) {
                    System.out.println("HOME WALMART ALREADY SCRAPED.");
                } else {
                    List<Result> list3 = scrape.scrapeWalmart(item.getName(), item.getPrice(), item.getCode(), item.getImage(), item.getAsin());
                    repo.saveAll(list3);
                }


            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.out.println("CODE == " + item.getCode());
            System.out.println("ASIN == " + item.getAsin());
            System.out.println("NAME == " + item.getName());
            System.out.println("PRICE == " + item.getPrice());
//                if (e == 3) {
//                    break;
//                }
            e++;
        }

    }
}
