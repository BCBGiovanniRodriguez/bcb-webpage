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

import com.bcb.webpage.dto.request.CustomerDetailRequest;
import com.bcb.webpage.dto.request.CustomerMovementPositionRequest;
import com.bcb.webpage.dto.response.customer.CustomerDetailResponse;
import com.bcb.webpage.dto.response.customer.ListaBeneficiaro;
import com.bcb.webpage.dto.response.customer.ListaCuentum;
import com.bcb.webpage.dto.response.position.CustomerMovementPositionResponse;
import com.bcb.webpage.dto.response.position.Movimiento;
import com.bcb.webpage.dto.response.position.Posicion;
import com.bcb.webpage.dto.response.position.Saldo;
import com.bcb.webpage.dto.response.statement.StatementAccount;
import com.bcb.webpage.dto.response.taxcertificate.TaxCertificate;
import com.bcb.webpage.model.sisbur.service.LegacyService;
import com.bcb.webpage.model.webpage.dto.MovementDTO;
import com.bcb.webpage.model.webpage.dto.interfaces.PositionInterface;
import com.bcb.webpage.model.webpage.entity.CustomerData;
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
import com.bcb.webpage.model.webpage.repository.CustomerCustomerRepository;
import com.bcb.webpage.model.webpage.repository.CustomerDataRepository;
import com.bcb.webpage.model.webpage.repository.CustomerMovementReportRepository;
import com.bcb.webpage.model.webpage.repository.CustomerPositionReportRequestRepository;
import com.bcb.webpage.model.webpage.repository.CustomerSessionRepository;
import com.bcb.webpage.model.webpage.repository.CustomerStatementAccountRepository;
import com.bcb.webpage.model.webpage.repository.CustomerStatementAccountRequestRepository;
import com.bcb.webpage.model.webpage.repository.CustomerTaxCertificateRepository;
import com.bcb.webpage.model.webpage.repository.CustomerTaxCertificateRequestRepository;
import com.bcb.webpage.model.webpage.services.BackendService;
import com.bcb.webpage.service.sisbur.CustomerReportService;
import com.bcb.webpage.service.sisbur.SisBurService;
import com.bcb.webpage.service.sisfiscal.SisFiscalService;
import com.bcb.webpage.service.sisfiscal.SisfiscalStatementAccount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/portal-clientes")
public class CustomerController {

    private final Integer ENVIRONTMENT_MODE = 0;

    private final Integer ENVIRONTMENT_MODE_DEVELOPMENT = 0;

    private final Integer ENVIRONTMENT_MODE_TESTING = 1;

    private final Integer ENVIRONTMENT_MODE_PRODUCTION = 2;

    private final Integer ENVIRONTMENT_MODE_MAINTENANCE = 3;

    @Autowired
    private SisBurService sisBurService;

    @Autowired
    private SisFiscalService sisFiscalService;

    @Autowired
    private LegacyService legacyService;

    @Autowired
    private BackendService backendService;

    @Autowired
    private CustomerContractRepository contractRepository;

    @Autowired
    private CustomerCustomerRepository customerRepository;

    @Autowired
    private CustomerDataRepository customerDataRepository;

    @Autowired
    private CustomerSessionRepository customerSessionRepository;

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

    private DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate today = LocalDate.now();

    private LocalDateTime todayTime = LocalDateTime.now();

    private CustomerContract currentCustomerContract = null;

    private CustomerCustomer customer;

    private ObjectMapper mapper = new ObjectMapper();

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
        // Balance detail
        Posicion positionDetail = null;

        Double totalBalanceMN = 0D;
        Double totalBalanceMoneyMarket = 0D;
        Double totalBalanceStockMarket = 0D;
        
        Double grandTotal = 0D;
        Double cashPercentage = 0D;
        Double moneyMarketPercentage = 0D;
        Double stockMarketPercentage = 0D;
        // Request Response objects
        //CustomerDetailResponse customerDetailResponse = null;
        CustomerMovementPositionResponse customerMovementPositionResponse = null;

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

            // TODO User Detail !! Change, already local!!!!
            //CustomerDetailRequest customerDetailRequest = new CustomerDetailRequest();
            //customerDetailRequest.setContrato(currentCustomerContract.getContractNumber());

            // Get Balance Detail
            CustomerMovementPositionRequest customerMovementPositionRequest = new CustomerMovementPositionRequest();
            customerMovementPositionRequest.setContrato(currentCustomerContract.getContractNumber());
            customerMovementPositionRequest.setTipo("2");
            
            //customerDetailResponse = backendService.customerDetail(customerDetailRequest);

            customerMovementPositionResponse = backendService.customerMovements(customerMovementPositionRequest);
            ArrayList<Posicion> listPositions = customerMovementPositionResponse.getPosicion();
            
            if (listPositions == null) {
                
            } else {
                if (listPositions.size() > 0) {
                    positionDetail = listPositions.getLast();
                    totalBalanceMoneyMarket = getDoubleValue(positionDetail.getSubtotalDin());
                    totalBalanceStockMarket = getDoubleValue(positionDetail.getSubtotalCap());
                }
            }
            
            ArrayList<Saldo> listBalance = customerMovementPositionResponse.getSaldo();
            
            if (listBalance != null && listBalance.size() > 0) {
                for (Saldo saldo : listBalance) {
                    if (!saldo.getCveDivisa().equals("Por Asignar Indeval")) {
                        totalBalanceMN += getDoubleValue(saldo.getSubTotal());
                    }
                }
            }
            // Get percentages
            grandTotal = getDoubleValue(customerMovementPositionResponse.getTotal());
            cashPercentage = (totalBalanceMN * 100) / grandTotal;
            moneyMarketPercentage = (totalBalanceMoneyMarket * 100) / grandTotal;
            stockMarketPercentage = (totalBalanceStockMarket * 100) / grandTotal;

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
        model.addAttribute("moneyMarketPercentage", moneyMarketPercentage);
        model.addAttribute("stockMarketPercentage", stockMarketPercentage);

        model.addAttribute("totalBalanceMN", totalBalanceMN);
        model.addAttribute("totalBalanceMoneyMarket", totalBalanceMoneyMarket);
        model.addAttribute("totalBalanceStockMarket", totalBalanceStockMarket);

        return "customer/dashboard";
    }
    
    @GetMapping("/detalle")
    public String customerDetail(Authentication authentication, Model model) throws JsonMappingException, JsonProcessingException {

        if (authentication == null) {
            return "redirect:/inicio-de-sesion";
        }
        
        CustomerCustomer customer = null;
        CustomerDetailResponse customerDetailResponse = null;
        CustomerDetailRequest customerDetailRequest = null;
        Boolean hasError = false;
        String errorMessage = "";
        
        List<ListaBeneficiaro> listaBeneficiaros = new ArrayList<>();
        List<ListaCuentum> listaCuenta = new ArrayList<>();

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            customer = currentCustomerContract.getCustomer();

            customerDetailRequest = new CustomerDetailRequest();
            customerDetailRequest.setContrato(currentCustomerContract.getContractNumber());
            
            customerDetailResponse = backendService.customerDetail(customerDetailRequest);
            //System.out.println(customerDetailResponse);

            listaBeneficiaros = customerDetailResponse.getBeneficiarios().getListaBeneficiaros();
            listaCuenta = customerDetailResponse.getCuenta().getListaCuenta();
        } catch (Exception e) {
            // Inform of error!
            System.out.println("Error on CustomerController::customerDetail " + e.getLocalizedMessage());
            // Get last saved if exists
            Long contractNumber = Long.parseLong(currentCustomerContract.getContractNumber());
            CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestType(contractNumber, CustomerData.REQUEST_CUSTOMER_DETAIL);
            customerDetailResponse = mapper.readValue(customerData.getData(), CustomerDetailResponse.class);
            listaBeneficiaros = customerDetailResponse.getBeneficiarios().getListaBeneficiaros();
            listaCuenta = customerDetailResponse.getCuenta().getListaCuenta();

            hasError = true;
            errorMessage = "Ocurrio un error, detalle técnico: " + e.getLocalizedMessage();
        }

        model.addAttribute("hasError", hasError);
        model.addAttribute("errorMessage", errorMessage);

        model.addAttribute("customerDetailResponse", customerDetailResponse);
        model.addAttribute("listaBeneficiaros", listaBeneficiaros);
        model.addAttribute("listaCuenta", listaCuenta);
        model.addAttribute("customer", customer);
        
        return "customer/detail";
    }

    @GetMapping("/posicion")
    public String customerPosition(Authentication authentication, Model model) {
        
        if (authentication == null) {
            return "redirect:/inicio-de-sesion";
        }

        CustomerCustomer customer = null;
        
        List<PositionInterface> generalList = new ArrayList<>();
        List<Posicion> positionStockList = new ArrayList<Posicion>();
        List<Posicion> positionMoneyList = new ArrayList<Posicion>();
        List<Posicion> positionFundsList = new ArrayList<Posicion>();
        List<Saldo> cashList = new ArrayList<>();
        
        CustomerMovementPositionResponse customerPositionResponse = null;
        CustomerMovementPositionRequest customerPositionRequest = null;
        Double cashTotalBalance = 0D;
        Double grandTotal = 0D;

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            customer = currentCustomerContract.getCustomer();

            customerPositionRequest = new CustomerMovementPositionRequest();
            customerPositionRequest.setContrato(currentCustomerContract.getContractNumber());
            customerPositionRequest.setTipo("2"); // 1: Movimientos - 2: - Posición

            customerPositionResponse = backendService.customerPosition(customerPositionRequest);
            
            List<Posicion> positionList = customerPositionResponse.getPosicion();
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
            }

            customerReportService.generatePositionReports(customerPositionResponse, customer);
            generalList = customerReportService.getGeneralList();

        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerPosition " + e.getLocalizedMessage());
        }

        model.addAttribute("cashTotalBalance", cashTotalBalance);
        model.addAttribute("today", today);
        model.addAttribute("positionStockList", positionStockList);
        model.addAttribute("positionMoneyList", positionMoneyList);
        model.addAttribute("positionFundsList", positionFundsList);
        model.addAttribute("cashList", cashList);
        model.addAttribute("customerPositionResponse", customerPositionResponse);
        model.addAttribute("generalList", generalList);

        return "customer/position";
    }
    

    @GetMapping("/posicion/reporte-general")
    public String customerPositionGeneralReport() {
        return new String();
    }

    @GetMapping("/posicion/reporte-capitales")
    public String customerPositionStockMarketReport() {
        return new String();
    }
    
    @GetMapping("/posicion/reporte-dinero")
    public String customerPositionMoneyMarketReport() {
        return new String();
    }

    @GetMapping("/posicion/descargar-reporte")
    public void getMethodName(@RequestParam Integer type, Authentication authentication, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        
        CustomerSession currentSession;
        String contractNumber = "";
        OutputStream outputStream = response.getOutputStream();
        String fileName = "";
        List<PositionInterface> positionList = new ArrayList<>();

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            currentSession = currentCustomerContract.getCustomer().getSessions().stream()
                .filter(cs -> cs.isCurrent())
                .findFirst()
                .orElse(null);

            contractNumber = currentCustomerContract.getContractNumber();
            CustomerPositionReportRequests positionReportRequests = new CustomerPositionReportRequests();
            positionReportRequests.setRequestedDate(LocalDateTime.now());
            positionReportRequests.setData(fileName);
            positionReportRequests.setSession(currentSession);
            positionReportRequests.setContractNumber(contractNumber);
            
            if (type == CustomerReportService.TYPE_GENERAL) {
                
                fileName = "ReportePosicionGeneral.pdf";
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "");
                
                List<PositionInterface> stockMarketList =  sisBurService.getMarketPosition(contractNumber, SisBurService.MARKET_TYPE_STOCK_MARKET);
                List<PositionInterface> moneyMarketList =  sisBurService.getMarketPosition(contractNumber, SisBurService.MARKET_TYPE_MONEY_MARKET);
                // Create position
                for (PositionInterface positionInterface : stockMarketList) {
                    positionList.add(positionInterface);
                }
                for (PositionInterface positionInterface : moneyMarketList) {
                    positionList.add(positionInterface);
                }
                
                
                
                positionReportRequests.setType(CustomerPositionReportRequests.TYPE_GENERAL);
                positionReportRequests.setData(mapper.writeValueAsString(positionList));
                positionReportRequestRepository.saveAndFlush(positionReportRequests);

                customerReportService.getOutputStreamReport(currentCustomerContract.getCustomer(), positionList, CustomerReportService.TYPE_GENERAL, outputStream);
            } else if (type == CustomerReportService.TYPE_STOCK_MARKET) {
                
                fileName = "ReportePosicionMercadoCapitales.pdf";
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "");
                List<PositionInterface> stockMarketList =  sisBurService.getMarketPosition(contractNumber, SisBurService.MARKET_TYPE_STOCK_MARKET);
                
                positionReportRequests.setType(CustomerPositionReportRequests.TYPE_STOCK_MARKET);
                positionReportRequests.setData(mapper.writeValueAsString(stockMarketList));
                positionReportRequestRepository.saveAndFlush(positionReportRequests);

                customerReportService.getOutputStreamReport(currentCustomerContract.getCustomer(), stockMarketList, CustomerReportService.TYPE_STOCK_MARKET, outputStream);
            } else if (type == CustomerReportService.TYPE_MONEY_MARKET) { 
                
                fileName = "ReportePosicionMercadoDinero.pdf";
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "");
                
                List<PositionInterface> moneyMarketList =  sisBurService.getMarketPosition(contractNumber, SisBurService.MARKET_TYPE_MONEY_MARKET);
                
                positionReportRequests.setType(CustomerPositionReportRequests.TYPE_MONEY_MARKET);
                positionReportRequests.setData(mapper.writeValueAsString(moneyMarketList));
                positionReportRequestRepository.saveAndFlush(positionReportRequests);
                
                customerReportService.getOutputStreamReport(currentCustomerContract.getCustomer(), moneyMarketList, CustomerReportService.TYPE_MONEY_MARKET, outputStream);
            }
            

        } catch (Exception e) {
            log.error("", e);
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

            CustomerSession currentSession = customer.getSessions().stream()
                .filter(cs -> cs.isCurrent())
                .findFirst()
                .orElse(null);
            
            customerReportService.generateMovementsReport(customer, startDate, endDate);
            movementReportList = customerReportService.getMovementDataList();

            CustomerMovementReport movementReport = new CustomerMovementReport();
            movementReport.setSession(currentSession);
            movementReport.setContractNumber(currentCustomerContract.getContractNumber());
            movementReport.setStarDate(startDate);
            movementReport.setEndDate(endDate);
            movementReport.setRequestedDate(LocalDateTime.now());
            movementReport.setReportData(mapper.writeValueAsString(movementReportList));

            customerMovementReportRepository.saveAndFlush(movementReport);

        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerMovements " + e.getLocalizedMessage());
        }
            
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("movementsList", movementsList);
        model.addAttribute("movementReportList", movementReportList);

        return "customer/movements";
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

        List<Map<String,Object>> statementPeriodList;
        StatementAccount currentStatementAccount = null;
        List<StatementAccount> statementAccountList = new ArrayList<>();
        String dateString;

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            statementPeriodList = sisFiscalService.getStatementAccountPeriods(currentCustomerContract.getContractNumber());

            for (Map<String,Object> statementObj : statementPeriodList) {
                dateString = statementObj.get("FECHA").toString().substring(0, 10);
                LocalDate localDate = LocalDate.parse(dateString);

                StatementAccount statementAccount = new StatementAccount();
                statementAccount.setContrato(currentCustomerContract.getContractNumber());
                statementAccount.setAnio(localDate.getYear() + "");
                statementAccount.setMes(localDate.getMonthValue() + "");
                statementAccount.setFecha(dateString);

                statementAccountList.add(statementAccount);
            }

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
            customerStatementAccountOptional = customerStatementAccountRepository.findOneByCustomerContractAndYearAndMonth(currentCustomerContract, year, month);

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
    public ResponseEntity<PathResource> showPdf(@PathVariable Long statementAccountId, Authentication authentication) throws IOException {
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

        PathResource res = new PathResource(path);
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
    public String customerTaxCertificatesQuery(@RequestParam String year, @RequestParam Integer type, 
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

            Optional<CustomerTaxCertificate> result = customerTaxCertificateRepository.findOneByYearAndTypeAndFileTypeAndCustomerContract(year.toString(), type, CustomerTaxCertificate.FILE_TYPE_PDF, currentCustomerContract);

            if (result.isPresent()) { // Already downloaded, get and send taxCertificateId
                customerTaxCertificate = result.get();
                customerTaxCertificateId = customerTaxCertificate.getTaxCertificateId();
            } else {
                byte[] data = sisBurService.getTaxCertificateFile(contractNumber, year, type, CustomerTaxCertificate.FILE_TYPE_PDF);
                String filePath = "contracts/" + contractNumber  + "/tax_certificates/";
                String fileName = contractNumber + "_" + year + "_" + CustomerTaxCertificate.taxCertificateTypes[type] + ".pdf";

                this.saveFile(contractNumber, filePath, fileName, data);
                customerTaxCertificate = this.registerTaxCertificate(currentCustomerContract, currentSession, filePath, fileName, year, type, CustomerTaxCertificate.FILE_TYPE_PDF);
                customerTaxCertificateId = customerTaxCertificate.getTaxCertificateId();
            }

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        model.addAttribute("customerTaxCertificateId", customerTaxCertificateId);
        
        return "customer/tax-certificate-view";
    }

    private CustomerTaxCertificate registerTaxCertificate(CustomerContract customerContract, CustomerSession session, String path, String filename, String year, Integer type, Integer fileType) {
        CustomerTaxCertificate customerTaxCertificate = new CustomerTaxCertificate();
        customerTaxCertificate.setCustomerContract(customerContract);
        customerTaxCertificate.setSession(session);
        customerTaxCertificate.setYear(year);
        customerTaxCertificate.setType(type);
        customerTaxCertificate.setFileType(fileType);
        customerTaxCertificate.setPath(path);
        customerTaxCertificate.setFilename(filename);
        customerTaxCertificate.setDownloadedDate(LocalDateTime.now());

        customerTaxCertificateRepository.saveAndFlush(customerTaxCertificate);

        return customerTaxCertificate;
    }

    @GetMapping("/constancia-fiscal/descargar")
    public void customerTaxCertificateDownload(@RequestParam String year, @RequestParam Integer type, @RequestParam Integer fileType, 
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
        customerTaxCertificateOptional = customerTaxCertificateRepository.findOneByYearAndTypeAndFileTypeAndCustomerContract(year, type, fileType, currentCustomerContract);

        if (customerTaxCertificateOptional.isPresent()) {
            customerTaxCertificate = customerTaxCertificateOptional.get();
        } else {
            byte[] data = sisBurService.getTaxCertificateFile(contractNumber, year, type, fileType);
            String extension = fileType == 1 ? "PDF" : "XML";
            String filePath = "contracts/" + contractNumber  + "/tax_certificates/";
            String fileName = contractNumber + "_" + year + "_" + CustomerTaxCertificate.taxCertificateTypes[type] + "." + extension.toLowerCase();

            this.saveFile(contractNumber, filePath, fileName, data);
            customerTaxCertificate = this.registerTaxCertificate(currentCustomerContract, currentSession, filePath, fileName, year, type, fileType);
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
    public ResponseEntity<PathResource> showTaxCertificate(@PathVariable Integer customerTaxCertificateId, Authentication authentication) {

        Optional<CustomerTaxCertificate> result;
        CustomerTaxCertificate taxCertificate;
        String customerTaxCertificateFileName = null;
        String customerTaxCertificateFilePath = null;

        try {
            result = customerTaxCertificateRepository.findById(Long.valueOf(customerTaxCertificateId));

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

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            //
            CustomerContract contract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
            customerContractsList = contract.getCustomer().getContracts();

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return customerContractsList;
    }
    
    @ModelAttribute("currentCustomerContract")
    public CustomerContract getCurrentCustomerContract(Authentication authentication) {
        CustomerContract currentCustomerContract = null;

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            CustomerContract contract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
            
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

        return currentCustomerContract;
    }

}
