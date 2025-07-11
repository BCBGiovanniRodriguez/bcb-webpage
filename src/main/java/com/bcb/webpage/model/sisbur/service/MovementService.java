package com.bcb.webpage.model.sisbur.service;

import java.sql.Types;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.bcb.webpage.model.backend.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.sisbur.mapper.CustomerMapper;

@Service
public class MovementService {

    @Autowired
    //@Qualifier("sisburNamedParameterJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 
     * @param contractNumber
     */
    public CustomerCustomer getCustomer(Integer contractNumber) {
        CustomerCustomer customer = null;
        
        Map<String, Object> parameters = new HashMap<>();
        String sql;

        try {
            parameters.put("contractNumber", contractNumber);
            sql = "select * from contratos where contrato = :contractNumber";

            customer = namedParameterJdbcTemplate.queryForObject(sql, parameters, new CustomerMapper());
        } catch (Exception e) {
            System.out.println("Error on MovementService::getCustomer[" + e.getMessage() + "]");
        }

        return customer;
    }

    /**
     * 
     * @param contractNumber
     */
    public void getContractMovements(Integer contractNumber, LocalDate startDate, LocalDate endDate) {
        String sql;
        LocalDate fecha;
        String sqlSelectMovimientosDia = "SELECT * FROM MovimientosDia WHERE Contrato = :contrato AND Asignado = '1' AND (";
        sqlSelectMovimientosDia += "(TipoMovimiento IN (:tmCRE, :tmCPA, :tmCDI, :tmVTA, :tmVDI) AND Asignado = 1 ) OR ";
        sqlSelectMovimientosDia += "(TipoMovimiento NOT IN (tmCRE, :tmCPA, :tmCDI, :tmVTA, :tmVDI, :tmCAC, :tmCAV) ) ) ";
        sqlSelectMovimientosDia += "AND Cancelado = '0' AND FechaOperacion >= TO_DATE('20250225', 'YYYYMMDD')";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("contrato", contractNumber, Types.NUMERIC);
        parameters.addValue("tmCRE", "CRE", Types.VARCHAR);
        parameters.addValue("tmCPA", "CPA", Types.VARCHAR);
        parameters.addValue("tmCDI", "CDI", Types.VARCHAR);
        parameters.addValue("tmVTA", "VTA", Types.VARCHAR);
        parameters.addValue("tmVDI", "VDI", Types.VARCHAR);
        parameters.addValue("tmCAC", "CAC", Types.VARCHAR);
        parameters.addValue("tmCAV", "CAV", Types.VARCHAR);
        parameters.addValue("startDate", startDate, Types.DATE);
        parameters.addValue("endDate", endDate, Types.DATE);

        

        try {
            sql = "";


        } catch (Exception e) {
            System.out.println("Error on MovementService::getMovements " + e.getLocalizedMessage());
        }
    }

}
