package com.erail.utils;

import com.erail.model.LoginData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class LoginExcelUtils {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private LoginExcelUtils() {
    }

    public static void createDefaultLoginDataFile(String filePath) throws IOException {
        List<LoginData> defaultData = Arrays.asList(
                new LoginData("TC001", "Admin", "admin123", "Valid"),
                new LoginData("TC002", "Admin", "wrongpass", "Invalid"),
                new LoginData("TC003", "invaliduser", "admin123", "Invalid"),
                new LoginData("TC004", "Admin", "", "Invalid"),
                new LoginData("TC005", "", "admin123", "Invalid")
        );
        writeLoginDataFile(filePath, defaultData);
    }

    public static void writeLoginDataFile(String filePath, List<LoginData> loginDataList) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Login Data");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("TestCaseID");
            header.createCell(1).setCellValue("Username");
            header.createCell(2).setCellValue("Password");
            header.createCell(3).setCellValue("ExpectedResult");

            for (int i = 0; i < loginDataList.size(); i++) {
                LoginData data = loginDataList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(data.getTestCaseId());
                row.createCell(1).setCellValue(data.getUsername());
                row.createCell(2).setCellValue(data.getPassword());
                row.createCell(3).setCellValue(data.getExpectedResult());
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
        }
    }

    public static List<LoginData> readLoginData(String filePath) throws IOException {
        List<LoginData> loginDataList = new ArrayList<>();

        try (FileInputStream inputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String testCaseId = getCellValue(row.getCell(0));
                String username = getCellValue(row.getCell(1));
                String password = getCellValue(row.getCell(2));
                String expectedResult = getCellValue(row.getCell(3));

                if (testCaseId.isEmpty()) {
                    continue;
                }

                loginDataList.add(new LoginData(testCaseId, username, password, expectedResult));
            }
        }

        return loginDataList;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return FORMATTER.formatCellValue(cell).trim();
    }
}
