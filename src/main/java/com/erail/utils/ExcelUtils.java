package com.erail.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ExcelUtils {

    private static final String HEADER = "Station Name";

    private ExcelUtils() {
    }

    public static void createExpectedStationsFile(String filePath, List<String> stationNames) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expected Stations");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue(HEADER);

            for (int i = 0; i < stationNames.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(stationNames.get(i));
            }

            sheet.autoSizeColumn(0);

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
        }
    }

    public static void writeActualStationsFile(String filePath, List<String> stationNames) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Actual Stations");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue(HEADER);

            for (int i = 0; i < stationNames.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(stationNames.get(i));
            }

            sheet.autoSizeColumn(0);

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
        }
    }

    public static List<String> readStationNames(String filePath) throws IOException {
        List<String> stationNames = new ArrayList<>();

        try (FileInputStream inputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Cell cell = row.getCell(0);
                if (cell != null) {
                    stationNames.add(cell.getStringCellValue().trim());
                }
            }
        }

        return stationNames;
    }

    public static boolean compareStationLists(List<String> expected, List<String> actual) {
        if (actual.size() < expected.size()) {
            return false;
        }

        for (int i = 0; i < expected.size(); i++) {
            if (!expected.get(i).equalsIgnoreCase(actual.get(i))) {
                return false;
            }
        }
        return true;
    }
}
