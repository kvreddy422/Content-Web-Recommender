package com.recommendations.org.springrecommendationlucene.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class getInputImpl implements getInput {

    private static final Logger LOGGER = LoggerFactory.getLogger(getInputImpl.class);


    @Override
    public List<String> getPosts() {
        Workbook workbook = null;
        List<String> posts = new ArrayList<>();
        try {
            workbook = WorkbookFactory.create(new File(getInput.FILENAME));
        } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
            LOGGER.debug("Exception while creating workbook!");
            e.printStackTrace();
        }
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();
        Row row = null;
        Cell cell = null;
        String cellContent = null;
        for(int i=1; i<=sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            cell = row.getCell(1);
            cellContent = dataFormatter.formatCellValue(cell);
            cell = row.getCell(2);
            cellContent += "\n\n" + dataFormatter.formatCellValue(cell);
            posts.add(cellContent);
        }

        return posts;
    }


}
