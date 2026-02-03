package com.bcb.webpage.service.sisfiscal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SisFiscalService {

    @Autowired
    @Qualifier("sisfiscalJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<String> getPeriodList() {
        List<String> result = new ArrayList<>();
        String sqlQuery;

        try {
            sqlQuery = "SELECT DISTINCT TO_CHAR(FECHA, 'YYYY-MM-DD') AS PERIODO FROM EstadosdeCuenta ";
            sqlQuery += "ORDER BY PERIODO DESC FETCH FIRST 13 ROWS ONLY";

            result = jdbcTemplate.queryForList(sqlQuery, String.class);

        } catch (Exception e) {
            log.error("SisFiscalService", e);
        }

        return result;
    }

    public List<Map<String,Object>> getStatementAccountPeriods(String contractNumber) {
        List<Map<String,Object>> result = new ArrayList<>();
        String sqlQuery;
        LocalDate aYearAgo = LocalDate.now();
        // A year and a month back in time
        aYearAgo = aYearAgo.minusYears(1).minusMonths(1);

        try {
            sqlQuery = "SELECT * FROM EstadosdeCuenta WHERE CONTRATO = '" + contractNumber + "' ";
            sqlQuery += "AND FECHA >= TO_DATE('" + aYearAgo.format(isoFormatter) + "', 'YYYY-MM-DD') ";
            sqlQuery += " ORDER BY FECHA DESC FETCH FIRST 13 ROWS ONLY";
            System.out.println(sqlQuery);
            result = jdbcTemplate.queryForList(sqlQuery);

        } catch (Exception e) {
            log.error("SisFiscalService", e);
        }

        return result;
    }

    public List<SisfiscalStatementAccount> getStatementAccountList(String contractNumber) {
        String sqlQuery;
        List<SisfiscalStatementAccount> statementAccountList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        try {
            sqlQuery = "SELECT * FROM EstadosdeCuenta WHERE CONTRATO = '" + contractNumber + "' ";
            sqlQuery += "AND FECHA <= TO_DATE('" + currentDate.format(isoFormatter) + "', 'YYYY-MM-DD') ";
            sqlQuery += " ORDER BY FECHA DESC FETCH FIRST 13 ROWS ONLY";

            statementAccountList = jdbcTemplate.query(sqlQuery, new RowMapper<SisfiscalStatementAccount>() {

                @Override
                @Nullable
                public SisfiscalStatementAccount mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                    SisfiscalStatementAccount statementAccount = new SisfiscalStatementAccount();
                    statementAccount.setContractNumber(rs.getString("CONTRATO"));
                    statementAccount.setDate(LocalDate.parse(rs.getString("FECHA").substring(0, 10)));
                    statementAccount.setPdfFile(new String(rs.getBytes("PDF")));
                    statementAccount.setXmlFile(new String(rs.getBytes("XML")));

                    return statementAccount;
                }
            });
        } catch (Exception e) {
            log.error("SisFiscalService", e);
        }

        return statementAccountList;
    }


    public byte[] getFile(String contractNumber, Integer type, LocalDate date) {
        String sqlQuery;
        String field = type == 1 ? "PDF" : "XML";

        sqlQuery = "SELECT " + field + " FROM EstadosdeCuenta WHERE CONTRATO = '" + contractNumber + "' ";
        sqlQuery += "AND FECHA = TO_DATE('" + date.format(isoFormatter) + "', 'YYYY-MM-DD')";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> extractBytes(rs, field));
    }
    
    private byte[] extractBytes(ResultSet rs, String field) throws SQLException {
        var blob = rs.getBlob(field);

        if (blob != null) {
            return blob.getBytes(1, (int) blob.length());
        }
        return null;
    }

}
