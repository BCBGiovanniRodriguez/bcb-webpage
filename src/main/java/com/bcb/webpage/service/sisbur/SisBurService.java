package com.bcb.webpage.service.sisbur;

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
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import com.bcb.webpage.dto.response.position.Posicion;
import com.bcb.webpage.model.webpage.dto.GeneralDTO;
import com.bcb.webpage.model.webpage.dto.interfaces.PositionInterface;
import com.bcb.webpage.service.sisbur.model.CustomerCashBalance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SisBurService {

    @Autowired
    @Qualifier("sisburJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String CURRENCY_TYPE_MXN = "MXN";

    public static final String CURRENCY_TYPE_USD = "USD";

    public static final Integer MARKET_TYPE_STOCK_MARKET = 1;

    public static final Integer MARKET_TYPE_MONEY_MARKET = 2;

    public static final Integer MARKET_TYPE_INVESTMENT_FUNDS = 3;

    private static final String[] marketTypes = {"", "Mercado de Capitales", "Mercado de Dinero", "Fondos de Inversión"};

    private static final String dailyPositionTable = "PosicionDiaV";



    public Double getPendingBalance(Integer contractNumber) {
        String sqlQuery;
        Double pendingBalance = 0D;

        try {
            sqlQuery = "SELECT Sum(importesucio - importeisr - importeiva) FROM MovimientosPendientes ";
            sqlQuery += "WHERE Contrato = '" + contractNumber + "' ";

            pendingBalance = jdbcTemplate.queryForObject(sqlQuery, Double.class);
        } catch (Exception e) {
            log.error("", e);
        }

        return pendingBalance;
    }

    public Double getSoldBalance(Integer contractNumber, List<String> unnassignedMovementFolioList) {
        String sqlQuery;
        Double soldBalance = 0D;

        try {
            sqlQuery = "SELECT SUM(importesucio - importeisr) FROM MovimientosDia ";
            sqlQuery += "WHERE Asignado = 0 And Cancelado = 0 And TIPOMOVIMIENTO  IN ('VDI') ";
            sqlQuery += "AND Contrato = '" + contractNumber + "' ";

            if (!unnassignedMovementFolioList.isEmpty()) {
                String identifiers = String.join(", ", unnassignedMovementFolioList);
                sqlQuery += "AND FOLIO NOT IN ('" + identifiers + "') ";
            }

            soldBalance = jdbcTemplate.queryForObject(sqlQuery, Double.class);
        } catch (Exception e) {
            log.error("SisBurService", e);
        }

        return soldBalance;
    }

    public Double getAssignedMovementBalance(Integer contractNumber, List<String> unnassignedMovementFolioList) {
        String sqlQuery;
        Double assignedMovementBalance = 0D;

        try {
            sqlQuery = "SELECT Sum(importesucio) FROM MovimientosDia ";
            sqlQuery += "WHERE Asignado = '0' And Cancelado = '0' And TIPOMOVIMIENTO IN ('CDI','CRE') ";
            sqlQuery += "AND Contrato = '" + contractNumber + "' ";

            if (!unnassignedMovementFolioList.isEmpty()) {
                String identifiers = String.join(", ", unnassignedMovementFolioList);
                sqlQuery += "AND FOLIO NOT IN ('" + identifiers + "') ";
            }

            assignedMovementBalance = jdbcTemplate.queryForObject(sqlQuery, Double.class);

        } catch (Exception e) {
            log.error("SisBurService", e);
        }

        return assignedMovementBalance;
    }

    public List<String> getPendingMovementFolioList(Integer contractNumber) {
        String sqlQuery;
        List<String> folioList = new ArrayList<>();

        try {
            sqlQuery = "SELECT DISTINCT folio FROM MovimientosDia WHERE TIPOMOVIMIENTO IN ('CDI','CRE','VDI') ";
            sqlQuery += "And Asignado = '1' AND Cancelado = '0' AND Contrato = '" + contractNumber + "'";

            folioList = jdbcTemplate.queryForList(sqlQuery, String.class);

        } catch (Exception e) {
            log.error("SisBurService", e);
        }

        return folioList;
    }

    public Double getCurrentBalanceFromPendingMovements(String contractNumber) {
        String sqlQuery;
        List<Map<String, Object>> resultList;
        Double balance = 0D;

        try {
            sqlQuery = "SELECT SUM(ImporteSucio - ImporteIsr - ImporteIva) FROM MovimientosPendientes ";
            sqlQuery += "WHERE Contrato = '" + contractNumber + "' ";

            balance = jdbcTemplate.queryForObject(sqlQuery, Double.class);

        } catch (Exception e) {
            log.error("SisBurService", e);
        }

        return balance;
    }

    public CustomerCashBalance getContractBalanceByCurrency(String contractNumber, String currencyType) {
        String sqlQuery;
        List<Map<String, Object>> resultList;
        CustomerCashBalance customerCashBalance = null;

        try {
            sqlQuery = "SELECT * FROM SaldosEfectivo ";
            sqlQuery += "WHERE CveDivisa = '" + currencyType + "' ";
            sqlQuery += "AND Contrato '" + contractNumber + "' ";

            resultList = jdbcTemplate.queryForList(sqlQuery);

            if (!resultList.isEmpty()) {
                Map<String, Object> result = resultList.getFirst();
                customerCashBalance = new CustomerCashBalance();

                customerCashBalance.setCustomerKey(Integer.parseInt(result.get("CVECLIENTE").toString()));
                customerCashBalance.setContractNumber(Integer.parseInt(result.get("CONTRATO").toString()));
                customerCashBalance.setCurrency(result.get("CVEDIVISA").toString());
                customerCashBalance.setBalanceToday(Double.parseDouble(result.get("SALDOH").toString()));
                customerCashBalance.setBalance24(Double.parseDouble(result.get("SALDO2").toString()));
                customerCashBalance.setBalance48(Double.parseDouble(result.get("SALDO4").toString()));
                customerCashBalance.setBalance72(Double.parseDouble(result.get("SALDO7").toString()));
                customerCashBalance.setBalance96(Double.parseDouble(result.get("SALDO9").toString()));
                customerCashBalance.setBalance120(Double.parseDouble(result.get("SALDO1").toString()));
                customerCashBalance.setHoldBalance(Double.parseDouble(result.get("HOLD").toString()));
            }

        } catch (Exception e) {
            log.error("SisBurService", e);
        }

        return customerCashBalance;
    }

    public List<PositionInterface> getMarketPosition(String contractNumber, Integer marketType) {
        String sqlQuery;
        List<Map<String, Object>> resultList;
        List<PositionInterface> posicionList = new ArrayList<>();

        if (marketType == null || marketType < 0 || marketType > marketTypes.length) {
            log.error("SisBurService", new Exception("Tipo de Valor de Mercado no permitido"));
        }

        try {
            sqlQuery = "SELECT * FROM " + dailyPositionTable + " WHERE ";
            sqlQuery += "Titulos <> '0' AND Contrato = '" + contractNumber + "' AND Mercado = '" + marketType + "' ";  
            sqlQuery += "ORDER BY Tenencia, Folioid, Folio2";

            resultList = jdbcTemplate.queryForList(sqlQuery);
            Double plusMinusValia = 0D;
            Double titulos = 0D;
            Double costoXTitulos = 0D;
            Double costoPromedio = 0D;
            
            for (Map<String,Object> map : resultList) {
                GeneralDTO posicion= new GeneralDTO();

                posicion.setEmmiter(map.get("EMISORA").toString());
                posicion.setSerie(map.get("SERIE").toString());
                posicion.setPeriod(map.get("PLAZO").toString());
                posicion.setRate(map.get("TASAINTE").toString());
                posicion.setSecurities(map.get("TITULOS").toString());
                //posicion.setCostoXTitulos(map.get("PRECIOSUCIO").toString());
                posicion.setAverageAmount(Double.parseDouble(map.get("COSTOSUCIO").toString()));
                titulos = Double.parseDouble(posicion.getSecurities());
                costoXTitulos = titulos * Double.parseDouble(map.get("PRECIOSUCIO24").toString());
                costoPromedio = titulos * Double.parseDouble(map.get("COSTOSUCIO").toString());
                plusMinusValia = costoXTitulos - costoPromedio;
                //posicion.setValorMercado(costoXTitulos + "");
                posicion.setPrice(costoPromedio);
                posicion.setCapitalGainLoss(plusMinusValia);
                posicion.setMarket(marketTypes[marketType]);

                posicionList.add(posicion);
                plusMinusValia = 0D;
            }


        } catch (Exception e) {
            log.error("SisBurService[getMoneyMarketPosition]", e);
        }

        return posicionList;
    }

    public void updateCustomerContractPassword(String customerNumber, String contractNumber, String newPassword) {
        String sqlQuery;

        try {
            sqlQuery = "UPDATE CONTRATOS SET PASSWORD = '"+ newPassword + "' WHERE ";
            sqlQuery += "CVECLIENTE = '" + customerNumber + "' AND CONTRATO = '" + contractNumber + "'";

            jdbcTemplate.execute(sqlQuery);

        } catch (Exception e) {
            log.error("SisBurService[updateCustomerContractPassword]", e);
        }
    }

    public List<Map<String, Object>> getPeriodList(String contractNumber) {
        String sqlQuery;
        List<Map<String, Object>> periodList = new ArrayList<>();

        try {
            sqlQuery = "SELECT c.PERIODO, c.TIPO, c.COTITULAR FROM CONSTANCIAS c WHERE c.CONTRATO = '" + contractNumber + "' ORDER BY c.PERIODO";

            periodList = jdbcTemplate.queryForList(sqlQuery);

        } catch (Exception e) {
            log.error("SisBurService", e);
        }

        return periodList;
    }

    public byte[] getTaxCertificateFile(String contractNumber, String year, Integer type, Integer fileType) {
        String sqlQuery;
        String field = fileType == 1 ? "PDF" : "XML";

        sqlQuery = "SELECT c." + field + " FROM CONSTANCIAS c WHERE c.CONTRATO = '" + contractNumber + "' ";
        sqlQuery += "AND c.PERIODO = '" + year + "' AND c.TIPO = '" + type + "'";

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
