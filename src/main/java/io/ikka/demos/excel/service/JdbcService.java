package io.ikka.demos.excel.service;

import io.ikka.demos.excel.dto.RowsHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class JdbcService {
    private final JdbcTemplate jdbcTemplate;

    public RowsHolder queryToRowsHolder(String sql) {
        List<String> columnLabels = new ArrayList<>();
        final int[] columnCount = {-1};
        final ResultSetMetaData[] metaData = {null};

        List<Object> results = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    if (metaData[0] == null) {
                        metaData[0] = rs.getMetaData();
                        columnCount[0] = metaData[0].getColumnCount();

                        IntStream
                                .rangeClosed(1, columnCount[0])
                                .forEach(index -> {
                                    try {
                                        columnLabels.add(metaData[0].getColumnLabel(index));
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                });
                    }

                    List<Object> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount[0]; i++) {
                        row.add(rs.getObject(i));
                    }
                    return row;
                });

        return RowsHolder.builder()
                .columnLabels(columnLabels)
                .rows(results)
                .build();
    }
}
