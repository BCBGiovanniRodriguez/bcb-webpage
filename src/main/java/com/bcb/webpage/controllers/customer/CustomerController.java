package com.bcb.webpage.controllers.customer;

import java.io.FileOutputStream;
import java.io.IOException;
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
import com.bcb.webpage.model.webpage.dto.GeneralDTO;
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

    private DateTimeFormatter mexFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private LocalDate today = LocalDate.now();

    private LocalDateTime todayTime = LocalDateTime.now();

    private CustomerContract currentContract;

    private CustomerCustomer customer;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        CustomerCustomer customer = null;
        CustomerContract customerSessionContract = null;
        CustomerContract customerCurrentContract = null;
        String currentSessionDateStr = "N/A";
        String currentSessionTimeStr = "N/A";
        String lastSessionDateStr = "N/A";
        String lastSessionTimeStr = "N/A";
        CustomerDetailResponse customerDetailResponse = null;
        CustomerMovementPositionResponse customerMovementPositionResponse = null;
        Double totalBalanceMN = 0D;
        Double totalBalanceMoneyMarket = 0D;
        Double totalBalanceStockMarket = 0D;
        Posicion positionDetail = null;

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            customerSessionContract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
            customer = customerSessionContract.getCustomer();
            // Find currentContract
            customerCurrentContract = customer.getContracts()
                    .stream()
                    .filter(cc -> cc.isCurrent())
                    .findFirst()
                    .orElseThrow();

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

            System.out.println("Contract: " + customerSessionContract.getContractNumber());
            System.out.println("CurrentSession" + currentSession.getTimestamp().toString());
            System.out.println("LastSession" + lastSession.getTimestamp().toString());

            currentSessionDateStr = WordUtils.capitalizeFully(DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy").format(currentSession.getTimestamp()));
            currentSessionTimeStr = DateTimeFormatter.ofPattern("HH:mm:ss").format(currentSession.getTimestamp());
            lastSessionDateStr = WordUtils.capitalizeFully(DateTimeFormatter.ofPattern("EEEE, MMMM dd yyyy").format(lastSession.getTimestamp()));
            lastSessionTimeStr = DateTimeFormatter.ofPattern("HH:mm:ss").format(lastSession.getTimestamp());

            // TODO User Detail !! Change, already local!!!!
            CustomerDetailRequest customerDetailRequest = new CustomerDetailRequest();
            customerDetailRequest.setContrato(customerCurrentContract.getContractNumber());

            // Get balance detail
            CustomerMovementPositionRequest customerMovementPositionRequest = new CustomerMovementPositionRequest();
            customerMovementPositionRequest.setContrato(customerCurrentContract.getContractNumber());
            customerMovementPositionRequest.setTipo("2");
            
            customerDetailResponse = backendService.customerDetail(customerDetailRequest);

            customerMovementPositionResponse = backendService.customerMovements(customerMovementPositionRequest);
            ArrayList<Posicion> listPositions = customerMovementPositionResponse.getPosicion();
            
            if (listPositions == null) {
                
            } else {
                if (listPositions.size() > 0) {
                    positionDetail = listPositions.getLast();
                    totalBalanceMoneyMarket = Double.parseDouble(positionDetail.getSubtotalDin().replace(",", ""));
                    totalBalanceStockMarket = Double.parseDouble(positionDetail.getSubtotalCap().replace(",", ""));
                }
            }
            
            ArrayList<Saldo> listBalance = customerMovementPositionResponse.getSaldo();
            if (listBalance != null && listBalance.size() > 0) {
                for (Saldo saldo : listBalance) {
                    if (!saldo.getCveDivisa().equals("Por Asignar Indeval")) {
                        totalBalanceMN += Double.parseDouble(saldo.getSubTotal().replace(",", "")); 
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error on CustomerController::dashboard " + e.getLocalizedMessage());
        }

        // Plain Values
        model.addAttribute("customerFullName", customer.getCustomerFullName());
        model.addAttribute("currentSessionDateStr", currentSessionDateStr);
        model.addAttribute("currentSessionTimeStr", currentSessionTimeStr);
        model.addAttribute("lastSessionDateStr", lastSessionDateStr);
        model.addAttribute("lastSessionTimeStr", lastSessionTimeStr);

        model.addAttribute("totalBalanceMN", totalBalanceMN);
        model.addAttribute("totalBalanceMoneyMarket", totalBalanceMoneyMarket);
        model.addAttribute("totalBalanceStockMarket", totalBalanceStockMarket);
        // Object Values
        model.addAttribute("customerDetailResponse", customerDetailResponse);
        model.addAttribute("positionDetail", positionDetail);
        model.addAttribute("customerMovementPositionResponse", customerMovementPositionResponse);


        return "customer/dashboard";
    }
    
    @GetMapping("/detalle")
    public String customerDetail(Authentication authentication, Model model) {
        CustomerCustomer customer = null;
        CustomerContract contract = null;
        CustomerDetailResponse customerDetailResponse = null;
        CustomerDetailRequest customerDetailRequest = null;
        
        List<ListaBeneficiaro> listaBeneficiaros = new ArrayList<>();
        List<ListaCuentum> listaCuenta = new ArrayList<>();

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            contract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
            customer = contract.getCustomer();

            customerDetailRequest = new CustomerDetailRequest();
            customerDetailRequest.setContrato(contract.getContractNumber());
            
            customerDetailResponse = backendService.customerDetail(customerDetailRequest);
            //System.out.println(customerDetailResponse);

            listaBeneficiaros = customerDetailResponse.getBeneficiarios().getListaBeneficiaros();
            listaCuenta = customerDetailResponse.getCuenta().getListaCuenta();
        } catch (Exception e) {
            System.out.println("Error on CustomerController::customerDetail " + e.getLocalizedMessage());
        }

        model.addAttribute("customerDetailResponse", customerDetailResponse);
        model.addAttribute("listaBeneficiaros", listaBeneficiaros);
        model.addAttribute("listaCuenta", listaCuenta);
        
        return "customer/detail";
    }

    @GetMapping("/posicion")
    public String customerPosition(Authentication authentication, Model model) {
        CustomerCustomer customer = null;
        CustomerContract customerSessionContract = null;
        CustomerContract customerCurrentContract = null;

        ObjectMapper mapper = new ObjectMapper();
        List<GeneralDTO> generalList = new ArrayList<>();
        List<Posicion> positionStockList = new ArrayList<Posicion>();
        List<Posicion> positionMoneyList = new ArrayList<Posicion>();
        List<Posicion> positionFundsList = new ArrayList<Posicion>();
        List<Saldo> cashList = new ArrayList<>();

        String generalJson = null;
        String stockMarketJson = null;
        String moneyMarketJson = null;
        String cashJson = null;
        CustomerMovementPositionResponse customerPositionResponse = null;
        CustomerMovementPositionRequest customerPositionRequest = null;
        Double cashTotalBalance = 0D;
        Double grandTotal = 0D;

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            customerSessionContract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
            customer = customerSessionContract.getCustomer();

            customerPositionRequest = new CustomerMovementPositionRequest();
            customerPositionRequest.setContrato(customerSessionContract.getContractNumber());
            customerPositionRequest.setTipo("2"); // 1: Movimientos - 2: - Posición

            customerPositionResponse = backendService.customerPosition(customerPositionRequest);
            grandTotal = Double.parseDouble(customerPositionResponse.getTotal().replace(",", ""));

            List<Posicion> positionList = customerPositionResponse.getPosicion();
            for (Posicion posicion : positionList) {
                if (posicion.isStockMarket()) positionStockList.add(posicion);
                if (posicion.isMoneyMarket()) positionMoneyList.add(posicion);
                if (posicion.isFundsMarket()) positionFundsList.add(posicion);
            }
            

            List<Saldo> balanceList = customerPositionResponse.getSaldo();

            for(int cont = 0; cont < 2; cont++) {
                Saldo saldoTmp = balanceList.get(cont);
                cashTotalBalance += Double.parseDouble(saldoTmp.getSaldoActual().replace(",", ""));
                cashList.add(saldoTmp);
            }

            stockMarketJson = mapper.writeValueAsString(positionStockList);
            moneyMarketJson = mapper.writeValueAsString(positionMoneyList);
            cashJson = mapper.writeValueAsString(cashList);
            // ===================================================
            Double cashPercentage = (cashTotalBalance * 100) / grandTotal;
            GeneralDTO cashGeneralDTO = new GeneralDTO();
            

            //System.out.println("GrandTotal: " + grandTotal.toString());
            //System.out.println("CashTotalBalance" + cashTotalBalance.toString());

            cashGeneralDTO.setMarket("Efectivo");
            cashGeneralDTO.setEmmiter("");
            cashGeneralDTO.setSerie("");
            cashGeneralDTO.setSecurities("");
            cashGeneralDTO.setAverageAmount(0D);
            cashGeneralDTO.setPrice(0D);
            cashGeneralDTO.setValue(cashTotalBalance);
            cashGeneralDTO.setPercentage(cashPercentage);
            cashGeneralDTO.setCapitalGainLoss(0D);
            generalList.add(cashGeneralDTO);

            for (Posicion posicionTmp : positionStockList) {
                GeneralDTO stockGeneralDto = new GeneralDTO();
                stockGeneralDto.setMarket("Capitales");
                stockGeneralDto.setEmmiter(posicionTmp.getEmisora());
                stockGeneralDto.setSerie(posicionTmp.getSerie());
                stockGeneralDto.setSecurities(posicionTmp.getTitulos());
                Double averageAmount = Double.parseDouble(posicionTmp.getCostoPromedio().replace(",", ""));
                Double price = Double.parseDouble(posicionTmp.getCostoXTitulos().replace(",", ""));
                Double value = Double.parseDouble(posicionTmp.getValorMercado().replace(",", ""));
                Double capitalGainLoss = Double.parseDouble(posicionTmp.getPlusMinusvalia().replace(",", ""));
                
                stockGeneralDto.setAverageAmount(averageAmount);
                stockGeneralDto.setPrice(price);
                stockGeneralDto.setValue(value);

                Double percentage = (100 * value) / grandTotal;
                //System.out.println("GrandTotal: " + grandTotal.toString());
                //System.out.println("PositionValue" + value.toString());
                //System.out.println("Percentage" + percentage.toString());
                String cssStyle = "";
                if (capitalGainLoss > 0D) {
                    cssStyle = "text-success";
                } else if(capitalGainLoss < 0D) {
                    cssStyle = "text-danger";
                }

                stockGeneralDto.setPercentage(percentage);
                stockGeneralDto.setCapitalGainLoss(capitalGainLoss);
                stockGeneralDto.setCssStyle(cssStyle);
                generalList.add(stockGeneralDto);
            }

            for (Posicion posicionTmp : positionMoneyList) {
                GeneralDTO moneyGeneralDto = new GeneralDTO();
                moneyGeneralDto.setMarket("Dinero");
                moneyGeneralDto.setEmmiter(posicionTmp.getEmisora());
                moneyGeneralDto.setSerie(posicionTmp.getSerie());
                moneyGeneralDto.setSecurities(posicionTmp.getTitulos());
                Double averageAmount = Double.parseDouble(posicionTmp.getCostoPromedio().replace(",", ""));
                Double price = Double.parseDouble(posicionTmp.getCostoXTitulos().replace(",", ""));
                Double value = Double.parseDouble(posicionTmp.getValorMercado().replace(",", ""));
                Double capitalGainLoss = Double.parseDouble(posicionTmp.getPlusMinusvalia().replace(",", ""));
                
                moneyGeneralDto.setAverageAmount(averageAmount);
                moneyGeneralDto.setPrice(price);
                moneyGeneralDto.setValue(value);

                Double percentage = (100 * value) / grandTotal;
                //System.out.println("GrandTotal: " + grandTotal.toString());
                //System.out.println("PositionValue" + value.toString());
                //System.out.println("Percentage" + percentage.toString());

                moneyGeneralDto.setPercentage(percentage);
                moneyGeneralDto.setCapitalGainLoss(capitalGainLoss);
                String cssStyle = "";
                if (capitalGainLoss > 0D) {
                    cssStyle = "text-success";
                } else if(capitalGainLoss < 0D) {
                    cssStyle = "text-danger";
                }
                moneyGeneralDto.setCssStyle(cssStyle);
                generalList.add(moneyGeneralDto);
            }

            generalJson = mapper.writeValueAsString(generalList);

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

        model.addAttribute("generalJson", generalJson);
        model.addAttribute("stockMarketJson", stockMarketJson);
        model.addAttribute("moneyMarketJson", moneyMarketJson);
        model.addAttribute("cashJson", cashJson);

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

        List<Movimiento> movementsList = new ArrayList<Movimiento>();
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
        @RequestParam(name = "startDate", required = true) LocalDate startDate, 
        @RequestParam(name = "endDate", required = true) LocalDate endDate, 
        @RequestBody String entity,
        Authentication authentication,
        Model model) {
        CustomerCustomer customer = null;
        CustomerContract contract = null;

        //System.out.println(startDate.toString());
        //System.out.println(endDate.toString());

        List<Movimiento> movementsList = new ArrayList<Movimiento>();
        CustomerMovementPositionResponse movementResponse = null;
        CustomerMovementPositionRequest movementRequest = null;

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            contract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
            customer = contract.getCustomer();

            movementRequest = new CustomerMovementPositionRequest();
            movementRequest.setContrato(contract.getContractNumber());
            movementRequest.setTipo("1"); // Movements
            
            movementRequest.setFechaIni(mexFormatter.format(startDate));
            movementRequest.setFechaFin(mexFormatter.format(endDate));

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
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            //CustomerContract contract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();

            statementAccountRequest = new CustomerStatementAccountRequest();
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
        //model.addAttribute("lastStatementAccounts", lastStatementAccounts);redd
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
        CustomerTaxCertificateDetailResponse taxCertificateDetailResponse = null;
        List<TaxCertificate> taxCertificateList = new ArrayList<>();
        Set<String> yearSet = new TreeSet<>();
        String taxCertificatesJson = null;
        ObjectMapper mapper = new ObjectMapper();

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            CustomerTaxCertificateRequest taxCertificateRequest = new CustomerTaxCertificateRequest();
            taxCertificateRequest.setContrato(userDetails.getUsername());
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
        // Si no existe el archivo previamente, se descarga
        // Se guarda el detalle de la descarga en la base de datos
        // Se guarda registro de la constancia fiscal
        String contractNumber = null;
        CustomerContract customerContract = null;
        CustomerTaxCertificate customerTaxCertificate = null;
        Long customerTaxCertificateId = null;
        System.out.println("Anio:" + year);
        System.out.println("Tipo:" + type);


        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            contractNumber = userDetails.getUsername();
            customerContract = contractRepository.findOneByContractNumber(contractNumber).get();

            Optional<CustomerTaxCertificate> result = customerTaxCertificateRepository.findByYearAndTypeAndCustomerContract(year.toString(), type.toString(), customerContract);

            if (result.isPresent()) { // Already downloaded, get and send taxCertificateId
                customerTaxCertificate = result.get();
                customerTaxCertificateId = customerTaxCertificate.getTaxCertificateId();
            } else {
                String taxCertificateFilePath = null;
                String taxCertificateFileName = null;
                customerTaxCertificate = new CustomerTaxCertificate();
                customerTaxCertificate.setType(type.toString());

                CustomerTaxCertificateRequest taxCertificateRequest = new CustomerTaxCertificateRequest();
                CustomerTaxCertificateResponse taxCertificateResponse = null;
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
                    customerTaxCertificate.setCustomerContract(customerContract);
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

    @GetMapping("/constancia-fiscal/descargarxml")
    public String customerTaxCertificateDownloadXml() {
        return "customer/tax-certificate-view";
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




    @PostMapping("/constancia-fiscal")
    public String customerTaxCertificatesSubmit(@RequestBody String year, Authentication authentication, Model model) {
        
        try {

            
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            CustomerTaxCertificateRequest taxCertificateRequest = new CustomerTaxCertificateRequest();
            taxCertificateRequest.setContrato(userDetails.getUsername());
            taxCertificateRequest.setAnoconsulta(year);
            taxCertificateRequest.setPdf("1");
            taxCertificateRequest.setConstanciaTipo("1");

            CustomerTaxCertificatePeriodResponse taxCertificatePeriodResponse;


        } catch (Exception e) {
            System.out.println("Error on: customerTaxCertificatesSubmit " + e.getLocalizedMessage());
        }

        model.addAttribute("year", year);
        
        return "customer/tax-certificates";
    }
    
    @GetMapping("/cambiar-contrato/{contractNumber}")
    public String getChangeContract(@PathVariable String contractNumber, Authentication authentication) {
        // Verify if contract number in customer contracts
        if (contractNumber != null) {
            try {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                CustomerContract customerContract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();

                CustomerContract customerContractTarget = customerContract.getCustomer().getContracts()
                    .stream()
                    .filter(cc -> cc.getContractNumber() == contractNumber)
                    .findAny()
                    .orElseThrow();

                customerContractTarget.setCurrent(CustomerContract.CURRENT_TRUE);
                contractRepository.saveAndFlush(customerContractTarget);
                
                for (CustomerContract customerContractTmp : customerContract.getCustomer().getContracts()) {
                    if (customerContractTarget.getCustomerContractId() != customerContractTmp.getCustomerContractId()) {
                        customerContractTmp.setCurrent(CustomerContract.CURRENT_FALSE);
                        contractRepository.saveAndFlush(customerContractTmp);
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
            //
            CustomerContract contract = contractRepository.findOneByContractNumber(userDetails.getUsername()).get();
            
            if (contract.isCurrent()) {
                currentCustomerContract = contract;
            } else {
                for (CustomerContract customerContract : contract.getCustomer().getContracts()) {
                    if(customerContract.isCurrent()) {
                        currentCustomerContract = customerContract;
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return currentCustomerContract;
    }
}
