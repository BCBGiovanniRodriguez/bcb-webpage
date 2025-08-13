package com.bcb.webpage.model.sisbur.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LegacyService {

    @Autowired
    @Qualifier("sisburJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

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

                //System.out.println(excluded);

                sqlSelect += " AND CONTRATO NOT IN " + excluded;
            }

            sqlSelect += "ORDER BY CVECLIENTE";

            result = jdbcTemplate.queryForList(sqlSelect);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }

        return result;
    }
}
