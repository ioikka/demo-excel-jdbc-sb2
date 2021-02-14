package io.ikka.demos.excel.service;

import io.ikka.demos.excel.dto.RowsHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ExcelService {

    public void generateReport(RowsHolder rowsHolder, String fileLocation) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Any");
            //sheet.setColumnWidth(0, 6000);
            //sheet.setColumnWidth(1, 4000);

            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();

            XSSFFont font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            headerStyle.setFont(font);

            List<String> columnLabels = rowsHolder.getColumnLabels();
            for (int i = 0; i < columnLabels.size(); i++) {
                Cell headerCell = header.createCell(i);
                headerCell.setCellValue(columnLabels.get(i));
                headerCell.setCellStyle(headerStyle);
            }

            CellStyle style = workbook.createCellStyle();
//            style.setWrapText(true);

            List<Object> rows = rowsHolder.getRows();
            //setting rows
            for (int i = 0; i < rows.size(); i++) {
                Row row = sheet.createRow(i + 2);
                List<Object> o = (List<Object>) rows.get(i);

                //setting cells
                for (int j = 0; j < o.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(o.get(j) == null ? null : o.get(j).toString());
                    cell.setCellStyle(style);
                }
            }

            try (FileOutputStream outputStream = new FileOutputStream(fileLocation)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
