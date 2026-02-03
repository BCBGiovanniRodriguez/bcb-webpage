package com.bcb.webpage.controllers.customer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bcb.webpage.dto.response.position.Movimiento;
import com.bcb.webpage.dto.response.statement.StatementAccount;
import com.bcb.webpage.dto.response.taxcertificate.TaxCertificate;
import com.bcb.webpage.model.webpage.dto.CashDTO;
import com.bcb.webpage.model.webpage.dto.GeneralDTO;
import com.bcb.webpage.model.webpage.dto.MovementDTO;
import com.bcb.webpage.model.webpage.dto.customer.CustomerAttorney;
import com.bcb.webpage.model.webpage.dto.customer.CustomerBankAccount;
import com.bcb.webpage.model.webpage.dto.customer.CustomerBeneficiary;
import com.bcb.webpage.model.webpage.dto.interfaces.PositionInterface;
import com.bcb.webpage.model.webpage.entity.CustomerMovementReport;
import com.bcb.webpage.model.webpage.entity.CustomerSession;
import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.webpage.entity.customers.CustomerPositionReportRequests;
import com.bcb.webpage.model.webpage.entity.customers.CustomerStatementAccount;
import com.bcb.webpage.model.webpage.entity.customers.CustomerStatementAccountRequests;
import com.bcb.webpage.model.webpage.entity.customers.CustomerTaxCertificate;
import com.bcb.webpage.model.webpage.entity.customers.CustomerTaxCertificateRequests;
import com.bcb.webpage.model.webpage.repository.CustomerContractRepository;
import com.bcb.webpage.model.webpage.repository.CustomerMovementReportRepository;
import com.bcb.webpage.model.webpage.repository.CustomerPositionReportRequestRepository;
import com.bcb.webpage.model.webpage.repository.CustomerStatementAccountRepository;
import com.bcb.webpage.model.webpage.repository.CustomerStatementAccountRequestRepository;
import com.bcb.webpage.model.webpage.repository.CustomerTaxCertificateRepository;
import com.bcb.webpage.model.webpage.repository.CustomerTaxCertificateRequestRepository;
import com.bcb.webpage.service.sisbur.CustomerReportService;
import com.bcb.webpage.service.sisbur.SisBurService;
import com.bcb.webpage.service.sisfiscal.SisFiscalService;
import com.bcb.webpage.service.sisfiscal.SisfiscalStatementAccount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/portal-clientes")
public class CustomerController {

    @Autowired
    private SisBurService sisBurService;

    @Autowired
    private SisFiscalService sisFiscalService;

    @Autowired
    private CustomerContractRepository contractRepository;

    @Autowired
    private CustomerTaxCertificateRepository customerTaxCertificateRepository;

    @Autowired
    private CustomerStatementAccountRepository customerStatementAccountRepository;

    @Autowired
    private CustomerMovementReportRepository customerMovementReportRepository;

    @Autowired
    private CustomerStatementAccountRequestRepository statementAccountRequestRepository;

    @Autowired
    private CustomerPositionReportRequestRepository positionReportRequestRepository;

    @Autowired
    private CustomerTaxCertificateRequestRepository taxCertificateRequestRepository;

    private DateTimeFormatter userSessionFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy");

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private DateTimeFormatter mexFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private DateTimeFormatter fileCompleteFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate today = LocalDate.now();

    private LocalDateTime todayTime = LocalDateTime.now();

    private CustomerContract currentCustomerContract = null;

    private ObjectMapper mapper = new ObjectMapper();

    public CustomerController() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private CustomerReportService customerReportService;

    public Double getDoubleValue(String val) {
        return Double.parseDouble(val.replace(",", ""));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/inicio-de-sesion";
        }

        String customerFullName = "";
        CustomerCustomer customer = null;

        // Session Information
        String currentSessionDateStr = "N/A";
        String currentSessionTimeStr = "N/A";
        String lastSessionDateStr = "N/A";
        String lastSessionTimeStr = "N/A";
        
        Double grandTotal = 0D;
        Double cashPercentage = 0D;
        Double moneyMarketPercentage = 0D;
        Double stockMarketPercentage = 0D;
        Double investmentFundsMarketPercentage = 0D;

        List<CashDTO> cashBalanceList = new ArrayList<>();
        List<PositionInterface> stockMarketPositionList = new ArrayList<>();
        List<PositionInterface> moneyMarketPositionList = new ArrayList<>();
        List<PositionInterface> investmentFundsPositionList = new ArrayList<>();
        
        Double cashTotalBalance = 0D;
        Double stockMarketBalance = 0D;
        Double moneyMarketBalance = 0D;
        Double investmentFundsMarketBalance = 0D;

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            customer = currentCustomerContract.getCustomer();
            customerFullName = customer.getCustomerFullName();

            // Get session detail
            List<CustomerSession> sessionList = customer.getSessions();
            CustomerSession currentSession = sessionList.stream()
                .filter(cs -> cs.isCurrent())
                .findFirst()
                .orElse(null);
            
            CustomerSession lastSession = sessionList.stream()
                .filter(cs -> !cs.isCurrent() && (cs.getSessionId() == currentSession.getSessionId() - 1))
                .findAny()
                .orElse(null);

            if (lastSession == null) {
                lastSession = currentSession;
            }

            currentSessionDateStr = WordUtils.capitalizeFully(userSessionFormatter.format(currentSession.getTimestamp()));
            currentSessionTimeStr = timeFormatter.format(currentSession.getTimestamp());
            lastSessionDateStr = WordUtils.capitalizeFully(userSessionFormatter.format(lastSession.getTimestamp()));
            lastSessionTimeStr = timeFormatter.format(lastSession.getTimestamp());

            cashBalanceList = sisBurService.getCashBalance(currentCustomerContract.getContractNumber());
            stockMarketPositionList = sisBurService.getMarketPosition(currentCustomerContract.getContractNumber(), SisBurService.MARKET_TYPE_STOCK_MARKET);
            moneyMarketPositionList = sisBurService.getMarketPosition(currentCustomerContract.getContractNumber(), SisBurService.MARKET_TYPE_MONEY_MARKET);
            investmentFundsPositionList = sisBurService.getMarketPosition(currentCustomerContract.getContractNumber(), SisBurService.MARKET_TYPE_INVESTMENT_FUNDS);

            for (CashDTO element : cashBalanceList) {
                cashTotalBalance += element.getCurrentBalance();
            }
            // 
            for (PositionInterface positionInterface : stockMarketPositionList) {
                stockMarketBalance += ((GeneralDTO) positionInterface).getSecurities() * ((GeneralDTO) positionInterface).getDirtyPrice24();
            }

            for (PositionInterface positionInterface : moneyMarketPositionList) {
                if (((GeneralDTO) positionInterface).getHolding().equals("REP") && !((GeneralDTO) positionInterface).getEmmiter().equals("REPORTO")) {
                    moneyMarketBalance += ((GeneralDTO) positionInterface).getAward() + ((GeneralDTO) positionInterface).getAmount();
                } else {
                    moneyMarketBalance += ((GeneralDTO) positionInterface).getSecurities() * ((GeneralDTO) positionInterface).getDirtyPrice24();
                }
            }

            for (PositionInterface positionInterface : investmentFundsPositionList) {
                investmentFundsMarketBalance += ((GeneralDTO) positionInterface).getSecurities() * ((GeneralDTO) positionInterface).getDirtyPrice();
            }
            System.out.println("CashTotalBalance: " + cashTotalBalance);
            grandTotal = cashTotalBalance + stockMarketBalance + moneyMarketBalance + investmentFundsMarketBalance;
            cashPercentage = (cashTotalBalance * 100) / grandTotal;
            stockMarketPercentage = (stockMarketBalance * 100) / grandTotal;
            moneyMarketPercentage = (moneyMarketBalance * 100) / grandTotal;
            investmentFundsMarketPercentage = (investmentFundsMarketBalance * 100) / grandTotal;
        } catch (Exception e) {
            System.out.println("Error on CustomerController::dashboard " + e.getLocalizedMessage());
        }

        // Plain Values
        model.addAttribute("customerFullName", customerFullName);
        model.addAttribute("currentSessionDateStr", currentSessionDateStr);
        model.addAttribute("currentSessionTimeStr", currentSessionTimeStr);
        model.addAttribute("lastSessionDateStr", lastSessionDateStr);
        model.addAttribute("lastSessionTimeStr", lastSessionTimeStr);

        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("cashPercentage", cashPercentage);
        model.addAttribute("stockMarketPercentage", stockMarketPercentage);
        model.addAttribute("moneyMarketPercentage", moneyMarketPercentage);
        model.addAttribute("investmentFundsMarketPercentage", investmentFundsMarketPercentage);

        model.addAttribute("totalBalanceMN", cashTotalBalance);
        model.addAttribute("totalBalanceStockMarket", stockMarketBalance);
        model.addAttribute("totalBalanceMoneyMarket", moneyMarketBalance);
        model.addAttribute("investmentFundsMarketBalance", investmentFundsMarketBalance);

        return "customer/dashboard";
    }
    
    @GetMapping("/detalle")
    public String customerDetail(Authentication authentication, Model model) throws JsonMappingException, JsonProcessingException {

        if (authentication == null) {
            return "redirect:/inicio-de-sesion";
        }
        
        CustomerCustomer customer = null;
        List<CustomerBeneficiary> beneficiaryList = new ArrayList<>();
        List<CustomerAttorney> attorneyList = new ArrayList<>();
        List<CustomerBankAccount> bankAccountList = new ArrayList<>();

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            customer = currentCustomerContract.getCustomer();

            beneficiaryList = sisBurService.getBeneficiaries(currentCustomerContract.getContractNumber());
            attorneyList = sisBurService.getAttorneys(currentCustomerContract.getContractNumber());
            bankAccountList = sisBurService.getBankAccounts(currentCustomerContract.getContractNumber());
        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerDetail " + e.getLocalizedMessage());
        }

        model.addAttribute("customer", customer);
        model.addAttribute("attorneyList", attorneyList);
        model.addAttribute("beneficiaryList", beneficiaryList);
        model.addAttribute("bankAccountList", bankAccountList);

        return "customer/detail";
    }

    @GetMapping("/posicion")
    public String customerPosition(Authentication authentication, Model model) {
        
        if (authentication == null) {
            return "redirect:/inicio-de-sesion";
        }
        
        List<PositionInterface> generalList = new ArrayList<>();
        List<CashDTO> cashBalanceList = new ArrayList<>();
        List<PositionInterface> stockMarketPositionList = new ArrayList<>();
        List<PositionInterface> moneyMarketPositionList = new ArrayList<>();
        List<PositionInterface> investmentFundsPositionList = new ArrayList<>();
        
        List<PositionInterface> moneyMarketDirectPositionList = new ArrayList<>();
        List<PositionInterface> moneyMarketReportPositionList = new ArrayList<>();

        Double cashTotalBalance = 0D;

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            
            cashBalanceList = sisBurService.getCashBalance(currentCustomerContract.getContractNumber());
            for (CashDTO element : cashBalanceList) {
                cashTotalBalance = element.getCurrentBalance();
            }

            stockMarketPositionList = sisBurService.getMarketPosition(currentCustomerContract.getContractNumber(), SisBurService.MARKET_TYPE_STOCK_MARKET);
            moneyMarketPositionList = sisBurService.getMarketPosition(currentCustomerContract.getContractNumber(), SisBurService.MARKET_TYPE_MONEY_MARKET);
            investmentFundsPositionList = sisBurService.getMarketPosition(currentCustomerContract.getContractNumber(), SisBurService.MARKET_TYPE_INVESTMENT_FUNDS);
            generalList = this.getGeneralPosition(cashBalanceList, stockMarketPositionList, moneyMarketPositionList, investmentFundsPositionList);
 
            for (PositionInterface element : moneyMarketPositionList) {
                if (((GeneralDTO) element).getHolding().equals("REP")) {
                    moneyMarketReportPositionList.add(element);
                } else {
                    moneyMarketDirectPositionList.add(element);
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerPosition " + e.getLocalizedMessage());
        }

        model.addAttribute("cashTotalBalance", cashTotalBalance);
        model.addAttribute("today", today);
        model.addAttribute("positionStockList", stockMarketPositionList);
        model.addAttribute("positionMoneyList", moneyMarketPositionList);
        model.addAttribute("moneyMarketReportPositionList", moneyMarketReportPositionList);
        model.addAttribute("moneyMarketDirectPositionList", moneyMarketDirectPositionList);
        model.addAttribute("positionFundsList", investmentFundsPositionList);
        model.addAttribute("cashList", cashBalanceList);
        model.addAttribute("generalList", generalList);

        return "customer/position";
    }

    public List<PositionInterface> getGeneralPosition(List<CashDTO> cashBalanceList, List<PositionInterface> stockMarketPosition, List<PositionInterface> moneyMarketPosition, List<PositionInterface> investmentFundsMarketPosition) {
        Double grandTotal = 0D;
        Double percentage;
        Double cashTotalBalance = 0D;
        
        List<PositionInterface> generalList = new ArrayList<>();

        try {
            // Calculate grand balance
            for (CashDTO element : cashBalanceList) {
                cashTotalBalance += element.getCurrentBalance();
                grandTotal += element.getCurrentBalance();
            }

            for (PositionInterface element : stockMarketPosition) {
                grandTotal += ((GeneralDTO) element).getSecurities() * ((GeneralDTO) element).getDirtyPrice();
            }

            for (PositionInterface element : moneyMarketPosition) {
                if (((GeneralDTO) element).getHolding().equals("REP")) {
                    grandTotal += ((GeneralDTO) element).getAmount() + ((GeneralDTO) element).getAward();
                } else {
                    grandTotal += ((GeneralDTO) element).getSecurities() * ((GeneralDTO) element).getDirtyPrice24();
                }
            }

            for (PositionInterface element : investmentFundsMarketPosition) {
                grandTotal += ((GeneralDTO) element).getSecurities() * ((GeneralDTO) element).getDirtyPrice();
            }

            System.out.println("GrandTotal Calculated: " + grandTotal);

            GeneralDTO cashGeneralDTO = new GeneralDTO();    
            cashGeneralDTO.setMarket("Saldo MN");
            cashGeneralDTO.setEmmiter("");
            cashGeneralDTO.setSerie("");
            cashGeneralDTO.setSecurities(0D);
            cashGeneralDTO.setAverageAmount(0D);
            cashGeneralDTO.setMarketPrice(0D);
            cashGeneralDTO.setMarketValue(cashTotalBalance);
            cashGeneralDTO.setPercentage((cashTotalBalance * 100) / grandTotal); // Se sabe hasta el final
            cashGeneralDTO.setCapitalGainLoss(0D);
            generalList.add(cashGeneralDTO);

            //System.out.println("GrandTotal: " + grandTotal);
            
            for (PositionInterface element : stockMarketPosition) {
                percentage = (((GeneralDTO) element).getSecurities() * ((GeneralDTO) element).getDirtyPrice24() * 100) / grandTotal;
                ((GeneralDTO) element).setPercentage(percentage);
            }

            for (PositionInterface element : moneyMarketPosition) {
                if (((GeneralDTO) element).getHolding().equals("REP")) {
                    percentage = (((GeneralDTO) element).getAward() + ((GeneralDTO) element).getAmount() * 100) / grandTotal;
                } else {
                    percentage = (((GeneralDTO) element).getSecurities() * ((GeneralDTO) element).getDirtyPrice24() * 100) / grandTotal;
                }

                ((GeneralDTO) element).setPercentage(percentage);
            }

            for (PositionInterface element : investmentFundsMarketPosition) {
                percentage = (((GeneralDTO) element).getMarketValue() * 100) / grandTotal;
                ((GeneralDTO) element).setPercentage(percentage);
            }

            generalList.addAll(stockMarketPosition);
            generalList.addAll(moneyMarketPosition);
            generalList.addAll(investmentFundsMarketPosition);

        } catch (Exception e) {
            log.error("CustomerController::getGeneralPosition", e);
        }

        return generalList;
    }

    @GetMapping("/posicion/descargar-reporte")
    public void customerPositionDownload(@RequestParam Integer type, Authentication authentication, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        
        CustomerSession currentSession;
        String contractNumber = "";
        OutputStream outputStream = response.getOutputStream();
        String fileName = "";
        List<PositionInterface> positionList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        List<PositionInterface> moneyMarketDirectPositionList = new ArrayList<>();
        List<PositionInterface> moneyMarketReportPositionList = new ArrayList<>();
        String jsonData = "";
        
        String moneyMarketReportJsonData = "";

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            currentSession = currentCustomerContract.getCustomer().getSessions().stream()
                .filter(cs -> cs.isCurrent())
                .findFirst()
                .orElse(null);

            contractNumber = currentCustomerContract.getContractNumber();
            fileName = contractNumber + "_";
            
            if (type == CustomerReportService.TYPE_GENERAL) {
                fileName += "ReportePosicionGeneral_" + now.format(fileCompleteFormatter) + ".pdf";

                List<CashDTO> cashBalanceList = sisBurService.getCashBalance(contractNumber);
                List<PositionInterface> stockMarketList =  sisBurService.getMarketPosition(contractNumber, SisBurService.MARKET_TYPE_STOCK_MARKET);
                List<PositionInterface> moneyMarketList =  sisBurService.getMarketPosition(contractNumber, SisBurService.MARKET_TYPE_MONEY_MARKET);
                List<PositionInterface> investmentFundsMarketList =  sisBurService.getMarketPosition(contractNumber, SisBurService.MARKET_TYPE_INVESTMENT_FUNDS);

                positionList = this.getGeneralPosition(cashBalanceList, stockMarketList, moneyMarketList, investmentFundsMarketList);
            } else if (type == CustomerReportService.TYPE_STOCK_MARKET) {
                fileName += "ReportePosicionMercadoCapitales_"+ now.format(fileCompleteFormatter) + ".pdf";
                positionList =  sisBurService.getMarketPosition(contractNumber, SisBurService.MARKET_TYPE_STOCK_MARKET);

            } else if (type == CustomerReportService.TYPE_MONEY_MARKET) {
                fileName += "ReportePosicionMercadoDinero_" + now.format(fileCompleteFormatter) + ".pdf";
                positionList =  sisBurService.getMarketPosition(contractNumber, SisBurService.MARKET_TYPE_MONEY_MARKET);

                for (PositionInterface element : positionList) {
                    if (((GeneralDTO) element).getHolding().equals("REP")) {
                        moneyMarketReportPositionList.add(element);
                    } else {
                        moneyMarketDirectPositionList.add(element);
                    }
                }

                jsonData = mapper.writeValueAsString(moneyMarketDirectPositionList);
                moneyMarketReportJsonData = mapper.writeValueAsString(moneyMarketReportPositionList);
            }

            if (type != CustomerReportService.TYPE_MONEY_MARKET) {
                jsonData = mapper.writeValueAsString(positionList);
            }

            CustomerPositionReportRequests positionReportRequests = new CustomerPositionReportRequests();
            positionReportRequests.setRequestedDate(LocalDateTime.now());
            positionReportRequests.setSession(currentSession);
            positionReportRequests.setContractNumber(contractNumber);
            positionReportRequests.setType(type);
            positionReportRequests.setData(jsonData);
            positionReportRequestRepository.saveAndFlush(positionReportRequests);

            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "");
            customerReportService.getOutputStreamReport(currentCustomerContract.getCustomer(), type, outputStream, jsonData, moneyMarketReportJsonData);

        } catch (Exception e) {
            log.error("CustomerController::customerPositionDownload", e);
        }
        
    }
    
    @GetMapping("/movimientos")
    public String customerMovements(
        @RequestParam(name = "startDate", required = false) LocalDate startDate, 
        @RequestParam(name = "endDate", required = false) LocalDate endDate, 
        Authentication authentication,
        Model model) {

        if (authentication == null) {
            return "redirect:/inicio-de-sesion";
        }

        List<Movimiento> movementsList = new ArrayList<Movimiento>();
        
        model.addAttribute("today", today);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("movementsList", movementsList);

        return "customer/movements";
    }

    @PostMapping("/movimientos")
    public String customerMovementsSubmit(
        @RequestParam(required = true) LocalDate startDate, 
        @RequestParam(required = true) LocalDate endDate, 
        @RequestBody String entity,
        Authentication authentication,
        Model model) {

        if (authentication == null) {
            return "redirect:/inicio-de-sesion";
        }

        CustomerCustomer customer = null;

        List<MovementDTO> movementReportList = new ArrayList<>();
        List<Movimiento> movementsList = new ArrayList<Movimiento>();

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            customer = currentCustomerContract.getCustomer();
            
            customerReportService.generateMovementsReport(customer, startDate, endDate);
            movementReportList = customerReportService.getMovementDataList();
        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerMovements " + e.getLocalizedMessage());
        }
            
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("movementsList", movementsList);
        model.addAttribute("movementReportList", movementReportList);

        return "customer/movements";
    }
    
    @GetMapping("/movimientos/descargar-reporte")
    public void downloadMovementReport(@RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaTermino, 
        Authentication authentication, HttpServletResponse response) {
        
        response.setContentType("application/pdf");
        String contractNumber;
        String fileName;
        CustomerSession currentSession;
        List<MovementDTO> data;

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            contractNumber = currentCustomerContract.getContractNumber();

            currentSession = currentCustomerContract.getCustomer().getSessions().stream()
                .filter(cs -> cs.isCurrent())
                .findFirst()
                .orElse(null);

            data = customerReportService.getDataList(currentCustomerContract.getCustomer(), fechaInicio, fechaTermino);
            
            fileName = contractNumber + "_ReporteMovimientos_" + fechaInicio.format(fileFormatter) + "_" + fechaTermino.format(fileFormatter) + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "");

            // Audit
            CustomerMovementReport movementReport = new CustomerMovementReport();
            movementReport.setSession(currentSession);
            movementReport.setContractNumber(currentCustomerContract.getContractNumber());
            movementReport.setStarDate(fechaInicio);
            movementReport.setEndDate(fechaTermino);
            movementReport.setRequestedDate(LocalDateTime.now());
            movementReport.setReportData(mapper.writeValueAsString(data));
            customerMovementReportRepository.saveAndFlush(movementReport);

            customerReportService.getOutputStreamMovementsReport(currentCustomerContract.getCustomer(), data, fechaInicio, fechaTermino, response.getOutputStream());
        } catch (Exception e) {
            log.error("CustomerController::downloadMovementReport", e);
        }
    }

    @GetMapping("/resultados")
    public String customerResults() {

        return "customer/results";
    }

    @GetMapping("/estados-cuenta")
    public String customerStatements(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/inicio-de-sesion";
        }

        List<String> periodList;
        List<Map<String,Object>> statementPeriodList;
        StatementAccount currentStatementAccount = null;
        List<StatementAccount> statementAccountList = new ArrayList<>();
        String dateString;

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);

            periodList = sisFiscalService.getPeriodList();
            statementPeriodList = sisFiscalService.getStatementAccountPeriods(currentCustomerContract.getContractNumber());

            for (String datePeriod : periodList) {
                Map<String,Object> element = statementPeriodList
                    .stream()
                    .filter(
                        e -> e.get("FECHA")
                            .toString()
                            .substring(0, 10)
                            .equals(datePeriod))
                    .findFirst()
                    .orElse(null);

                if (element == null) {
                    statementAccountList.add(null);
                } else {
                    StatementAccount statementAccount = new StatementAccount();
                    statementAccount.setContrato(currentCustomerContract.getContractNumber());
                    dateString = element.get("FECHA").toString().substring(0, 10);
                    LocalDate localDate = LocalDate.parse(dateString);
                    statementAccount.setAnio(localDate.getYear() + "");
                    statementAccount.setMes(localDate.getMonthValue() + "");
                    statementAccount.setFecha(dateString);
    
                    statementAccountList.add(statementAccount);
                }
            }
            statementAccountList = statementAccountList.subList(0, statementPeriodList.size());

            currentStatementAccount = statementAccountList.getFirst();

            statementAccountList.remove(0);

        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage());
        }

        model.addAttribute("currentStatementAccount", currentStatementAccount);
        model.addAttribute("statementAccountList", statementAccountList);

        return "customer/statements";
    }

    @GetMapping("/estados-cuenta/consultar/{number}")
    public String customerStatementAccountQuery(@PathVariable Integer number, @RequestParam(required= false) Integer tipo, 
        Authentication authentication, Model model) {
        
        List<SisfiscalStatementAccount> statementAccountList;
        SisfiscalStatementAccount statementAccount;
        
        Optional<CustomerStatementAccount> customerStatementAccountOptional;
        CustomerStatementAccount customerStatementAccount;
        CustomerSession currentSession;
            
        String contractNumber = "";
        Long statementAccountId = null;
        String year = "";
        String month = "";

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            currentSession = currentCustomerContract.getCustomer().getSessions().stream()
                .filter(cs -> cs.isCurrent())
                .findFirst()
                .orElse(null);

            contractNumber = currentCustomerContract.getContractNumber();
            statementAccountList = sisFiscalService.getStatementAccountList(contractNumber);
            statementAccount = statementAccountList.get(number - 1);
            // Check if file already downloaded
            year += statementAccount.getDate().getYear();
            month += statementAccount.getDate().getMonthValue() < 10 ? "0" + statementAccount.getDate().getMonthValue() : statementAccount.getDate().getMonthValue();

            // Check on DB
            customerStatementAccountOptional = customerStatementAccountRepository.findOneByCustomerContractAndYearAndMonthAndType(currentCustomerContract, year, month, tipo);

            if (customerStatementAccountOptional.isPresent()) {
                statementAccountId = customerStatementAccountOptional.get().getStatementAccountId();
            } else {
                byte[] data = sisFiscalService.getFile(contractNumber, tipo, statementAccount.getDate());
                String extension = tipo == 1 ? "PDF" : "XML";
                String statementAccountFilePath = "contracts/" + contractNumber  + "/statement_accounts/";
                String statementAccountFileName = contractNumber + "_" + year + month + "_EstadoDeCuenta." + extension.toLowerCase();

                this.saveFile(contractNumber, statementAccountFilePath, statementAccountFileName, data);
                customerStatementAccount = this.registerStatementAccount(currentSession, currentCustomerContract, year, month, statementAccountFilePath, statementAccountFileName, tipo);

                statementAccountId = customerStatementAccount.getStatementAccountId();
            }
            
            model.addAttribute("statementAccountId", statementAccountId);

        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage());
        }

        return "customer/statement-search";
    }

    @GetMapping("/estados-cuenta/ver-pdf/{statementAccountId}")
    public ResponseEntity<PathResource> showPdf(@NonNull @PathVariable Long statementAccountId, Authentication authentication) throws IOException {
        if (authentication == null) {
            //return "redirect:/inicio-de-sesion";
        }

        Optional<CustomerStatementAccount> result;
        CustomerStatementAccount statementAccount;
        String fileName = null;
        String path = "";

        try {
            result = customerStatementAccountRepository.findById(statementAccountId);

            if (result.isPresent()) {
                statementAccount = result.get();
                path = statementAccount.getPath();
                fileName = statementAccount.getFilename();
            }

        } catch (Exception e) {
            System.out.println("Error on: " + e.getLocalizedMessage());
        }

        PathResource res = new PathResource(path + fileName);
        HttpHeaders headers = new org.springframework.http.HttpHeaders();

        headers.set("X-Frame-Options", "ALLOW-FROM origin");
        headers.setAccessControlAllowOrigin("*");
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        return ResponseEntity.ok().headers(headers).body(res);
    }

    @GetMapping("/estados-cuenta/descargar/{number}")
    public void customerStatementsDownload(@PathVariable Integer number, @RequestParam Integer tipo,
        Authentication authentication, HttpServletResponse response, Model model) throws IOException {

        String contractNumber;
        List<SisfiscalStatementAccount> statementAccountList;
        SisfiscalStatementAccount statementAccount;
        
        Optional<CustomerStatementAccount> customerStatementAccountOptional;
        CustomerStatementAccount customerStatementAccount;
        CustomerSession currentSession;

        String contentType = tipo == 1 ? "application/pdf" : "application/xml";
        String year = "";
        String month = "";
        String fileName;
        String fullPath;
        
        currentCustomerContract = getCurrentCustomerContract(authentication);
        currentSession = currentCustomerContract.getCustomer().getSessions().stream()
                .filter(cs -> cs.isCurrent())
                .findFirst()
                .orElse(null);

        contractNumber = currentCustomerContract.getContractNumber();
        statementAccountList = sisFiscalService.getStatementAccountList(contractNumber);
        statementAccount = statementAccountList.get(number - 1); // List is 0 index based
        
        year += statementAccount.getDate().getYear();
        month += statementAccount.getDate().getMonthValue() < 10 ? "0" + statementAccount.getDate().getMonthValue() : statementAccount.getDate().getMonthValue();

        // Check on DB
        customerStatementAccountOptional = customerStatementAccountRepository.findOneByCustomerContractAndYearAndMonthAndType(currentCustomerContract, year, month, tipo);
        if (customerStatementAccountOptional.isPresent()) {
            customerStatementAccount = customerStatementAccountOptional.get();
            fileName = customerStatementAccount.getFilename();
        } else {
            byte[] data = sisFiscalService.getFile(contractNumber, tipo, statementAccount.getDate());
            String extension = tipo == 1 ? "PDF" : "XML";
            String statementAccountFilePath = "contracts/" + contractNumber  + "/statement_accounts/";
            String statementAccountFileName = contractNumber + "_" + year + month + "_EstadoDeCuenta." + extension.toLowerCase();
            
            this.saveFile(contractNumber, statementAccountFilePath, statementAccountFileName, data);
            customerStatementAccount = this.registerStatementAccount(currentSession, currentCustomerContract, year, month, statementAccountFilePath, statementAccountFileName, tipo);
            fileName = statementAccountFileName;
        }

        // Load to audit
        CustomerStatementAccountRequests statementAccountRequests = new CustomerStatementAccountRequests();
        statementAccountRequests.setContractNumber(contractNumber);
        statementAccountRequests.setRequestedDate(LocalDateTime.now());
        statementAccountRequests.setSession(currentSession);
        statementAccountRequests.setStatementAccount(customerStatementAccount);
        statementAccountRequestRepository.saveAndFlush(statementAccountRequests);
        
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "");
        fullPath = customerStatementAccount.getPath() + "/" + customerStatementAccount.getFilename();
        InputStream inputStream = new FileInputStream(new File(fullPath));
        int nRead;
        
        while ((nRead = inputStream.read()) != -1) {
            response.getWriter().write(nRead);
        }

        inputStream.close();
    }

    private void saveFile(String contractNumber, String filePath, String fileName, byte[] data) throws IOException {
        // Create directories if not exists
        Path path = Paths.get(filePath);
        Files.createDirectories(path);
        OutputStream outputStream = new FileOutputStream(filePath + fileName);
        outputStream.write(data);
        outputStream.close();
    }

    private CustomerStatementAccount registerStatementAccount(CustomerSession session, CustomerContract contract, String year, String month, String filePath, String fileName, Integer type) {
        CustomerStatementAccount customerStatementAccount = new CustomerStatementAccount();
        customerStatementAccount.setSession(session);
        customerStatementAccount.setCustomerContract(contract);
        customerStatementAccount.setDownloadedDate(LocalDateTime.now());
        customerStatementAccount.setYear(year);
        customerStatementAccount.setMonth(month);
        customerStatementAccount.setPath(filePath);
        customerStatementAccount.setFilename(fileName);
        customerStatementAccount.setType(type);

        customerStatementAccountRepository.saveAndFlush(customerStatementAccount);

        return customerStatementAccount;
    }

    @GetMapping("/constancia-fiscal")
    public String customerTaxCertificates(Authentication authentication, Model model) {
        
        if (authentication == null) {
            return "redirect:/inicio-de-sesion";
        }

        List<TaxCertificate> taxCertificateList = new ArrayList<>();
        Set<String> yearSet = new TreeSet<>();
        String taxCertificatesJson = null;
        
        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            
            resultList = sisBurService.getPeriodList(currentCustomerContract.getContractNumber());
            
            for (Map<String, Object> element : resultList) {
                TaxCertificate taxCertificate = new TaxCertificate();
                taxCertificate.setPeriodo(element.get("PERIODO").toString());
                taxCertificate.setTipo(element.get("TIPO").toString());
                taxCertificate.setCotitular(element.get("COTITULAR").toString());

                taxCertificateList.add(taxCertificate);

                yearSet.add(taxCertificate.getPeriodo());
            }

            taxCertificatesJson = mapper.writeValueAsString(taxCertificateList);

        } catch (Exception e) {
            System.out.println("Error on CustomerController: " + e.getLocalizedMessage());
        }

        model.addAttribute("yearSet", yearSet);
        model.addAttribute("taxCertificatesList", taxCertificateList);
        model.addAttribute("taxCertificatesJson", taxCertificatesJson);

        return "customer/tax-certificates";
    }

    @GetMapping("/constancia-fiscal/consultar")
    public String customerTaxCertificatesQuery(@RequestParam String year, @RequestParam Integer type, @RequestParam Integer owner, 
        Authentication authentication, Model model) {
        
        String contractNumber = null;
        Long customerTaxCertificateId = null;

        CustomerTaxCertificate customerTaxCertificate = null;

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            contractNumber = currentCustomerContract.getContractNumber();

            CustomerSession currentSession = currentCustomerContract.getCustomer().getSessions().stream()
                .filter(cs -> cs.isCurrent())
                .findFirst()
                .orElse(null);

            Optional<CustomerTaxCertificate> result = customerTaxCertificateRepository.findOneByYearAndTypeAndOwnerAndFileTypeAndCustomerContract(year.toString(), owner, type, CustomerTaxCertificate.FILE_TYPE_PDF, currentCustomerContract);

            if (result.isPresent()) { // Already downloaded, get and send taxCertificateId
                customerTaxCertificate = result.get();
                customerTaxCertificateId = customerTaxCertificate.getTaxCertificateId();
            } else {
                byte[] data = sisBurService.getTaxCertificateFile(contractNumber, year, type, owner, CustomerTaxCertificate.FILE_TYPE_PDF);
                String filePath = "contracts/" + contractNumber  + "/tax_certificates/";
                String fileName = contractNumber + "_" + year + "_" + CustomerTaxCertificate.taxCertificateTypes[type] + ".pdf";

                this.saveFile(contractNumber, filePath, fileName, data);
                customerTaxCertificate = this.registerTaxCertificate(currentCustomerContract, currentSession, filePath, fileName, year, type, owner, CustomerTaxCertificate.FILE_TYPE_PDF);
                customerTaxCertificateId = customerTaxCertificate.getTaxCertificateId();
            }

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        model.addAttribute("customerTaxCertificateId", customerTaxCertificateId);
        
        return "customer/tax-certificate-view";
    }

    private CustomerTaxCertificate registerTaxCertificate(CustomerContract customerContract, CustomerSession session, String path, String filename, String year, Integer type, Integer owner, Integer fileType) {
        CustomerTaxCertificate customerTaxCertificate = new CustomerTaxCertificate();
        customerTaxCertificate.setCustomerContract(customerContract);
        customerTaxCertificate.setSession(session);
        customerTaxCertificate.setYear(year);
        customerTaxCertificate.setType(type);
        customerTaxCertificate.setOwner(owner);
        customerTaxCertificate.setFileType(fileType);
        customerTaxCertificate.setPath(path);
        customerTaxCertificate.setFilename(filename);
        customerTaxCertificate.setDownloadedDate(LocalDateTime.now());

        customerTaxCertificateRepository.saveAndFlush(customerTaxCertificate);

        return customerTaxCertificate;
    }

    @GetMapping("/constancia-fiscal/descargar")
    public void customerTaxCertificateDownload(@RequestParam String year, @RequestParam Integer type, 
        @RequestParam Integer owner, @RequestParam Integer fileType, 
        Authentication authentication, HttpServletResponse response, Model model) throws IOException {

        String contractNumber;
        String contentType = fileType == 1 ? "application/pdf" : "application/xml";

        Optional<CustomerTaxCertificate> customerTaxCertificateOptional;
        CustomerTaxCertificate customerTaxCertificate;
        CustomerSession currentSession;

        currentCustomerContract = getCurrentCustomerContract(authentication);
        currentSession = currentCustomerContract.getCustomer().getSessions().stream()
                .filter(cs -> cs.isCurrent())
                .findFirst()
                .orElse(null);
            
        contractNumber = currentCustomerContract.getContractNumber();
        customerTaxCertificateOptional = customerTaxCertificateRepository.findOneByYearAndTypeAndOwnerAndFileTypeAndCustomerContract(year, type, owner, fileType, currentCustomerContract);

        if (customerTaxCertificateOptional.isPresent()) {
            customerTaxCertificate = customerTaxCertificateOptional.get();
        } else {
            byte[] data = sisBurService.getTaxCertificateFile(contractNumber, year, type, owner, fileType);
            String extension = fileType == 1 ? "PDF" : "XML";
            String filePath = "contracts/" + contractNumber  + "/tax_certificates/";
            String fileName = contractNumber + "_" + year + "_" + CustomerTaxCertificate.taxCertificateTypes[type] + "." + extension.toLowerCase();

            this.saveFile(contractNumber, filePath, fileName, data);
            customerTaxCertificate = this.registerTaxCertificate(currentCustomerContract, currentSession, filePath, fileName, year, type, owner, fileType);
        }

        // Audit
        CustomerTaxCertificateRequests taxCertificateRequests = new CustomerTaxCertificateRequests();
        taxCertificateRequests.setContractNumber(contractNumber);
        taxCertificateRequests.setRequestedDate(LocalDateTime.now());
        taxCertificateRequests.setSession(currentSession);
        taxCertificateRequests.setTaxCertificate(customerTaxCertificate);
        taxCertificateRequestRepository.saveAndFlush(taxCertificateRequests);

        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=" + customerTaxCertificate.getFilename());
        
        InputStream inputStream = new FileInputStream(new File(customerTaxCertificate.getPath() + customerTaxCertificate.getFilename()));
        int nRead;
        
        while ((nRead = inputStream.read()) != -1) {
            response.getWriter().write(nRead);
        }

        inputStream.close();
    }

    @GetMapping("/constancia-fiscal/ver-pdf/{customerTaxCertificateId}")
    public ResponseEntity<PathResource> showTaxCertificate(@NonNull @PathVariable Long customerTaxCertificateId, Authentication authentication) {

        Optional<CustomerTaxCertificate> result;
        CustomerTaxCertificate taxCertificate;
        String customerTaxCertificateFileName = null;
        String customerTaxCertificateFilePath = null;

        try {
            result = customerTaxCertificateRepository.findById(customerTaxCertificateId);

            if (result.isPresent()) {
                taxCertificate = result.get();
                customerTaxCertificateFileName = taxCertificate.getFilename();
                customerTaxCertificateFilePath = taxCertificate.getPath();
            }

        } catch (Exception e) {
            System.out.println("Error on CustomerController::showTaxCertificate: " + e.getLocalizedMessage());
        }

        PathResource res = new PathResource(customerTaxCertificateFilePath + customerTaxCertificateFileName);

        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("X-Frame-Options", "ALLOW-FROM origin");
        headers.setAccessControlAllowOrigin("*");
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename(customerTaxCertificateFileName).build());
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(res);
    }
    
    @GetMapping("/cambiar-contrato/{contractNumber}")
    public String getChangeContract(@PathVariable String contractNumber, Authentication authentication) {
        
        if (authentication == null) {
            return "redirect:/inicio-de-sesion";
        }

        if (contractNumber != null) {
            try {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                CustomerContract customerContract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
                
                // Verify if contract number in customer contracts
                List<CustomerContract> customerContractList = customerContract.getCustomer().getContracts();
                CustomerContract customerContractTarget = customerContractList
                    .stream()
                    .filter(cc -> cc.getContractNumber().equals(contractNumber))
                    .findFirst()
                    .orElseThrow();
                
                if (customerContractTarget != null) {
                    customerContractTarget.setCurrent(CustomerContract.CURRENT_TRUE);
                    contractRepository.saveAndFlush(customerContractTarget);
                    
                    for (CustomerContract customerContractTmp : customerContractList) {
                        if (customerContractTarget.getCustomerContractId() != customerContractTmp.getCustomerContractId()) {
                            customerContractTmp.setCurrent(CustomerContract.CURRENT_FALSE);

                            contractRepository.saveAndFlush(customerContractTmp);
                        }
                    }
                }


            } catch (Exception e) {
                System.out.println("CustomerController::getChangeContract::[" + e.getLocalizedMessage() + "]");
            }
        }

        return "redirect:/portal-clientes/dashboard";
    }

    @ModelAttribute("customerContractList")
    public List<CustomerContract> customerContractList(Authentication authentication) {
        List<CustomerContract> customerContractsList = new ArrayList<>();

        if (authentication != null) {
            try {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                //
                CustomerContract contract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
                customerContractsList = contract.getCustomer().getContracts();
    
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

        return customerContractsList;
    }
    
    @ModelAttribute("currentCustomerContract")
    public CustomerContract getCurrentCustomerContract(Authentication authentication) {
        CustomerContract currentCustomerContract = null;

        if (authentication != null) {
            try {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                CustomerContract contract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
                System.out.println("Current contract: " + contract.getContractNumber());
                System.out.println("Value of isCurrent(): " + contract.isCurrent());

                if (contract.isCurrent()) {
                    currentCustomerContract = contract;
                } else {
                    for (CustomerContract customerContract : contract.getCustomer().getContracts()) {
                        if(customerContract.isCurrent()) {
                            currentCustomerContract = customerContract;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

        return currentCustomerContract;
    }

}
