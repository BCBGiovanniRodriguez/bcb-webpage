package com.bcb.webpage.controllers.customer;

import java.io.ByteArrayInputStream;
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
import java.util.Base64;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bcb.webpage.dto.request.CustomerDetailRequest;
import com.bcb.webpage.dto.request.CustomerMovementPositionRequest;
import com.bcb.webpage.dto.request.CustomerStatementAccountRequest;
import com.bcb.webpage.dto.request.CustomerTaxCertificateRequest;
import com.bcb.webpage.dto.response.customer.CustomerDetailResponse;
import com.bcb.webpage.dto.response.customer.ListaBeneficiaro;
import com.bcb.webpage.dto.response.customer.ListaCuentum;
import com.bcb.webpage.dto.response.position.CustomerMovementPositionResponse;
import com.bcb.webpage.dto.response.position.Movimiento;
import com.bcb.webpage.dto.response.position.Posicion;
import com.bcb.webpage.dto.response.position.Saldo;
import com.bcb.webpage.dto.response.statement.CustomerStatementAccountResponse;
import com.bcb.webpage.dto.response.statement.CustomerStatementFileResponse;
import com.bcb.webpage.dto.response.statement.StatementAccount;
import com.bcb.webpage.dto.response.taxcertificate.CustomerTaxCertificateDetailResponse;
import com.bcb.webpage.dto.response.taxcertificate.CustomerTaxCertificatePeriodResponse;
import com.bcb.webpage.dto.response.taxcertificate.CustomerTaxCertificateResponse;
import com.bcb.webpage.dto.response.taxcertificate.TaxCertificate;
import com.bcb.webpage.model.sisbur.service.LegacyService;
import com.bcb.webpage.model.webpage.dto.MovementDTO;
import com.bcb.webpage.model.webpage.dto.interfaces.PositionInterface;
import com.bcb.webpage.model.webpage.entity.CustomerData;
import com.bcb.webpage.model.webpage.entity.CustomerSession;
import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.webpage.entity.customers.CustomerStatementAccount;
import com.bcb.webpage.model.webpage.entity.customers.CustomerTaxCertificate;
import com.bcb.webpage.model.webpage.repository.CustomerContractRepository;
import com.bcb.webpage.model.webpage.repository.CustomerCustomerRepository;
import com.bcb.webpage.model.webpage.repository.CustomerDataRepository;
import com.bcb.webpage.model.webpage.repository.CustomerSessionRepository;
import com.bcb.webpage.model.webpage.repository.CustomerStatementAccountRepository;
import com.bcb.webpage.model.webpage.repository.CustomerTaxCertificateRepository;
import com.bcb.webpage.model.webpage.services.BackendService;
import com.bcb.webpage.service.sisbur.CustomerReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/portal-clientes")
public class CustomerController {

    private final Integer ENVIRONTMENT_MODE = 0;

    private final Integer ENVIRONTMENT_MODE_DEVELOPMENT = 0;

    private final Integer ENVIRONTMENT_MODE_TESTING = 1;

    private final Integer ENVIRONTMENT_MODE_PRODUCTION = 2;

    private final Integer ENVIRONTMENT_MODE_MAINTENANCE = 3;

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

    private DateTimeFormatter userSessionFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy");

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private DateTimeFormatter mexFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private LocalDate today = LocalDate.now();

    private LocalDateTime todayTime = LocalDateTime.now();

    private CustomerContract currentCustomerContract;

    private CustomerCustomer customer;

    private ObjectMapper mappe r = new ObjectMapper();

    @Autowired
    private CustomerReportService customerReportService;

    public Double getDoubleValue(String val) {
        return Double.parseDouble(val.replace(",", ""));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
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
    

    @GetMapping("/movimientos")
    public String customerMovements(
        @RequestParam(name = "startDate", required = false) LocalDate startDate, 
        @RequestParam(name = "endDate", required = false) LocalDate endDate, 
        Model model) {

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
    

    @GetMapping("/resultados")
    public String customerResults() {
        return "customer/results";
    }

    @GetMapping("/estados-cuenta")
    public String customerStatements(Authentication authentication, Model model) {
        CustomerStatementAccountRequest statementAccountRequest = null;
        CustomerStatementAccountResponse statementAccountResponse = null;

        List<StatementAccount> statementAccountList = new ArrayList<>();
        StatementAccount statementAccount = null;
        StatementAccount currentStatementAccount = null;
        List<StatementAccount> last12StatementAccounts = new ArrayList<>();
        Set<Integer> statementYearsSet = new TreeSet<>();
        List<Integer> statementYearList = new ArrayList<>();
        
        ObjectMapper mapper = new ObjectMapper();
        List<StatementAccount> lastStatementAccounts = new ArrayList<>();
        String lastStatementAccountsJson = null;
        int counter;
        
        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            customer = currentCustomerContract.getCustomer();

            statementAccountRequest = new CustomerStatementAccountRequest();
            statementAccountRequest.setContrato(currentCustomerContract.getContractNumber());

            statementAccountResponse = backendService.getStatementsInfo(statementAccountRequest);

            statementAccountList = statementAccountResponse.getListaEstadosCuenta();
            currentStatementAccount = statementAccountList.getFirst();
            
            for(counter = 1; counter < statementAccountList.size(); counter++) {
                statementAccount = statementAccountList.get(counter);

                if (counter < 13) {
                    last12StatementAccounts.add(statementAccount);
                } else {
                    if (!statementYearList.contains(Integer.parseInt(statementAccount.getAnio()))) {
                        statementYearList.add(Integer.parseInt(statementAccount.getAnio()));
                    }
                    lastStatementAccounts.add(statementAccount);
                }
            }

            lastStatementAccountsJson = mapper.writeValueAsString(lastStatementAccounts);

        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerStatements " + e.getLocalizedMessage());
        }

        model.addAttribute("currentStatementAccount", currentStatementAccount);
        model.addAttribute("last12StatementAccounts", last12StatementAccounts);
        model.addAttribute("statementYearsSet", statementYearList);
        model.addAttribute("lastStatementAccountsJson", lastStatementAccountsJson);

        return "customer/statements";
    }
    
    @PostMapping("/estados-cuenta")
    public String customerStatementsSubmit(Authentication authentication, Model model) {

        return "customer/statements";
    }

    @GetMapping("/estados-cuenta/consultarpdf/{number}")
    public String customerStatementsQuery(@PathVariable Integer number, Authentication authentication, Model model, HttpServletResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        String contractNumber;
        CustomerStatementAccountResponse statementAccountResponse;
        List<StatementAccount> customerStatementList;
        StatementAccount statementAccount;
        CustomerStatementAccount customerStatementAccount;
        CustomerStatementFileResponse customerStatementFileResponse;
        Optional<CustomerStatementAccount> result;
        CustomerContract contract;
        
        String statementAccountFileName = null;
        String statementAccountFilePath = null;
        Long statementAccountId = null;

        try {
            // Get current contract number logged in
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            contractNumber = userDetails.getUsername();
            contract = contractRepository.findOneByContractNumber(contractNumber).get();
            
            // Get list of statements
            CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestType(Long.parseLong(contractNumber), CustomerData.REQUEST_CUSTOMER_STATEMENTS_DATA);
            statementAccountResponse = mapper.readValue(customerData.getData(), CustomerStatementAccountResponse.class);
            customerStatementList = statementAccountResponse.getListaEstadosCuenta();
            // Search for specified statement on the list
            statementAccount = customerStatementList.get(number - 1);
            // Check if the statement is already downloaded, if not download and register
            statementAccountFilePath = "contracts/" + contractNumber  + "/statement_accounts/";
            statementAccountFileName = contractNumber + "_" + statementAccount.getAnio() + statementAccount.getMes() + "_EstadoDeCuenta.pdf";
            result = customerStatementAccountRepository.findOneByCustomerContractAndYearAndMonth(contract, statementAccount.getAnio(), statementAccount.getMes());

            // If not exist, download and store, else do nothing :D... Or check if file exists!!
            if (result.isPresent()) {
                statementAccountId = result.get().getStatementAccountId();
            } else {
                // Download the file via backend service
                String fecha = statementAccount.getAnio() + statementAccount.getMes() + statementAccount.getFecha().substring(0, 2);
                CustomerStatementAccountRequest customerStatementAccountRequest = new CustomerStatementAccountRequest();
                customerStatementAccountRequest.setContrato(contractNumber);
                customerStatementAccountRequest.setPdf("1");
                customerStatementAccountRequest.setFecha(fecha);

                customerStatementFileResponse = backendService.getStatementsFile(customerStatementAccountRequest);

                if (customerStatementFileResponse.getRespuesta() != "Error") {
                    // Create directories if not exists
                    Path path = Paths.get(statementAccountFilePath);
                    Files.createDirectories(path);
                    // Decode from Base64 to binary
                    byte[] byteArray = Base64.getDecoder().decode(customerStatementFileResponse.getConstancia());
                    // Save the binary file
                    OutputStream outputStream = new FileOutputStream(statementAccountFilePath + statementAccountFileName);
                    outputStream.write(byteArray);
                    outputStream.close();

                    customerStatementAccount = new CustomerStatementAccount();
                    customerStatementAccount.setCustomerContract(contract);
                    customerStatementAccount.setDownloadedDate(LocalDateTime.now());
                    customerStatementAccount.setYear(statementAccount.getAnio());
                    customerStatementAccount.setMonth(statementAccount.getMes());
                    customerStatementAccount.setPath(statementAccountFilePath + statementAccountFileName);
                    System.out.println(customerStatementAccount.toString());

                    customerStatementAccountRepository.saveAndFlush(customerStatementAccount);
                    statementAccountId = customerStatementAccount.getStatementAccountId();
                }
            }

        } catch (Exception e) {
            System.out.println("Error on customerStatementsQuery:: " + e.getLocalizedMessage());
        }

        model.addAttribute("statementAccountId", statementAccountId);

        return "customer/statement-search";
    }

    @GetMapping("/estados-cuenta/ver-pdf/{statementAccountId}")
    public ResponseEntity<PathResource> showPdf(@PathVariable Integer statementAccountId, Authentication authentication) throws IOException {
        Optional<CustomerStatementAccount> result;
        String statementAccountFileName = null;
        String statementAccountFilePath = null;

        try {
            result = customerStatementAccountRepository.findById(Long.valueOf(statementAccountId));
            if (result.isPresent()) {
                statementAccountFilePath = result.get().getPath();
            }

        } catch (Exception e) {
            System.out.println("Error on: " + e.getLocalizedMessage());
        }

        PathResource res = new PathResource(statementAccountFilePath);
        
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("X-Frame-Options", "ALLOW-FROM origin");
        headers.setAccessControlAllowOrigin("*");
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename(statementAccountFileName).build());
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        return ResponseEntity.ok().headers(headers).body(res);
    }

    @GetMapping("/estados-cuenta/consultarxml/{number}")
    public String customerStatementsXmlQuery(@PathVariable Integer number, Authentication authentication, Model model, HttpServletResponse response) {
        return "customer/statement-search-xml";
    }

    // function to get file identifier
    private String getStatementAccountFileIdentifier(String contractNumber, int positionList, int type) {
        ObjectMapper mapper = new ObjectMapper();
        String extensionFile;
        CustomerStatementAccountResponse statementAccountResponse;
        List<StatementAccount> customerStatementList;
        StatementAccount statementAccount;
        CustomerStatementAccount customerStatementAccount;
        CustomerStatementFileResponse customerStatementFileResponse;
        Optional<CustomerStatementAccount> result;
        CustomerContract contract;
        
        String statementAccountFileName = null;
        String statementAccountFilePath = null;
        Long statementAccountId = null;

        try {
            extensionFile = type == CustomerStatementAccount.STATEMENT_ACCOUNT_TYPE_PDF ? "pdf" : "xml";
            // Get current contract number logged in
            //UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            //contractNumber = userDetails.getUsername();
            contract = contractRepository.findOneByContractNumber(contractNumber).get();
            
            // Get list of statements
            CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestType(Long.parseLong(contractNumber), CustomerData.REQUEST_CUSTOMER_STATEMENTS_DATA);
            statementAccountResponse = mapper.readValue(customerData.getData(), CustomerStatementAccountResponse.class);
            customerStatementList = statementAccountResponse.getListaEstadosCuenta();
            // Search for specified statement on the list
            statementAccount = customerStatementList.get(positionList);
            // Check if the statement is already downloaded, if not download and register
            statementAccountFilePath = "contracts/" + contractNumber  + "/statement_accounts/";
            statementAccountFileName = contractNumber + "_" + statementAccount.getAnio() + statementAccount.getMes() + "_EstadoDeCuenta.pdf";
            result = customerStatementAccountRepository.findOneByCustomerContractAndYearAndMonth(contract, statementAccount.getAnio(), statementAccount.getMes());

            // If not exist, download and store, else do nothing :D... Or check if file exists!!
            if (result.isPresent()) {
                statementAccountId = result.get().getStatementAccountId();
            } else {
                // Download the file via backend service
                CustomerStatementAccountRequest customerStatementAccountRequest = new CustomerStatementAccountRequest();
                customerStatementAccountRequest.setContrato(contractNumber);
                customerStatementAccountRequest.setPdf("1");
                customerStatementAccountRequest.setFecha(statementAccount.getFecha().substring(0, 10));
                customerStatementFileResponse = backendService.getStatementsFile(customerStatementAccountRequest);

                if (customerStatementFileResponse.getRespuesta() != "Error") {
                    // Create directories if not exists
                    Path path = Paths.get(statementAccountFilePath);
                    Files.createDirectories(path);
                    // Decode from Base64 to binary
                    byte[] byteArray = Base64.getDecoder().decode(customerStatementFileResponse.getConstancia());
                    // Save the binary file
                    OutputStream outputStream = new FileOutputStream(statementAccountFilePath + statementAccountFileName);
                    outputStream.write(byteArray);
                    outputStream.close();

                    customerStatementAccount = new CustomerStatementAccount();
                    customerStatementAccount.setCustomerContract(contract);
                    customerStatementAccount.setDownloadedDate(LocalDateTime.now());
                    customerStatementAccount.setYear(statementAccount.getAnio());
                    customerStatementAccount.setMonth(statementAccount.getMes());
                    customerStatementAccount.setPath(statementAccountFilePath + statementAccountFileName);
                    System.out.println(customerStatementAccount.toString());

                    customerStatementAccountRepository.saveAndFlush(customerStatementAccount);
                    statementAccountId = customerStatementAccount.getStatementAccountId();
                }
            }

        } catch (Exception e) {
            System.out.println("Error on customerStatementsQuery:: " + e.getLocalizedMessage());
        }

        //model.addAttribute("statementAccountId", statementAccountId);

        return "customer/statement-search";
    }
    

    @GetMapping("/estados-cuenta/descargar/{number}")
    public String customerStatementsDownload(@PathVariable Integer number, Authentication authentication, Model model) {

        return "customer/statement-download";
    }



    @GetMapping("/constancia-fiscal")
    public String customerTaxCertificates(Authentication authentication, Model model) {
        CustomerTaxCertificateRequest taxCertificateRequest = null;
        CustomerTaxCertificateDetailResponse taxCertificateDetailResponse = null;
        List<TaxCertificate> taxCertificateList = new ArrayList<>();
        Set<String> yearSet = new TreeSet<>();
        String taxCertificatesJson = null;
        
        CustomerCustomer customer = null;

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            customer = currentCustomerContract.getCustomer();

            taxCertificateRequest = new CustomerTaxCertificateRequest();
            taxCertificateRequest.setContrato(currentCustomerContract.getContractNumber());

            taxCertificateDetailResponse = backendService.getTaxCertificatesDetail(taxCertificateRequest);
            taxCertificateList = taxCertificateDetailResponse.getListaConstancias();
            
            for (TaxCertificate taxCertificate : taxCertificateList) {
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

    @GetMapping("/constancia-fiscal/consultarpdf")
    public String customerTaxCertificateViewPdf(@RequestParam Integer year, @RequestParam Integer type, Authentication authentication, Model model) {
        CustomerTaxCertificateRequest taxCertificateRequest = null;
        CustomerTaxCertificateResponse taxCertificateResponse = null;

        String contractNumber = null;
        CustomerTaxCertificate customerTaxCertificate = null;
        Long customerTaxCertificateId = null;

        try {
            currentCustomerContract = getCurrentCustomerContract(authentication);
            customer = currentCustomerContract.getCustomer();
            contractNumber = currentCustomerContract.getContractNumber();

            Optional<CustomerTaxCertificate> result = customerTaxCertificateRepository.findByYearAndTypeAndFileTypeAndCustomerContract(year.toString(), type, CustomerTaxCertificate.FILE_TYPE_PDF, currentCustomerContract);

            if (result.isPresent()) { // Already downloaded, get and send taxCertificateId
                customerTaxCertificate = result.get();
                customerTaxCertificateId = customerTaxCertificate.getTaxCertificateId();
            } else {
                String taxCertificateFilePath = null;
                String taxCertificateFileName = null;
                customerTaxCertificate = new CustomerTaxCertificate();
                customerTaxCertificate.setType(type);

                taxCertificateRequest = new CustomerTaxCertificateRequest();
                taxCertificateRequest.setContrato(contractNumber);
                taxCertificateRequest.setPdf("1");
                taxCertificateRequest.setConstanciaTipo(type.toString());
                taxCertificateRequest.setAnoconsulta(year.toString());

                taxCertificateResponse = backendService.getTaxCertificateFile(taxCertificateRequest);

                if (taxCertificateResponse.getRespuesta() != "Error") {
                    // Save downloaded file
                    taxCertificateFilePath = "contracts/" + contractNumber  + "/tax_certificates/";
                    taxCertificateFileName = contractNumber + "_" + year.toString() + "_" + customerTaxCertificate.getTypeAsString() + ".pdf";

                    // Save file and record
                    Path path = Paths.get(taxCertificateFilePath);
                    Files.createDirectories(path);
                    // Decode from Base64 to binary
                    byte[] byteArray = Base64.getDecoder().decode(taxCertificateResponse.getConstancia());
                    // Save the binary file
                    OutputStream outputStream = new FileOutputStream(taxCertificateFilePath + taxCertificateFileName);
                    outputStream.write(byteArray);
                    outputStream.close();

                    // Fill project
                    LocalDateTime now = LocalDateTime.now();
                    customerTaxCertificate.setCustomerContract(currentCustomerContract);
                    customerTaxCertificate.setDownloadedDate(now);
                    customerTaxCertificate.setFilename(taxCertificateFileName);
                    customerTaxCertificate.setYear(year.toString());
                    customerTaxCertificate.setPath(taxCertificateFilePath);

                    customerTaxCertificateRepository.saveAndFlush(customerTaxCertificate);
                    customerTaxCertificateId = customerTaxCertificate.getTaxCertificateId();
                }
            }

        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerTaxCertificateViewPdf: " + e.getLocalizedMessage());
        }

        model.addAttribute("customerTaxCertificateId", customerTaxCertificateId);

        return "customer/tax-certificate-view";
    }

    @GetMapping("/constancia-fiscal/descargarpdf")
    public String customerTaxCertificateDownloadPdf() {
        return "customer/tax-certificate-view";
    }

    @RequestMapping(value = "/constancia-fiscal/descargarxml", produces = "application/xml", method = RequestMethod.GET)
    public void customerTaxCertificateDownloadXml(@RequestParam Integer year, @RequestParam Integer type, 
        HttpServletResponse response,
        Authentication authentication) {

        String contractNumber = null;
        CustomerTaxCertificateRequest taxCertificateRequest = null;
        CustomerTaxCertificateResponse taxCertificateResponse = null;

        CustomerTaxCertificate customerTaxCertificate = null;
        String taxCertificateFilePath = "";
        String taxCertificateFileName = "";

        try {
            
            if (year == null || type == null) {
                throw new Exception("Parametros año y tipo son requeridos");
            } else {
                currentCustomerContract = getCurrentCustomerContract(authentication);
                customer = currentCustomerContract.getCustomer();
                contractNumber = currentCustomerContract.getContractNumber();
                InputStream inputStream = null;

                Optional<CustomerTaxCertificate> result = customerTaxCertificateRepository.findByYearAndTypeAndFileTypeAndCustomerContract(year.toString(), type, CustomerTaxCertificate.FILE_TYPE_XML, currentCustomerContract);

                if (result.isPresent()) {
                    customerTaxCertificate = result.get();
                    File xmlFile = new File(customerTaxCertificate.getPath() + customerTaxCertificate.getFilename());

                    inputStream = new FileInputStream(xmlFile);
                } else {
                    taxCertificateRequest = new CustomerTaxCertificateRequest();
                    taxCertificateRequest.setAnoconsulta(year.toString());
                    taxCertificateRequest.setConstanciaTipo(type.toString());
                    taxCertificateRequest.setPdf("2"); // 1 PDF, 2 XML
                    taxCertificateRequest.setContrato(contractNumber);
        
                    taxCertificateResponse = backendService.getTaxCertificateFile(taxCertificateRequest);
                    
                    if (taxCertificateResponse.getRespuesta() != "Error") {
                        customerTaxCertificate = new CustomerTaxCertificate();
                        customerTaxCertificate.setType(type);
    
                        // Save downloaded file
                        taxCertificateFilePath = "contracts/" + contractNumber  + "/tax_certificates/";
                        taxCertificateFileName = contractNumber + "_" + year.toString() + "_" + customerTaxCertificate.getTypeAsString() + ".xml";
        
                        // Save file and record
                        Path path = Paths.get(taxCertificateFilePath);
                        Files.createDirectories(path);
                        // Decode from Base64 to binary
                        byte[] byteArray = Base64.getDecoder().decode(taxCertificateResponse.getConstancia());
                        // Save the binary file
                        OutputStream outputStream = new FileOutputStream(taxCertificateFilePath + taxCertificateFileName);
                        outputStream.write(byteArray);
                        outputStream.close();
        
                        // Fill Tax Certificate
                        LocalDateTime now = LocalDateTime.now();
                        customerTaxCertificate.setCustomerContract(currentCustomerContract);
                        customerTaxCertificate.setDownloadedDate(now);
                        customerTaxCertificate.setFilename(taxCertificateFileName);
                        customerTaxCertificate.setYear(year.toString());
                        customerTaxCertificate.setPath(taxCertificateFilePath);
        
                        customerTaxCertificateRepository.saveAndFlush(customerTaxCertificate);
        
                        inputStream = new ByteArrayInputStream(byteArray);
                    }
                    
                }

                response.setContentType("application/xml");
                org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
            }

        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage());
        }

        //return "customer/tax-certificate-view";
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
