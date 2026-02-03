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

    private Integer offset = 500;

    public static final Integer CONTRACT_INITIAL_YES = 1;

    public static final Integer CONTRACT_INITIAL_NO = 0;

    private DateTimeFormatter mexFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<Map<String, Object>> getCustomerContracts(Integer inicial) {
        List<Map<String,Object>> result = new ArrayList<>();
        
        String sqlSelect = "SELECT CVECLIENTE, CONTRATO, EMAIL, TELCEL, TELDOM, TELOFI, NOMCLIENTE, APEPATCLIENTE, APEMATCLIENTE, RFC, CURP, INICIAL, ";
        sqlSelect += "BLOQUEADO, PASSWORD, EJECUCION, SRVASE, SOFISTICADO, PERFIL, ";
        sqlSelect += "CALLEDOM, NUMEXTERIORDOM, NUMINTERIORDOM, COLONIADOM, CPOSTALDOM, DELMUNDOM, CIUDADDOM, ESTADODOM, PAISDOM ";
        sqlSelect += "FROM CONTRATOS WHERE NOT TIPOPERSONA = 'P' AND BAJA = 0 AND INICIAL = " + inicial + " ORDER BY CONTRATO";

        try {
            result = jdbcTemplate.queryForList(sqlSelect);
            
        } catch (Exception e) {
            System.out.println("LegacyService: " + e.getLocalizedMessage());
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
            System.out.println("Error on LegacyService::getCustomerMovements " + e.getLocalizedMessage());
        }

        return dataList;
    }

    public List<Map<String, Object>> getProfileLists() {
        List<Map<String, Object>> profileList = new ArrayList<>();
        String sqlSelect;

        try {
            sqlSelect = "SELECT * FROM CATPERFILES";

            profileList = jdbcTemplate.queryForList(sqlSelect);
        } catch (Exception e) {
            log.error("LegacyService::getProfileLists", e);
        }

        return profileList;
    }

    public List<Map<String, Object>> getTownshipLists(List<Map<String, Object>> customerContracts) {
        
        StringBuilder stringBuilder = new StringBuilder();
        List<Map<String, Object>> townshipList = new ArrayList<>();
        String sqlSelect;
        String identifiersSelect;
        String included;

        try {
            sqlSelect = "SELECT * FROM CATMUNICIPIOS cm WHERE cm.IDMUNICIPIO IN (";
            identifiersSelect = "SELECT DISTINCT c.DELMUNDOM FROM CONTRATOS c WHERE c.CONTRATO IN ";

            if (customerContracts.size() > 0) {
                stringBuilder.append("(");

                for (Map<String, Object> element : customerContracts) {
                    stringBuilder.append("'").append(element.get("CONTRATO").toString()).append("', ");
                }

                included = stringBuilder.toString();
                included = included.substring(0, included.length() - 2);
                included += ") ";

                identifiersSelect += included;
            }

            sqlSelect += identifiersSelect + ")";

            townshipList = jdbcTemplate.queryForList(sqlSelect);

        } catch (Exception e) {
            log.error("LegacyService::getTownshipLists", e);
        }

        return townshipList;
    }

    public List<Map<String, Object>> getCityLists(List<Map<String, Object>> customerContracts) {
        
        StringBuilder stringBuilder = new StringBuilder();
        List<Map<String, Object>> cityList = new ArrayList<>();
        String sqlSelect;
        String identifiersSelect;
        String included;

        try {
            sqlSelect = "SELECT * FROM CATCIUDADES cc WHERE cc.IDCIUDAD IN (";
            identifiersSelect = "SELECT DISTINCT c.CIUDADDOM FROM CONTRATOS c WHERE c.CONTRATO IN ";

            if (customerContracts.size() > 0) {
                stringBuilder.append("(");

                for (Map<String, Object> element : customerContracts) {
                    stringBuilder.append("'").append(element.get("CONTRATO").toString()).append("', ");
                }

                included = stringBuilder.toString();
                included = included.substring(0, included.length() - 2);
                included += ") ";

                identifiersSelect += included;
            }
            sqlSelect += identifiersSelect + ")";
            cityList = jdbcTemplate.queryForList(sqlSelect);

        } catch (Exception e) {
            log.error("LegacyService::getCityLists", e);
        }

        return cityList;
    }

    public List<Map<String, Object>> getStateLists(List<Map<String, Object>> customerContracts) {
        
        StringBuilder stringBuilder = new StringBuilder();
        List<Map<String, Object>> stateList = new ArrayList<>();
        String sqlSelect;
        String identifierSelect;
        String included;

        try {
            sqlSelect = "SELECT * FROM CATESTADOS cc WHERE cc.IDESTADO IN (";
            identifierSelect = "SELECT DISTINCT c.ESTADODOM FROM CONTRATOS c WHERE c.CONTRATO IN ";

            if (customerContracts.size() > 0) {
                stringBuilder.append("(");

                for (Map<String, Object> element : customerContracts) {
                    stringBuilder.append("'").append(element.get("CONTRATO").toString()).append("', ");
                }

                included = stringBuilder.toString();
                included = included.substring(0, included.length() - 2);
                included += ") ";

                identifierSelect += included;
            }

            sqlSelect += identifierSelect + ")";
            stateList = jdbcTemplate.queryForList(sqlSelect);

        } catch (Exception e) {
            log.error("LegacyService::getStateLists", e);
        }

        return stateList;
    }

    public List<Map<String, Object>> getCountryLists(List<Map<String, Object>> customerContracts) {
        
        StringBuilder stringBuilder = new StringBuilder();
        List<Map<String, Object>> countryList = new ArrayList<>();
        String sqlSelect;
        String identifierSelect;
        String included;

        try {
            sqlSelect = "SELECT * FROM CATPAIS cp WHERE cp.IDPAIS IN (";
            identifierSelect = "SELECT DISTINCT c.PAISDOM FROM CONTRATOS c WHERE c.CONTRATO IN ";

            if (customerContracts.size() > 0) {
                stringBuilder.append("(");

                for (Map<String, Object> element : customerContracts) {
                    stringBuilder.append("'").append(element.get("CONTRATO").toString()).append("', ");
                }

                included = stringBuilder.toString();
                included = included.substring(0, included.length() - 2);
                included += ") ";

                identifierSelect += included;
            }

            sqlSelect += identifierSelect + ")";
            countryList = jdbcTemplate.queryForList(sqlSelect);

        } catch (Exception e) {
            log.error("LegacyService::getCountryLists", e);
        }

        return countryList;
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

    public List<String> getCustomerContractStockMarketPositionList() {
        List<String> contractList = new ArrayList<>();
        String sqlSelect;

        try {
            sqlSelect = "SELECT DISTINCT p.CONTRATO FROM POSICIONDIAV p WHERE p.MERCADO = '1' AND p.TITULOS > '0' ";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlSelect);
            for (Map<String, Object> item : resultList) {
                contractList.add(item.get("CONTRATO").toString());
            }
        } catch (Exception e) {
            System.out.println("LegacyService::getCustomerContractStockMarketPositionList[" + e.getLocalizedMessage() + "]");
        }

        return contractList;
    }

    public List<Map<String, Object>> getCurrentCustomerStockMarketPosition(List<String> customerContractNumberList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String sqlSelect;

        try {
            sqlSelect = "SELECT p.CVECLIENTE, p.CONTRATO, p.TIPOVALOR, p.EMISORA, p.SERIE, p.TITULOS, ";
            sqlSelect += "p.COSTOLIMPIO, p.COSTOSUCIO, p.COSTOLIMPIOINI, p.COSTOSUCIOINI, p.PRECIOLIMPIOINI, p.PRECIOSUCIOINI, ";
            sqlSelect += "p.PRECIOLIMPIO, p.PRECIOSUCIO, p.PRECIOLIMPIO24, p.PRECIOSUCIO24 ";
            sqlSelect += "FROM POSICIONDIAV p WHERE p.MERCADO = '1' AND p.TITULOS > '0' ";

            String condition;
            StringBuilder sb = new StringBuilder(" AND p.CONTRATO IN (");
            if (!customerContractNumberList.isEmpty()) {
                for (String contractNumber : customerContractNumberList) {
                    sb.append(contractNumber).append(", ");
                }
                
                condition = sb.toString();
                condition = condition.substring(0, condition.length() - 2);
                condition += ")";

                sqlSelect += condition;
            }

            resultList = jdbcTemplate.queryForList(sqlSelect);
        } catch (Exception e) {
            System.out.println("LegacyService::getCurrentCustomerStockMarketPosition[" + e.getLocalizedMessage() + "]");
        }

        return resultList;
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

    public boolean isStockMarketActive() {
        boolean startOfDay = false;
        boolean endOfDay = true;

        String sqlSelect;
        LocalDate today = LocalDate.now();

        try {
            sqlSelect = "SELECT * FROM logOperacion WHERE FECHA = TO_DATE('" + today.format(isoFormatter) + "', 'YYYY-MM-DD') AND FUNCION = 'Checar Servicios Windows'";
            startOfDay = jdbcTemplate.queryForList(sqlSelect).size() == 1;

            sqlSelect = "SELECT * FROM logOperacion WHERE FECHA = TO_DATE('" + today.format(isoFormatter) + "', 'YYYY-MM-DD') AND FUNCION = 'Cambio de día Lúmina'";
            endOfDay = jdbcTemplate.queryForList(sqlSelect).size() == 1;
        } catch (Exception e) {
            System.out.println("LegacyService::isStockMarketActive[" + e.getLocalizedMessage() + "]");
        }

        // Si ya inicio el día y no se han cerrado operaciones
        return startOfDay && !endOfDay;
    }
}
