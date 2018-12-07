package com.akvasoft.market.config;

import com.akvasoft.market.controller.Scraper;
import com.akvasoft.market.modal.Item;
import com.akvasoft.market.modal.SkippedProducts;
import com.akvasoft.market.repo.Skipped;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

@Controller
public class ExcelReader {

    public List<Item> read(String file) throws Exception {
        List<Item> list = new ArrayList<>();

        String filename = "/var/lib/tomcat8/uploads/" + file;
        FileInputStream fis = null;
        System.out.println(file);

        try {

            fis = new FileInputStream(filename);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator rowIter = sheet.rowIterator();
            Item item = null;

            while (rowIter.hasNext()) {
                item = new Item();
                XSSFRow myRow = (XSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                Vector<String> cellStoreVector = new Vector<String>();
                while (cellIter.hasNext()) {
                    XSSFCell myCell = (XSSFCell) cellIter.next();
                    String cellvalue = "";
                    try {
                        cellvalue = myCell.getStringCellValue();
                    } catch (IllegalStateException e) {
                        cellvalue = myCell.getNumericCellValue() + "";
                    }

                    cellStoreVector.addElement(cellvalue);
                }
                String firstcolumnValue = null;
                String secondcolumnValue = "";
                String thirdcolumnValue = "";
                String fourthcolumnValue = "";

                int i = 0;
                try {
                    firstcolumnValue = cellStoreVector.get(i).toString();
                    secondcolumnValue = cellStoreVector.get(i + 1).toString();
                    thirdcolumnValue = cellStoreVector.get(i + 2).toString();
                    fourthcolumnValue = cellStoreVector.get(i + 3).toString();
                } catch (ArrayIndexOutOfBoundsException r) {
                    item.setCode(firstcolumnValue + "skipped");
                    item.setAsin(secondcolumnValue + "skipped");
                    item.setName(thirdcolumnValue + "skipped");
                    item.setPrice(fourthcolumnValue + "skipped");
                    list.add(item);
                    continue;
                }

                item.setCode(firstcolumnValue);
                item.setAsin(secondcolumnValue);
                item.setName(thirdcolumnValue);
                item.setPrice(fourthcolumnValue);
                list.add(item);

            }


        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (fis != null) {

                fis.close();

            }

        }

        return list;

    }


}
