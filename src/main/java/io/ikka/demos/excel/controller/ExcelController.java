package io.ikka.demos.excel.controller;

import io.ikka.demos.excel.dto.RowsHolder;
import io.ikka.demos.excel.dto.SqlStatement;
import io.ikka.demos.excel.service.ExcelService;
import io.ikka.demos.excel.service.JdbcService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.File;
import java.io.FileInputStream;

@RequiredArgsConstructor
@Controller
public class ExcelController {
    private final ExcelService excelService;
    private final JdbcService jdbcService;

    @SneakyThrows
    @PostMapping("/excel/download")
    public ResponseEntity<Resource> getFile(@ModelAttribute SqlStatement sqlStatement, Model model) {
        var filename = "temp.xlsx";
        String fileLocation = getFileLocation(filename);

        RowsHolder rowsHolder = jdbcService.queryToRowsHolder(sqlStatement.getSql());
        excelService.generateReport(rowsHolder, fileLocation);
        var file = new InputStreamResource(new FileInputStream(fileLocation));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    private String getFileLocation(String filename) {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + filename;
        return fileLocation;
    }

    @GetMapping({"/excel"})
    public String excel(Model model) {
        SqlStatement sqlStatement = new SqlStatement();
        sqlStatement.setSql("select * from instruments");
        model.addAttribute("sqlStatement", sqlStatement);
        return "excel";
    }
}
