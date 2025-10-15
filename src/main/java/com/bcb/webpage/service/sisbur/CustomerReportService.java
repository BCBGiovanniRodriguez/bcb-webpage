package com.bcb.webpage.service.sisbur;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.bcb.webpage.dto.response.position.CustomerMovementPositionResponse;
import com.bcb.webpage.dto.response.position.Posicion;
import com.bcb.webpage.dto.response.position.Saldo;
import com.bcb.webpage.model.sisbur.service.LegacyService;
import com.bcb.webpage.model.webpage.dto.CashDTO;
import com.bcb.webpage.model.webpage.dto.GeneralDTO;
import com.bcb.webpage.model.webpage.dto.MovementDTO;
import com.bcb.webpage.model.webpage.dto.interfaces.PositionInterface;
import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.json.data.JsonDataSource;

@Slf4j
@Service
public class CustomerReportService {

    @Autowired
    private LegacyService legacyService;

    @Autowired
    private SisBurService sisBurService;

    private String rootOutputPath = "./contracts/";

    public static final Integer TYPE_GENERAL = 1;

    public static final Integer TYPE_CASH = 2;

    public static final Integer TYPE_STOCK_MARKET = 3;
    
    public static final Integer TYPE_MONEY_MARKET = 4;

    public static final Integer TYPE_INVESTMENT_FUNDS = 5;

    public static final String[] types = {"", "General", "Efectivo", "Capitales", "Dinero", "FondosInversion"};

    public static final String[] typeFiles = {"", "GeneralPosition_Letter_Landscape.jrxml", "Cash_Letter_Landscape.jrxml", "StockMarket_Letter_Landscape.jrxml", "MoneyMarket_Letter_Landscape.jrxml", "FondosInversion"};

    private DateTimeFormatter filedateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private DateTimeFormatter filedateShortFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private DateTimeFormatter mexFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateTimeFormatter isoShortFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private List<PositionInterface> generalList = new ArrayList<>();

    private List<MovementDTO> movementDataList = new ArrayList<>();

    public void getOutputStreamReport(CustomerCustomer customer, List<PositionInterface> data, Integer type, OutputStream outputStream) {
        ObjectMapper mapper = new ObjectMapper();
        
        // Always generate info from current contract
        CustomerContract currentContract = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            currentContract = customer.getContracts().stream()
                .filter(f -> f.isCurrent())
                .findFirst()
                .orElse(null);

            if (currentContract == null) {
                throw new Exception("Contrato no seleccionado");
            } else {
                File reportTemplate = ResourceUtils.getFile("classpath:" + typeFiles[type]);
                JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplate.getPath());
                
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("P_SEARCH_DATE", now.format(mexFormatter));
                parameters.put("P_CUSTOMER_NAME", customer.getCustomerFullName());
                parameters.put("P_CUSTOMER_CONTRACT_NUMBER", currentContract.getContractNumber());
                
                String jsonData = mapper.writeValueAsString(data);

                ByteArrayInputStream jsonDataInputStream = new ByteArrayInputStream(jsonData.getBytes());
                JsonDataSource jsonDataSource = new JsonDataSource(jsonDataInputStream);

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jsonDataSource);
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            }
        } catch (Exception e) {
            System.out.println("[ReportService][generateReport][" + e.getLocalizedMessage() + "]");
        }
    }

    public void generateReport(CustomerCustomer customer, List<PositionInterface> data, Integer type) {
        ObjectMapper mapper = new ObjectMapper();
        String outputPath = rootOutputPath;
        String typeAsString;
        String fileName = "";
        
        // Always generate info from current contract
        CustomerContract currentContract = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            currentContract = customer.getContracts().stream()
                .filter(f -> f.isCurrent())
                .findFirst()
                .orElse(null);

            if (currentContract == null) {
                throw new Exception("Contrato no seleccionado");
            } else {
                typeAsString = getTypeAsString(type);
                File reportTemplate = ResourceUtils.getFile("classpath:" + typeFiles[type]);
                JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplate.getAbsolutePath());

                outputPath += currentContract.getContractNumber() + "/position_reports";
                Path path = Paths.get(outputPath);
                Files.createDirectories(path);
                
                fileName = currentContract.getContractNumber() + "_" + now.format(filedateFormatter) + "_" + typeAsString + ".pdf";
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("P_SEARCH_DATE", now.format(mexFormatter));
                parameters.put("P_CUSTOMER_NAME", customer.getCustomerFullName());
                parameters.put("P_CUSTOMER_CONTRACT_NUMBER", currentContract.getContractNumber());
                
                String jsonData = mapper.writeValueAsString(data);

                ByteArrayInputStream jsonDataInputStream = new ByteArrayInputStream(jsonData.getBytes());
                JsonDataSource jsonDataSource = new JsonDataSource(jsonDataInputStream);

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jsonDataSource);
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath + "/" + fileName);
            }
        } catch (Exception e) {
            System.out.println("[ReportService][generateReport][" + e.getLocalizedMessage() + "]");
        }
    }

    public void generatePositionReports(CustomerMovementPositionResponse customerPositionResponse, CustomerCustomer customer) {
        List<Posicion> positionList = customerPositionResponse.getPosicion();

        List<Posicion> positionStockList = new ArrayList<Posicion>();
        List<Posicion> positionMoneyList = new ArrayList<Posicion>();
        List<Posicion> positionFundsList = new ArrayList<Posicion>();
        List<Saldo> cashList = new ArrayList<>();
        
        List<PositionInterface> cashReportList = new ArrayList<>();
        List<PositionInterface> stockMarketReportList = new ArrayList<>();
        List<PositionInterface> moneyMarketReportList = new ArrayList<>();

        Double cashTotalBalance = 0D;
        Double grandTotal = 0D;

        try {
            generalList = new ArrayList<>();

            for (Posicion posicion : positionList) {
                if (posicion.isStockMarket()) positionStockList.add(posicion);
                if (posicion.isMoneyMarket()) positionMoneyList.add(posicion);
                if (posicion.isFundsMarket()) positionFundsList.add(posicion);
            }
            
            List<Saldo> balanceList = customerPositionResponse.getSaldo();
            for(int cont = 0; cont < 2; cont++) {
                Saldo saldoTmp = balanceList.get(cont);
                cashTotalBalance += getDoubleValue(saldoTmp.getSaldoActual());
                cashList.add(saldoTmp);
                
                CashDTO cashDTO = new CashDTO();
                cashDTO.setCurrencyName(saldoTmp.getCveDivisa());
                cashDTO.setPendingBalance(getDoubleValue(saldoTmp.getSaldoXLiquidar()));
                cashDTO.setCurrentBalance(getDoubleValue(saldoTmp.getSaldoActual()));
                cashReportList.add(cashDTO);
            }

            grandTotal = getDoubleValue(customerPositionResponse.getTotal());

            Double cashPercentage = (cashTotalBalance * 100) / grandTotal;
            GeneralDTO cashGeneralDTO = new GeneralDTO();

            cashGeneralDTO.setMarket("SaldoMN");
            cashGeneralDTO.setEmmiter("");
            cashGeneralDTO.setSerie("");
            cashGeneralDTO.setSecurities("0");
            cashGeneralDTO.setAverageAmount(0D);
            cashGeneralDTO.setPrice(0D);
            cashGeneralDTO.setValue(cashTotalBalance);
            cashGeneralDTO.setPercentage(cashPercentage);
            cashGeneralDTO.setCapitalGainLoss(0D);
            generalList.add(cashGeneralDTO);
        
            String cssStyle = "";
            Double averageAmount = 0D;
            Double price = 0D;
            Double value = 0D;
            Double capitalGainLoss = 0D;
            Double percentage = 0D;

            for (Posicion posicionTmp : positionStockList) {
                GeneralDTO stockGeneralDto = new GeneralDTO();
                stockGeneralDto.setMarket("Capitales");
                stockGeneralDto.setEmmiter(posicionTmp.getEmisora());
                stockGeneralDto.setSerie(posicionTmp.getSerie());
                stockGeneralDto.setSecurities(posicionTmp.getTitulos());

                averageAmount = getDoubleValue(posicionTmp.getCostoPromedio());
                price = getDoubleValue(posicionTmp.getCostoXTitulos());
                value = getDoubleValue(posicionTmp.getValorMercado());
                capitalGainLoss = getDoubleValue(posicionTmp.getPlusMinusvalia());
                
                stockGeneralDto.setAverageAmount(averageAmount);
                stockGeneralDto.setPrice(price);
                stockGeneralDto.setValue(value);
                percentage = (100 * value) / grandTotal;
                if (capitalGainLoss > 0D) {
                    cssStyle = "text-success";
                } else if(capitalGainLoss < 0D) {
                    cssStyle = "text-danger";
                }

                stockGeneralDto.setPercentage(percentage);
                stockGeneralDto.setCapitalGainLoss(capitalGainLoss);
                stockGeneralDto.setCssStyle(cssStyle);

                generalList.add(stockGeneralDto);
                stockMarketReportList.add(stockGeneralDto);
            }

            for (Posicion posicionTmp : positionMoneyList) {
                GeneralDTO moneyGeneralDto = new GeneralDTO();
                moneyGeneralDto.setMarket("Dinero");
                moneyGeneralDto.setEmmiter(posicionTmp.getEmisora());
                moneyGeneralDto.setSerie(posicionTmp.getSerie());
                moneyGeneralDto.setSecurities(posicionTmp.getTitulos());
                moneyGeneralDto.setRate(posicionTmp.getTasa());
                moneyGeneralDto.setPeriod(posicionTmp.getPlazo());

                averageAmount = getDoubleValue(posicionTmp.getCostoPromedio());
                price = getDoubleValue(posicionTmp.getCostoXTitulos());
                value = getDoubleValue(posicionTmp.getValorMercado());
                capitalGainLoss = getDoubleValue(posicionTmp.getPlusMinusvalia());
                
                moneyGeneralDto.setAverageAmount(averageAmount);
                moneyGeneralDto.setPrice(price);
                moneyGeneralDto.setValue(value);

                percentage = (100 * value) / grandTotal;

                moneyGeneralDto.setPercentage(percentage);
                moneyGeneralDto.setCapitalGainLoss(capitalGainLoss);
                
                cssStyle = "";
                if (capitalGainLoss > 0D) {
                    cssStyle = "text-success";
                } else if(capitalGainLoss < 0D) {
                    cssStyle = "text-danger";
                }
                moneyGeneralDto.setCssStyle(cssStyle);
                generalList.add(moneyGeneralDto);
                moneyMarketReportList.add(moneyGeneralDto);
            }

            // Generate reports
            this.generateReport(customer, moneyMarketReportList, CustomerReportService.TYPE_MONEY_MARKET);
            this.generateReport(customer, stockMarketReportList, CustomerReportService.TYPE_STOCK_MARKET);
            this.generateReport(customer, generalList, CustomerReportService.TYPE_GENERAL);
            this.generateReport(customer, cashReportList, CustomerReportService.TYPE_CASH);


            
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage());
        }

    }

    public void getOutputStreamMovementsReport(CustomerCustomer customer, List<MovementDTO> movementDataList, LocalDate startDate, LocalDate endDate, OutputStream outputStream) {
        ObjectMapper mapper = new ObjectMapper();
        
        // Always generate info from current contract
        CustomerContract currentContract = null;

        try {
            currentContract = customer.getContracts().stream()
                .filter(f -> f.isCurrent())
                .findFirst()
                .orElse(null);

            if (currentContract == null) {
                throw new Exception("Contrato no seleccionado");
            } else {
                File reportTemplate = ResourceUtils.getFile("classpath:Movements_Letter_Landscape.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplate.getPath());
                
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("P_START_DATE", startDate.format(mexFormatter));
                parameters.put("P_END_DATE", endDate.format(mexFormatter));
                parameters.put("P_CUSTOMER_NAME", customer.getCustomerFullName());
                parameters.put("P_CUSTOMER_CONTRACT_NUMBER", currentContract.getContractNumber());
                
                String jsonData = mapper.writeValueAsString(movementDataList);

                ByteArrayInputStream jsonDataInputStream = new ByteArrayInputStream(jsonData.getBytes());
                JsonDataSource jsonDataSource = new JsonDataSource(jsonDataInputStream);

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jsonDataSource);
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            }
            
        } catch (Exception e) {
            System.out.println("[ReportService][generateMovementsReport][" + e.getLocalizedMessage() + "]");
        }
    }

    public void generateMovementsReport(CustomerCustomer customer, LocalDate startDate, LocalDate endDate) {
        ObjectMapper mapper = new ObjectMapper();
        String outputPath = rootOutputPath;
        String fileName = "";
        // Always generate info from current contract
        CustomerContract currentContract = null;

        try {
            currentContract = customer.getContracts().stream()
                .filter(f -> f.isCurrent())
                .findFirst()
                .orElse(null);

            if (currentContract == null) {
                throw new Exception("Contrato no seleccionado");
            } else {
                movementDataList = this.getDataList(customer, startDate, endDate);

                File reportTemplate = ResourceUtils.getFile("classpath:Movements_Letter_Landscape.jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplate.getPath());

                outputPath += currentContract.getContractNumber() + "/movement_reports";
                Path path = Paths.get(outputPath);
                Files.createDirectories(path);
                
                fileName = currentContract.getContractNumber() + "_" + startDate.format(filedateShortFormatter) + "_" + endDate.format(filedateShortFormatter) + "_Movements.pdf";
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("P_START_DATE", startDate.format(mexFormatter));
                parameters.put("P_END_DATE", endDate.format(mexFormatter));
                parameters.put("P_CUSTOMER_NAME", customer.getCustomerFullName());
                parameters.put("P_CUSTOMER_CONTRACT_NUMBER", currentContract.getContractNumber());
                
                String jsonData = mapper.writeValueAsString(movementDataList);

                ByteArrayInputStream jsonDataInputStream = new ByteArrayInputStream(jsonData.getBytes());
                JsonDataSource jsonDataSource = new JsonDataSource(jsonDataInputStream);

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jsonDataSource);
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath + "/" + fileName);
            }
            
        } catch (Exception e) {
            System.out.println("[ReportService][generateMovementsReport][" + e.getLocalizedMessage() + "]");
        }
    }

    public List<MovementDTO> getDataList(CustomerCustomer customer, LocalDate startDate, LocalDate endDate) {
        List<MovementDTO> data = new ArrayList<>();
        List<Map<String,Object>> movementTypeList = new ArrayList<>();
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<String> incomeOutcomeMovementsList = Arrays.asList("DEP", "RET", "RSB", "RLC", "DLC", "SBC", "TEF", "RSB", "DQM", "DSB", "DSD", "RST", "RCD", "DMB");
        List<String> otherMovementsList = Arrays.asList("APV", "PIN");
        List<String> purchaseMovementsList = Arrays.asList("CRE", "CPA", "CDI");

        try {
            movementTypeList = legacyService.getMovementTypeList();
            dataList = legacyService.getCustomerMovements(customer, startDate, endDate);

            String operationDate = null;
            String saleDate = null;
            String emmiter = null;
            String serie = null;
            Integer period = null;
            Double rate = null;
            Double price = null;
            Integer securities = null;
            Double comision = null;
            Double iva = null;
            Double isr = null;
            Double savageAmount = null;
            Double amount = null;
            String movementName = null;
            Optional<Map<String,Object>> movementType = null;
            
            for (Map<String, Object> row : dataList) {
                operationDate = row.get("FECHAOPERACION").toString().substring(0, 10);
                operationDate = LocalDate.parse(operationDate, isoShortFormatter).format(mexFormatter).toString();
                saleDate = row.get("FECHALIQUIDACION").toString().substring(0, 10);
                saleDate = LocalDate.parse(saleDate, isoShortFormatter).format(mexFormatter).toString();
                emmiter = row.get("EMISORA").toString();
                serie = row.get("SERIE").toString();
                period = Integer.parseInt(row.get("PLAZO").toString());
                rate = getDoubleValue(row.get("TASAREN").toString());
                price = getDoubleValue(row.get("PRECIOSUCIO").toString());
                securities = Integer.parseInt(row.get("TITULOS").toString());
                comision = getDoubleValue(row.get("IMPORTECOMISION").toString());
                iva = getDoubleValue(row.get("IMPORTEIVA").toString());
                isr = getDoubleValue(row.get("IMPORTEISR").toString());
                
                
                String movementKey = row.get("TIPOMOVIMIENTO").toString().trim();
                movementType = movementTypeList.stream()
                .filter(mt -> mt.get("TIPOMOVIMIENTO").toString().trim().equals(movementKey))
                .findAny();
                
                if (movementType.isPresent()) {
                    movementName = movementType.get().get("DESCRIPCION").toString().trim();
                }
                
                if (incomeOutcomeMovementsList.contains(movementKey)) {
                    savageAmount = getDoubleValue(row.get("IMPORTELIMPIO").toString());
                } else if(otherMovementsList.contains(movementKey)) {
                    savageAmount = getDoubleValue(row.get("IMPORTESUCIO").toString());
                } else {
                    savageAmount = securities * price;
                }

                if (purchaseMovementsList.contains(movementKey)) {
                    amount = -savageAmount - iva - isr - comision;
                } else {
                    amount = savageAmount - iva - isr - comision;
                }
                
                MovementDTO movementDTO = new MovementDTO();
                movementDTO.setOperationDate(operationDate);
                movementDTO.setSaleDate(saleDate);
                movementDTO.setConcept(movementName);
                movementDTO.setEmmiter(emmiter);
                movementDTO.setSerie(serie);
                movementDTO.setPeriod(period.toString());
                movementDTO.setRate(rate.toString());
                movementDTO.setPrice(price.toString());
                movementDTO.setSecurities(securities.toString());
                movementDTO.setSavageAmount(savageAmount.toString());
                movementDTO.setCommision(comision.toString());
                movementDTO.setIva(iva.toString());
                movementDTO.setIsr(isr.toString());
                movementDTO.setAmount(amount.toString());

                data.add(movementDTO);
            }
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage());
        }

        return data;
    }

    /**
     * 
     * @param type
     * @return
     * @throws Exception
     */
    public String getTypeAsString(Integer type) throws Exception {
        if (type < 0 || type > CustomerReportService.types.length) {
            throw new Exception("ReportService::getTypeAsString::Valor de tipo fuera de rango");
        }

        return CustomerReportService.types[type];
    }

    public Double getDoubleValue(String val) {
        return Double.parseDouble(val.replace(",", ""));
    }

    public List<PositionInterface> getGeneralList() {
        return generalList;
    }

    public void setGeneralList(List<PositionInterface> generalList) {
        this.generalList = generalList;
    }

    public List<MovementDTO> getMovementDataList() {
        return movementDataList;
    }

}
