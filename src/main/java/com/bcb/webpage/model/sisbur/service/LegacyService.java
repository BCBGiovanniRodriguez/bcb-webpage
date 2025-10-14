package com.bcb.webpage.model.sisbur.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LegacyService {

    @Autowired
    @Qualifier("sisburJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private DateTimeFormatter mexFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<Map<String, Object>> getCustomerContracts(List<Long> excludedList) {
        List<Map<String,Object>> result = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        String excluded;
        String sqlSelect = "SELECT CVECLIENTE, CONTRATO, EMAIL, TELDOM, NOMCLIENTE, APEPATCLIENTE, APEMATCLIENTE, INICIAL, BLOQUEADO, PASSWORD FROM CONTRATOS ";
        sqlSelect += "WHERE NOT TIPOPERSONA = 'P' AND BAJA = 0 AND INICIAL = 0 ";

        try {
            if (excludedList.size() > 0) {
                stringBuilder.append("(");
                for (Long customerKey : excludedList) {
                    stringBuilder.append("'").append(customerKey).append("', ");
                }

                excluded = stringBuilder.toString();
                excluded = excluded.substring(0, excluded.length() - 2);
                excluded += ") ";

                sqlSelect += " AND CONTRATO NOT IN " + excluded;
            }

            sqlSelect += "ORDER BY CVECLIENTE";

            result = jdbcTemplate.queryForList(sqlSelect);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }

        return result;
    }

    public List<Map<String,Object>> getCustomerMovements(CustomerCustomer customer, LocalDate starDate, LocalDate endDate) {
        StringBuilder stringBuilder;
        String sql = "";
        List<Map<String,Object>> dataList = new ArrayList<>();

        try {
            if (customer == null) {
                throw new Exception("Cliente no configurado");
            } else {
                List<CustomerContract> customerContractList = customer.getContracts();
                CustomerContract customerCurrentContract = customerContractList.stream()
                    .filter(cc -> cc.isCurrent())
                    .findFirst()
                    .orElse(null);

                if (customerCurrentContract != null) {
                    stringBuilder = new StringBuilder("SELECT * FROM ( ")
                        .append("SELECT * FROM MovimientosDia ")
                        .append("WHERE Contrato = '").append(customerCurrentContract.getContractNumber()).append("' ")
                        .append("AND Cancelado = 0 AND ((TipoMovimiento IN ('CRE','CPA','CDI','VTA','VDI') AND Asignado = 1 ) ")
                        .append("OR  (TipoMovimiento NOT IN ('CRE','CPA','CDI','VTA','VDI','CAC','CAV'))) ")
                        .append("AND Cancelado = 0 AND FechaOperacion >= TO_DATE('").append(starDate.format(mexFormatter)) .append("', 'DD-MM-YYYY') ")
                        .append("AND FechaOperacion <= TO_DATE('").append(endDate.format(mexFormatter)).append("', 'DD-MM-YYYY') ")
                        .append("Union All ")
                        .append("SELECT * FROM MovimientosAnual ")
                        .append("WHERE Contrato = '").append(customerCurrentContract.getContractNumber()).append("' ")
                        .append("AND ((TipoMovimiento IN ('CRE','CPA','CDI','VTA','VDI') AND Asignado = 1 ) ")
                        .append("OR  (TipoMovimiento NOT IN ('CRE','CPA','CDI','VTA','VDI','CAC','CAV'))) ")
                        .append("AND Cancelado = 0 AND FechaOperacion >= TO_DATE('").append(starDate.format(mexFormatter)).append("', 'DD-MM-YYYY') ")
                        .append("AND FechaOperacion <= TO_DATE('").append(endDate.format(mexFormatter)).append("', 'DD-MM-YYYY') ")
                        .append(") ORDER BY FechaOperacion, HoraRegistro ");

                    sql = stringBuilder.toString();
                    dataList = jdbcTemplate.queryForList(sql);
                }
            }
        } catch (Exception e) {
            System.out.println("Error on CustomerService::getCustomerMovements " + e.getLocalizedMessage());
        }

        return dataList;
    }

    public List<Map<String,Object>> getCurrentCustomerBalance(CustomerContract customerContract) {
        String sql;
        List<Map<String,Object>> dataList = new ArrayList<>();

        try {
            sql = "SELECT * FROM SaldosEfectivo WHERE CveDivisa = 'MXN' ";
            sql += "And Contrato = '" + customerContract.getContractNumber() + "' ORDER BY CveDivisa";

            dataList = jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("Error", e);
        }

        return dataList;
    }

    public void getCurrentCustomerMoneyMarketPosition(CustomerContract customerContract) {
        List<Map<String, Object>> positionList;
        StringBuilder stringBuilder;

        try {
            stringBuilder = new StringBuilder("SELECT * FROM PosicionDiaV  ")
                .append("WHERE Titulos <> 0 ")
                .append("And Contrato = '").append(customerContract.getContractNumber()).append("' ")
                .append("ORDER BY Mercado,Tenencia,Folioid,Folio2 ");
            
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage());
        }
    }

    public void getCustomerStockMarkertPosition() {

    }



    public List<Map<String,Object>> getMovementTypeList() {
        String sql = null;
        List<Map<String,Object>> movementTypeList = new ArrayList<>();

        try {
            sql = "SELECT * FROM TIPOSMOVIMIENTO ";
            movementTypeList = jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage());
        }

        return movementTypeList;
    }

    public Double getDoubleValue(String value) {
        Double result = 0D;

        try {
            result = Double.parseDouble(value.replace(",", "").trim());
        } catch (Exception e) {
            System.out.println("Error on:" + e.getLocalizedMessage());
        }

        return result;
    }
}
