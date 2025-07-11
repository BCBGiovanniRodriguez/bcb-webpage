package com.bcb.webpage.controllers.customer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.net.http.HttpHeaders;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat.Resource;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bcb.webpage.dto.request.CustomerDetailRequest;
import com.bcb.webpage.dto.request.CustomerMovementPositionRequest;
import com.bcb.webpage.dto.request.CustomerStatementAccountRequest;
import com.bcb.webpage.dto.request.Login;
import com.bcb.webpage.dto.response.LoginResponse;
import com.bcb.webpage.dto.response.customer.CustomerDetailResponse;
import com.bcb.webpage.dto.response.position.CustomerMovementPositionResponse;
import com.bcb.webpage.dto.response.position.Movimiento;
import com.bcb.webpage.dto.response.position.Posicion;
import com.bcb.webpage.dto.response.statement.CustomerStatementAccountResponse;
import com.bcb.webpage.dto.response.statement.CustomerStatementFileResponse;
import com.bcb.webpage.dto.response.statement.StatementAccount;
import com.bcb.webpage.model.backend.entity.CustomerData;
import com.bcb.webpage.model.backend.entity.CustomerSession;
import com.bcb.webpage.model.backend.entity.customers.CustomerContract;
import com.bcb.webpage.model.backend.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.backend.entity.customers.CustomerStatementAccount;
import com.bcb.webpage.model.backend.repository.ContractRepository;
import com.bcb.webpage.model.backend.repository.CustomerDataRepository;
import com.bcb.webpage.model.backend.repository.CustomerRepository;
import com.bcb.webpage.model.backend.repository.CustomerSessionRepository;
import com.bcb.webpage.model.backend.repository.CustomerStatementAccountRepository;
import com.bcb.webpage.model.backend.services.BackendService;
import com.bcb.webpage.model.backend.services.CustomerService;
import com.bcb.webpage.model.sisbur.service.MovementService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/portal-clientes")
public class CustomerController {

    private final Integer ENVIRONTMENT_MODE = 0;

    private final Integer ENVIRONTMENT_MODE_DEVELOPMENT = 0;

    private final Integer ENVIRONTMENT_MODE_TESTING = 1;

    private final Integer ENVIRONTMENT_MODE_PRODUCTION = 2;

    private final Integer ENVIRONTMENT_MODE_MAINTENANCE = 3;

    @Autowired
    private BackendService backendService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerDataRepository customerDataRepository;

    @Autowired
    private CustomerSessionRepository customerSessionRepository;

    @Autowired
    private CustomerStatementAccountRepository customerStatementAccountRepository;

    private DateTimeFormatter mexFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private LocalDate today = LocalDate.now();

    private LocalDateTime todayTime = LocalDateTime.now();

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String currentSessionDateStr = "N/A";
        String currentSessionTimeStr = "N/A";
        String lastSessionDateStr = "N/A";
        String lastSessionTimeStr = "N/A";

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            CustomerContract contract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
            List<CustomerSession> sessionList = contract.getCustomer().getSessions();
            CustomerSession currentSession = sessionList.stream()
                .filter(cs -> cs.isCurrent())
                .findFirst()
                .orElse(null);
            
            CustomerSession lastSession = sessionList.stream()
                .filter(cs -> !cs.isCurrent() && (cs.getSessionId() == currentSession.getSessionId() - 1))
                .findAny()
                .orElse(null);

            System.out.println("Contract: " + contract.getContractNumber());
            System.out.println("CurrentSession" + currentSession.getTimestamp().toString());
            System.out.println("LastSession" + lastSession.getTimestamp().toString());

            String contractNumber = contract.getContractNumber();
            CustomerDetailResponse customerDetailResponse = null;
            CustomerDetailRequest customerDetailRequest = new CustomerDetailRequest();
            customerDetailRequest.setContrato(contractNumber);

            CustomerMovementPositionResponse customerMovementPositionResponse = null;
            CustomerMovementPositionRequest customerMovementPositionRequest = new CustomerMovementPositionRequest();
            customerMovementPositionRequest.setContrato(contractNumber);
            customerMovementPositionRequest.setTipo("2");

            currentSessionDateStr = WordUtils.capitalizeFully(DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy").format(currentSession.getTimestamp()));
            currentSessionTimeStr = DateTimeFormatter.ofPattern("HH:mm:ss").format(currentSession.getTimestamp());
            lastSessionDateStr = WordUtils.capitalizeFully(DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy").format(lastSession.getTimestamp()));
            lastSessionTimeStr = DateTimeFormatter.ofPattern("HH:mm:ss").format(lastSession.getTimestamp());
            
            customerDetailResponse = backendService.customerDetail(customerDetailRequest);
            //System.out.println("CustomerDetail: " + customerDetailResponse.toString());

            Posicion positionDetail = new Posicion();
            customerMovementPositionResponse = backendService.customerMovements(customerMovementPositionRequest);
            //System.out.println("CustomerPosition: " + customerMovementPositionResponse.toString());

            ArrayList<Posicion> listPositions = customerMovementPositionResponse.getPosicion();
            if (listPositions != null && listPositions.size() > 0) {
                positionDetail = listPositions.getLast();
            }
            
            model.addAttribute("customerDetailResponse", customerDetailResponse);
            model.addAttribute("positionDetail", positionDetail);
            model.addAttribute("customerMovementPositionResponse", customerMovementPositionResponse);
        } catch (Exception e) {
            System.out.println("Error on CustomerController::dashboard " + e.getLocalizedMessage());
        }

        model.addAttribute("currentSessionDateStr", currentSessionDateStr);
        model.addAttribute("currentSessionTimeStr", currentSessionTimeStr);
        model.addAttribute("lastSessionDateStr", lastSessionDateStr);
        model.addAttribute("lastSessionTimeStr", lastSessionTimeStr);

        return "customer/dashboard";
    }
    
    @GetMapping("/detalle")
    public String customerDetail(Model model) {
        CustomerDetailResponse customerDetailResponse = null;
        CustomerDetailRequest customerDetailRequest = new CustomerDetailRequest();
        customerDetailRequest.setContrato("101272");

        try {
            customerDetailResponse = backendService.customerDetail(customerDetailRequest);
        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerDetail " + e.getLocalizedMessage());
        }

        model.addAttribute("customerDetailResponse", customerDetailResponse);
        
        return "customer/detail";
    }

    @GetMapping("/posicion")
    public String customerPosition(Model model) {
        ArrayList<Posicion> positionStockList = new ArrayList<Posicion>();
        ArrayList<Posicion> positionMoneyList = new ArrayList<Posicion>();
        ArrayList<Posicion> positionFundsList = new ArrayList<Posicion>();

        CustomerMovementPositionResponse customerPositionResponse = null;
        CustomerMovementPositionRequest customerPositionRequest = new CustomerMovementPositionRequest();
        customerPositionRequest.setContrato("101272");
        // 1: Movimientos - 2: - Posición
        customerPositionRequest.setTipo("2");
        //movementPositionRequest.setFechaIni(null);
        //movementPositionRequest.setFechaFin(null);

        try {
            customerPositionResponse = backendService.customerPosition(customerPositionRequest);

            ArrayList<Posicion> positionList = customerPositionResponse.getPosicion();
            for (Posicion posicion : positionList) {
                if (posicion.isStockMarket()) positionStockList.add(posicion);
                if (posicion.isMoneyMarket()) positionMoneyList.add(posicion);
                if (posicion.isFundsMarket()) positionFundsList.add(posicion);
            }

            

        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerPosition " + e.getLocalizedMessage());
        }

        model.addAttribute("today", today);
        model.addAttribute("positionStockList", positionStockList);
        model.addAttribute("positionMoneyList", positionMoneyList);
        model.addAttribute("positionFundsList", positionFundsList);
        model.addAttribute("customerPositionResponse", customerPositionResponse);

        // Obtener # de contrato
        // Obtener # de cliente
        // Obtener posición del contrato

        return "customer/position";
    }

    @GetMapping("/movimientos")
    public String customerMovements(
        @RequestParam(name = "startDate", required = false) LocalDate startDate, 
        @RequestParam(name = "endDate", required = false) LocalDate endDate, 
        Model model) {

        ArrayList<Movimiento> movementsList = new ArrayList<Movimiento>();
        /*
        String contractNumber = "101272";
        CustomerMovementPositionResponse movementResponse = null;
        CustomerMovementPositionRequest movementRequest = new CustomerMovementPositionRequest();
        movementRequest.setContrato(contractNumber);
        movementRequest.setTipo("1"); // Movements

        String startDateStr = "01/06/2025";
        String endDateStr = "15/06/2025";
        movementRequest.setFechaIni(startDateStr);
        movementRequest.setFechaFin(endDateStr);

        try {
            movementResponse = backendService.customerMovements(movementRequest);
            movementsList = movementResponse.movimiento;
        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerMovements " + e.getLocalizedMessage());
        }
        */
        model.addAttribute("today", today);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("movementsList", movementsList);

        return "customer/movements";
    }

    @PostMapping("/movimientos")
    public String customerMovementsSubmit(
        @RequestParam(name = "startDate", required = false) LocalDate startDate, 
        @RequestParam(name = "endDate", required = false) LocalDate endDate, 
        @RequestBody String entity,
        Model model) {

        System.out.println(startDate.toString());
        System.out.println(endDate.toString());

        ArrayList<Movimiento> movementsList = new ArrayList<Movimiento>();
        String contractNumber = "101272";
        CustomerMovementPositionResponse movementResponse = null;
        CustomerMovementPositionRequest movementRequest = new CustomerMovementPositionRequest();
        movementRequest.setContrato(contractNumber);
        movementRequest.setTipo("1"); // Movements
        
        movementRequest.setFechaIni(mexFormatter.format(startDate));
        movementRequest.setFechaFin(mexFormatter.format(endDate));

        try {
            movementResponse = backendService.customerMovements(movementRequest);
            movementsList = movementResponse.movimiento;
        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerMovements " + e.getLocalizedMessage());
        }
            
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("movementsList", movementsList);

        return "customer/movements";
    }
    

    @GetMapping("/resultados")
    public String customerResults() {
        return "customer/results";
    }

    @GetMapping("/estados-cuenta")
    public String customerStatements(Authentication authentication, Model model) {
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
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            //CustomerContract contract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();

            CustomerStatementAccountRequest statementAccountRequest = new CustomerStatementAccountRequest();
            statementAccountRequest.setContrato(userDetails.getUsername());

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

        //model.addAttribute("statementAccountList", statementAccountList);
        model.addAttribute("currentStatementAccount", currentStatementAccount);
        model.addAttribute("last12StatementAccounts", last12StatementAccounts);
        model.addAttribute("statementYearsSet", statementYearList);
        //model.addAttribute("lastStatementAccounts", lastStatementAccounts);
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

        model.addAttribute("statementAccountId", statementAccountId);

        return "customer/statement-search";
    }
    

    @GetMapping("/estados-cuenta/descargar/{number}")
    public String customerStatementsDownload(@PathVariable Integer number, Authentication authentication, Model model) {

        return "customer/statement-download";
    }



    @GetMapping("/constancia-fiscal")
    public String customerTaxCertificates() {
        return "customer/tax-certificates";
    }

    

    


}
