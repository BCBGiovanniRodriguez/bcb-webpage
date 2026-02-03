package com.bcb.webpage.service.sisbur;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bcb.webpage.model.webpage.dto.CashDTO;
import com.bcb.webpage.model.webpage.dto.GeneralDTO;
import com.bcb.webpage.model.webpage.dto.customer.CustomerAttorney;
import com.bcb.webpage.model.webpage.dto.customer.CustomerBankAccount;
import com.bcb.webpage.model.webpage.dto.customer.CustomerBeneficiary;
import com.bcb.webpage.model.webpage.dto.interfaces.PositionInterface;
import com.bcb.webpage.model.webpage.entity.SisburEmmissionPrice;
import com.bcb.webpage.model.webpage.entity.SisburEmmission;
import com.bcb.webpage.model.webpage.repository.SisburEmmissionPriceRepository;
import com.bcb.webpage.model.webpage.repository.SisburEmmissionRepository;
import com.bcb.webpage.service.sisbur.model.CustomerCashBalance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SisBurService {

    @Autowired
    @Qualifier("sisburJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SisburEmmissionRepository sisburEmmissionRepository;

    @Autowired
    private SisburEmmissionPriceRepository sisburEmmissionPriceRepository;
    
    private DateTimeFormatter isoShortFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String CURRENCY_TYPE_MXN = "MXN";

    public static final String CURRENCY_TYPE_USD = "USD";

    public static final Integer MARKET_TYPE_STOCK_MARKET = 1;

    public static final Integer MARKET_TYPE_MONEY_MARKET = 2;

    public static final Integer MARKET_TYPE_INVESTMENT_FUNDS = 3;

    private static final String[] marketTypes = {"", "Mercado de Capitales", "Mercado de Dinero", "Fondos de Inversión"};

    public List<CashDTO> getCashBalance(String contractNumber) {
        List<CashDTO> cashList = new ArrayList<>();
        CustomerCashBalance customerCashBalance = null;
        Double unassignedMovementMoneyMarketBalance = 0D;
        Double unassignedMovementMoneyMarketSoldBalance = 0D;
        Double pendingBalanceFromMovements = 0D;
        List<String> pendingMovementFolioList = new ArrayList<>();

        try {
            customerCashBalance = this.getContractBalanceByCurrency(contractNumber, CURRENCY_TYPE_MXN);
            pendingMovementFolioList = this.getPendingMovementFolioList(contractNumber);

            unassignedMovementMoneyMarketBalance = this.getUnassignedMovementMoneyMarketBalance(contractNumber, pendingMovementFolioList);
            unassignedMovementMoneyMarketBalance = (unassignedMovementMoneyMarketBalance != null) ? unassignedMovementMoneyMarketBalance : 0D;

            unassignedMovementMoneyMarketSoldBalance = this.getUnassignedMovementMoneyMarketSoldBalance(contractNumber, pendingMovementFolioList);
            unassignedMovementMoneyMarketSoldBalance = (unassignedMovementMoneyMarketSoldBalance != null) ? unassignedMovementMoneyMarketSoldBalance : 0D;

            pendingBalanceFromMovements = this.getPendingBalanceFromMovements(contractNumber);
            pendingBalanceFromMovements = (pendingBalanceFromMovements != null) ? pendingBalanceFromMovements : 0D;

            CashDTO mxnCash = new CashDTO();
            Double mxnPendingBalance = 0D;
            CashDTO moneyMarketCash = new CashDTO();
            CashDTO indevalCash = new CashDTO();

            mxnCash.setCurrencyName(SisBurService.CURRENCY_TYPE_MXN);
            mxnCash.setCurrentBalance(customerCashBalance.getBalanceToday());
            mxnPendingBalance = customerCashBalance.getBalance120() + customerCashBalance.getBalance96() + customerCashBalance.getBalance72() + customerCashBalance.getBalance48() + customerCashBalance.getBalance24();
            mxnCash.setPendingBalance(mxnPendingBalance);
            mxnCash.setSubTotal(mxnPendingBalance + pendingBalanceFromMovements + unassignedMovementMoneyMarketBalance - unassignedMovementMoneyMarketSoldBalance);

            moneyMarketCash.setCurrencyName("Por Asignar MD");
            moneyMarketCash.setCurrentBalance(unassignedMovementMoneyMarketBalance - unassignedMovementMoneyMarketSoldBalance);
            moneyMarketCash.setPendingBalance(0D);
            moneyMarketCash.setSubTotal(0D);

            indevalCash.setCurrencyName("Por Asignar Indeval");
            indevalCash.setCurrentBalance(pendingBalanceFromMovements);
            indevalCash.setPendingBalance(0D);
            indevalCash.setSubTotal(0D);

            cashList.add(mxnCash);
            cashList.add(moneyMarketCash);
            cashList.add(indevalCash);

        } catch (Exception e) {
            log.error("SisBurService::getCashBalance", e);
        }

        return cashList;
    }

    // Saldo Indeval
    private Double getPendingBalanceFromMovements(String contractNumber) {
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

    private Double getUnassignedMovementMoneyMarketSoldBalance(String contractNumber, List<String> unnassignedMovementFolioList) {
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

    private Double getUnassignedMovementMoneyMarketBalance(String contractNumber, List<String> unnassignedMovementFolioList) {
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

    private List<String> getPendingMovementFolioList(String contractNumber) {
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
            sqlQuery += "AND Contrato = '" + contractNumber + "' ";

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
                customerCashBalance.setHoldBalance(Double.parseDouble(result.get("SALDOHOLD").toString()));
            }

        } catch (Exception e) {
            log.error("SisBurService::getContractBalanceByCurrency", e);
        }

        return customerCashBalance;
    }

    public List<PositionInterface> getMarketPosition(String contractNumber, Integer marketType) {
        String sqlQuery;
        List<Map<String, Object>> resultList;
        List<PositionInterface> posicionList = new ArrayList<>();

        if (marketType == null || marketType < 0 || marketType > marketTypes.length) {
            log.error("SisBurService", new Exception("Tipo de Valor de Mercado no permitido"));
        } else {
            try {
                sqlQuery = "SELECT * FROM PosicionDiaV WHERE Titulos <> '0' AND ";
                sqlQuery += "Contrato = '" + contractNumber + "' AND Mercado = '" + marketType + "' ";
                sqlQuery += "ORDER BY EMISORA, SERIE, Tenencia, Folioid, Folio2";
                resultList = jdbcTemplate.queryForList(sqlQuery);

                Double capitalGainLoss = 0D;
                Double securities = 0D;
                Double dirtyPrice = 0D;
                Double dirtyPrice24 = 0D;
                Double dirtyCost = 0D;
                Double marketValue = 0D;
                Double averageCost = 0D;
                LocalDate averageDate;

                String valueType;
                String emmiter;
                String serie;
                Optional<SisburEmmission> sisburEmmisionOptional = null;
                Optional<SisburEmmissionPrice> sisburEmmisionPriceOptional = null;
                SisburEmmissionPrice sisburEmmisionPriceLast = null;

                // Pruebas!
                String cssStyle = null;
                String str = "2025-11-19 15:29:53";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                
                //LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
                LocalDateTime dateTime = LocalDateTime.now();
                LocalDateTime delayedTime = dateTime.minusMinutes(20L);

                SisburEmmission sisburEmmission = null;
                SisburEmmissionPrice sisburEmmissionPrice = null;
                
                for (Map<String,Object> map : resultList) {
                    GeneralDTO marketPosition= new GeneralDTO();

                    valueType = map.get("TIPOVALOR").toString();
                    emmiter = map.get("EMISORA").toString();
                    serie = map.get("SERIE").toString();
                    securities = Double.parseDouble(map.get("TITULOS").toString());
                    dirtyCost = Double.parseDouble(map.get("COSTOSUCIO").toString());
                    
                    marketPosition.setEmmiter(emmiter);
                    marketPosition.setSerie(serie);
                    marketPosition.setPeriod(map.get("PLAZO").toString());
                    marketPosition.setRate(map.get("TASAINTE").toString()); // Este parámetro es el que hay que establecer bien
                    marketPosition.setHolding(map.get("TENENCIA").toString());
                    marketPosition.setAmount(Double.parseDouble(map.get("MONTO").toString()));
                    marketPosition.setAward(Double.parseDouble(map.get("PREMIO").toString()));
                    marketPosition.setAverageDate("");

                    dirtyPrice = Double.parseDouble(map.get("PRECIOSUCIO").toString());
                    dirtyPrice24 = Double.parseDouble(map.get("PRECIOSUCIO24").toString());
                    
                    if (SisBurService.MARKET_TYPE_STOCK_MARKET == marketType) {
                        // Find emmision
                        sisburEmmisionOptional = sisburEmmissionRepository.findOneByValueTypeAndEmmiterAndSerie(valueType, emmiter, serie);
                        if (sisburEmmisionOptional.isPresent()) {
                            sisburEmmission = sisburEmmisionOptional.get();
                            sisburEmmisionPriceOptional = sisburEmmissionPriceRepository.findFirstByEmmissionIdAndCreatedLessThanEqualOrderByCreatedDesc(sisburEmmission, delayedTime);

                            System.out.println("Emmision: " + sisburEmmission.getEmmissionId());
                            System.out.println("DelayedTime" + delayedTime.format(formatter));
    
                            if (sisburEmmisionPriceOptional.isPresent()) {
                                sisburEmmisionPriceLast = sisburEmmisionPriceOptional.get();
                                System.out.println("Ultimo precio encontrado emmissionPriceId: " + sisburEmmisionPriceLast.getEmmisionPriceId());
                                dirtyPrice = sisburEmmisionPriceLast.getDirtyPrice();
                                dirtyPrice24 = sisburEmmisionPriceLast.getDirtyPrice24();
                                
                                System.out.println("CostoSucio TR:" + Double.parseDouble(map.get("COSTOSUCIO").toString()));
                                System.out.println("CostoSucio Delayed:" + sisburEmmisionPriceLast.getDirtyPrice());
                            } else {
                                System.out.println("No se encontró precio para emmision para: " + valueType + " " + emmiter + " " + serie);
                            }
                        } else {
                            System.out.println("No se encontró emmision para: " + valueType + " " + emmiter + " " + serie);
                        }
                    }
                    
                    marketValue = securities * dirtyPrice24;
                    /*if (SisBurService.MARKET_TYPE_STOCK_MARKET == marketType) {
                        marketValue = securities * dirtyPrice24;
                    }*/ 
                    if(SisBurService.MARKET_TYPE_MONEY_MARKET == marketType && marketPosition.getHolding().equals("REP") && !marketPosition.getEmmiter().equals("REPORTO")) {
                        marketValue = marketPosition.getAward() + marketPosition.getAmount();
                        marketPosition.setAverageDate(map.get("FECHAPROMEDIO").toString().substring(0, 10));
                    }

                    averageCost = securities * dirtyCost;
                    capitalGainLoss = marketValue - averageCost;
                    
                    marketPosition.setSecurities(securities);
                    marketPosition.setAverageAmount(dirtyCost);
                    marketPosition.setMarketPrice(dirtyPrice24); // Precio de Mercado Actual
                    marketPosition.setMarketValue(marketValue); // Valor de Mercado Actual
                    marketPosition.setCapitalGainLoss(capitalGainLoss); // PlusMinusvalia
                    marketPosition.setMarket(marketTypes[marketType]);
                    marketPosition.setDirtyPrice(dirtyPrice);
                    marketPosition.setDirtyPrice24(dirtyPrice24);
                    marketPosition.setDirtyCost(dirtyCost);

                    if (capitalGainLoss > 0D) {
                        cssStyle = "text-success";
                    } else if(capitalGainLoss < 0D) {
                        cssStyle = "text-danger";
                    }

                    marketPosition.setCssStyle(cssStyle);

                    if (SisBurService.MARKET_TYPE_STOCK_MARKET == marketType) {
                        System.out.println(marketPosition.toString());
                    }
    
                    posicionList.add(marketPosition);
                    capitalGainLoss = 0D;
                    marketValue = 0D;
                    averageCost = 0D;
                    averageDate = null;
                }
            } catch (Exception e) {
                log.error("SisBurService[getMoneyMarketPosition]", e);
            }
        }

        return posicionList;
    }

    public void updateCustomerContractPassword(String customerNumber, String contractNumber, String newPassword, Boolean isInitial) {
        String sqlQuery;

        try {
            sqlQuery = "UPDATE CONTRATOS SET PASSWORD = '"+ newPassword + "'  ";

            if (isInitial) {
                sqlQuery += ", INICIAL = '0' ";
            }
            
            sqlQuery += "WHERE CVECLIENTE = '" + customerNumber + "' AND CONTRATO = '" + contractNumber + "'";

            jdbcTemplate.execute(sqlQuery);

        } catch (Exception e) {
            log.error("SisBurService[updateCustomerContractPassword]", e);
        }
    }

    public List<Map<String, Object>> getPeriodList(String contractNumber) {
        String sqlQuery;
        List<Map<String, Object>> periodList = new ArrayList<>();

        try {
            sqlQuery = "SELECT c.PERIODO, c.TIPO, c.COTITULAR FROM CONSTANCIAS c WHERE c.CONTRATO = '" + contractNumber + "' ORDER BY c.PERIODO, c.COTITULAR, c.TIPO";

            periodList = jdbcTemplate.queryForList(sqlQuery);

        } catch (Exception e) {
            log.error("SisBurService", e);
        }

        return periodList;
    }

    public byte[] getTaxCertificateFile(String contractNumber, String year, Integer type, Integer owner, Integer fileType) {
        String sqlQuery;
        String field = fileType == 1 ? "PDF" : "XML";

        sqlQuery = "SELECT c." + field + " FROM CONSTANCIAS c WHERE c.CONTRATO = '" + contractNumber + "' ";
        sqlQuery += "AND c.PERIODO = '" + year + "' AND c.TIPO = '" + type + "' AND COTITULAR='" + owner + "'";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> extractBytes(rs, field));
    }
    
    private byte[] extractBytes(ResultSet rs, String field) throws SQLException {
        var blob = rs.getBlob(field);

        if (blob != null) {
            return blob.getBytes(1, (int) blob.length());
        }
        return null;
    }

    public List<CustomerBeneficiary> getBeneficiaries(String contractNumber) {
        String sqlQuery;
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<CustomerBeneficiary> beneficiaryList = new ArrayList<>();
        String fullName = null;

        try {
            sqlQuery = "SELECT * FROM CONBEN c WHERE c.FOLIO = '" + contractNumber + "'";
            resultList = jdbcTemplate.queryForList(sqlQuery);

            for (Map<String, Object> element : resultList) {
                fullName = element.get("NOMBRE").toString() + " " + element.get("APEPAT").toString() + " " + element.get("APEMAT").toString();

                CustomerBeneficiary customerBeneficiary = new CustomerBeneficiary();
                customerBeneficiary.setFullName(fullName);
                beneficiaryList.add(customerBeneficiary);
            }

        } catch (Exception e) {
            log.error("SisburService::getBeneficiaries", e);
        }

        return beneficiaryList;
    }

    public List<CustomerAttorney> getAttorneys(String contractNumber) {
        String sqlQuery;
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<CustomerAttorney> attorneyList = new ArrayList<>();
        String fullName = null;

        try {
            sqlQuery = "SELECT * FROM CONAPO c WHERE c.COA = 'A' AND c.FOLIO = '" + contractNumber + "'";
            resultList = jdbcTemplate.queryForList(sqlQuery);

            for (Map<String, Object> element : resultList) {
                fullName = element.get("NOMBRE").toString() + " " + element.get("APEPAT").toString() + " " + element.get("APEMAT").toString();
                

                CustomerAttorney attorney = new CustomerAttorney();
                attorney.setFullName(fullName);
                attorney.setOperationAuthorized(Integer.parseInt(element.get("AUTOOPE").toString()));

                attorneyList.add(attorney);
            }
        } catch (Exception e) {
            log.error("SisburService::getAttorneys", e);
        }

        return attorneyList;
    }

    public void getJointHolders(String contractNumber) {
        String sqlQuery;
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<CustomerAttorney> attorneyList = new ArrayList<>();
        String fullName = null;

        try {
            sqlQuery = "SELECT * FROM CONAPO c WHERE c.COA = 'C' AND c.FOLIO = '" + contractNumber + "'";
            resultList = jdbcTemplate.queryForList(sqlQuery);

            for (Map<String, Object> element : resultList) {
                fullName = element.get("NOMBRE").toString() + " " + element.get("APEPAT").toString() + " " + element.get("APEMAT").toString();

                CustomerAttorney attorney = new CustomerAttorney();
                attorney.setFullName(fullName);

                attorneyList.add(attorney);
            }
        } catch (Exception e) {
            log.error("SisburService::getJointHolders", e);
        }
    }

    public List<CustomerBankAccount> getBankAccounts(String contractNumber) {
        String sqlQuery;
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<CustomerBankAccount> bankAccountList = new ArrayList<>();

        try {
            sqlQuery = "SELECT CLABE, CVEINSTITUCION, (select nombanco from catbancos where cvebanco = Cveinstitucion) as BANCO FROM CONCHE c WHERE c.FOLIO = '" + contractNumber + "'";
            resultList = jdbcTemplate.queryForList(sqlQuery);

            for (Map<String, Object> element : resultList) {

                CustomerBankAccount bankAccount = new CustomerBankAccount();
                bankAccount.setAccountNumber(element.get("CLABE").toString());
                bankAccount.setInstitutionKey(element.get("CVEINSTITUCION").toString());
                bankAccount.setInstitutionName(element.get("BANCO").toString());

                bankAccountList.add(bankAccount);
            }
        } catch (Exception e) {
            log.error("SisburService::getBankAccounts", e);
        }

        return bankAccountList;
    }



}
