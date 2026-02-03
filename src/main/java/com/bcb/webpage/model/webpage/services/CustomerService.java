package com.bcb.webpage.model.webpage.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bcb.webpage.model.webpage.entity.SisburEmmission;
import com.bcb.webpage.model.webpage.entity.SisburEmmissionPrice;
import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.webpage.repository.CustomerContractRepository;
import com.bcb.webpage.model.webpage.repository.CustomerCustomerRepository;
import com.bcb.webpage.model.webpage.repository.SisburEmmissionPriceRepository;
import com.bcb.webpage.model.webpage.repository.SisburEmmissionRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerCustomerRepository customerRepository;

    @Autowired
    private CustomerContractRepository contractRepository;
    
    @Autowired
    private SisburEmmissionRepository sisburEmmissionRepository;

    @Autowired
    private SisburEmmissionPriceRepository sisburEmmissionPriceRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private CustomerCustomer customer;

    private DateTimeFormatter mexFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private List<Map<String, Object>> townshipList;
    
    private List<Map<String, Object>> cityList;
    
    private List<Map<String, Object>> stateList;

    private List<Map<String, Object>> countryList;

    List<Map<String, Object>> profileList;

    public void setProfileList(List<Map<String, Object>> profileList) {
        this.profileList = profileList;
    }

    public void setTownshipList(List<Map<String, Object>> townshipList) {
        this.townshipList = townshipList;
    }

    public void setCityList(List<Map<String, Object>> cityList) {
        this.cityList = cityList;
    }

    public void setStateList(List<Map<String, Object>> stateList) {
        this.stateList = stateList;
    }

    public void setCountryList(List<Map<String, Object>> countryList) {
        this.countryList = countryList;
    }

    public void setCustomerRepository(CustomerCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void setCustomer(CustomerCustomer customer) {
        this.customer = customer;
    }

    public List<CustomerContract> getCustomersContracts() {
        List<CustomerContract> contractList = null;

        try {
            contractList = contractRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return contractList;
    }

    public List<String> getAllCustomerContractNumberList() {
        List<String> customerContractNumberList = new ArrayList<>();

        try {
            customerContractNumberList = contractRepository.findAllContractNumbers();
        } catch (Exception e) {
            
        }

        return customerContractNumberList;
    }

    public List<CustomerCustomer> getCustomers() {
        List<CustomerCustomer> customerList = new ArrayList<>();

        try {
            customerList = customerRepository.findAll();
        } catch (Exception e) {
            System.out.println("Error on CustomerService::getCustomers " + e.getLocalizedMessage());
        }

        return customerList;
    }

    public void saveCustomers(List<Map<String, Object>> customerList) {

        try {
            Integer migratedContracts = 0;
            Integer migratedCustomers = 0;
            
            if (townshipList == null || cityList == null || stateList == null || countryList == null) {
                throw new Exception("CustomerService::saveCustomers[Una o mas fuentes de datos no se han configurado.]");
            }

            if (customerList != null && !customerList.isEmpty()) {
                Integer initial;
                String customerKey;
                CustomerCustomer customer;
                Map<String, Object> profileMap;
                String fullAddress;
                String passStr;
                Integer service = 0;
                LocalDateTime now;
                Optional<CustomerCustomer> result;
                passStr = "test"; // Default password for testing purposes
    
                for (Map<String, Object> item : customerList) {
                    now = LocalDateTime.now();
                    // Search for CVECLIENTE if not exist save, if exists get
                    customerKey = item.get("CVECLIENTE").toString();
                    initial = Integer.parseInt(item.get("INICIAL").toString());
                    result = customerRepository.findOneByCustomerKey(customerKey);
                    if (result.isPresent()) {
                        customer = result.get();
                    } else {
                        customer = new CustomerCustomer();
                        customer.setCustomerKey(customerKey);
                        customer.setName(item.get("NOMCLIENTE").toString());
                        customer.setLastName(item.get("APEPATCLIENTE").toString());
                        customer.setSecondLastName(item.get("APEMATCLIENTE").toString());
                        customer.setPhoneNumber(item.get("TELCEL").toString());
                        customer.setHomePhoneNumber(item.get("TELDOM").toString());
                        customer.setWorkPhoneNumber(item.get("TELOFI").toString());
                        customer.setEmail(item.get("EMAIL").toString());
                        //customer.setInitial(Integer.parseInt(item.get("INICIAL").toString()));
                        //customer.setLocked(Integer.parseInt(item.get("BLOQUEADO").toString()));
                        customer.setCreated(now);
                        customer.setRfc(item.get("RFC").toString());
                        customer.setCurp(item.get("CURP").toString());
    
                        fullAddress = getAddress(item);
                        profileMap = this.profileList.stream()
                            .filter(p -> p.get("CVEPERFIL").toString().equals(item.get("PERFIL").toString()))
                            .findFirst()
                            .orElse(null);
    
                        customer.setFullAddress(fullAddress);
                        if (profileMap != null) {
                            customer.setProfile(profileMap.get("NOMPERFIL").toString());
                        } else {
                            customer.setProfile("INDEFINIDO");
                        }
                        
                        if (Integer.parseInt(item.get("SRVASE").toString()) == 0) {
                            service = CustomerCustomer.SERVICE_ADVICE;
    
                            if (Integer.parseInt(item.get("SOFISTICADO").toString()) == 1) {
                                service = CustomerCustomer.SERVICE_SPECIALICED_ADVICE;
                            }
                        } else if (Integer.parseInt(item.get("SRVASE").toString()) ==  1) {
                            service = CustomerCustomer.SERVICE_MANAGEMENT;
                        }
                        
                        if (Integer.parseInt(item.get("EJECUCION").toString()) == 1) {
                            service = CustomerCustomer.SERVICE_EXECUTION;
                        }
    
                        customer.setService(service);
                        
                        //System.out.println("Ejecucion: " + item.get("EJECUCION").toString());
                        //System.out.println("SRVASE: " + item.get("SRVASE").toString() );
                        //System.out.println("SOFISTICADO: " + item.get("SOFISTICADO").toString() );
                        //System.out.println("Servicio: " + customer.getService());
                        //System.out.println("ServicioAsString: " + customer.getServiceAsString());
                        
                        customerRepository.saveAndFlush(customer);
                        //System.out.println("Cliente #" + customer.getCustomerKey() + " agregado");
                        service = 0;
                        migratedCustomers++;
                    }
                    
                    passStr = item.get("PASSWORD").toString(); // Uncomment for production use
                    CustomerContract contract = new CustomerContract();
                    contract.setCustomer(customer);
                    contract.setContractNumber(item.get("CONTRATO").toString());
                    if (initial == 0) { // Only existing contracts will have the password from legacy system
                        contract.setPassword(encoder.encode(passStr));
                    }
                    contract.setStatus(1);
                    contract.setCreated(now);
                    contract.setInitial(initial);
                    contract.setLocked(Integer.parseInt(item.get("BLOQUEADO").toString()));
    
                    contractRepository.saveAndFlush(contract);
                    //System.out.println("Contrato #" + contract.getContractNumber() + " agregado");
                    
                    //passStr = null; // Clear password string
                    migratedContracts++;
                }
            }
            
            System.out.println("CustomerService::saveCustomers[Proceso Terminado [ClientesImportados: " + migratedCustomers + ", ContratosImportados: " + migratedContracts + "]]");
        } catch (Exception e) {
            System.out.println("Error on CustomerService::saveCustomers " + e.getLocalizedMessage());
        }

    }
    
    private String getAddress(Map<String, Object> customerRawData) {
        String fullAddress = "";
        String townshipId = customerRawData.get("DELMUNDOM").toString();
        String cityId = customerRawData.get("CIUDADDOM").toString();
        String stateId = customerRawData.get("ESTADODOM").toString();
        String countryId = customerRawData.get("PAISDOM").toString();

        fullAddress += customerRawData.get("CALLEDOM").toString() + ", " + customerRawData.get("NUMEXTERIORDOM").toString() + ", " + customerRawData.get("NUMINTERIORDOM").toString() + ", ";
        fullAddress += customerRawData.get("COLONIADOM").toString() + ", " + customerRawData.get("CPOSTALDOM").toString() + ", ";

        if (!this.townshipList.isEmpty()) {
            Map<String, Object> township = townshipList.stream()
                .filter(t -> t.get("IDMUNICIPIO").toString().equals(townshipId))
                .findFirst()
                .orElse(null);
            
            fullAddress += township.get("NOMMUNICIPIO").toString() + ", ";
        }

        if(!this.cityList.isEmpty()) {
            Map<String, Object> city = cityList.stream()
                .filter(c -> c.get("IDCIUDAD").toString().equals(cityId))
                .findFirst()
                .orElse(null);
            
            fullAddress += city.get("NOMCIUDAD").toString() + ", ";
        }

        if (!this.stateList.isEmpty()) {
            Map<String, Object> state = stateList.stream()
                .filter(s -> s.get("IDESTADO").toString().equals(stateId))
                .findFirst()
                .orElse(null);

            fullAddress += state.get("NOMESTADO").toString() + ", ";
        }

        if (!this.countryList.isEmpty()) {
            Map<String, Object> country = countryList.stream()
                .filter(c -> c.get("IDPAIS").toString().equals(countryId))
                .findFirst()
                .orElse(null);

            fullAddress += country.get("NOMPAIS").toString();
        }

        return fullAddress;
    }

    public void registerStockMarketPosition(List<Map<String, Object>> stockMarketCustomerCurrentPositions) {
        if (!stockMarketCustomerCurrentPositions.isEmpty()) {
            Optional<CustomerContract> optionalCustomerContract;
            CustomerContract contract = null;

            String valueType = null;
            String emmiter = null;
            String serie = null;
            Optional<SisburEmmission> sisburEmmissionOptional;

            SisburEmmission sisburEmmission;
            SisburEmmissionPrice sisburEmmisionPrice;
            SisburEmmissionPrice sisburEmmisionPriceLast = null;
            Integer unregisteredContracts = 0;
            Integer totalUpdatedPrices = 0;
            
            for (Map<String,Object> stockMarketPosition : stockMarketCustomerCurrentPositions) {
                optionalCustomerContract = contractRepository.findOneByContractNumber(stockMarketPosition.get("CONTRATO").toString());
                if (optionalCustomerContract.isPresent()) { // Solo los contratos que existan en la página web
                    contract = optionalCustomerContract.get();

                    valueType = stockMarketPosition.get("TIPOVALOR").toString();
                    emmiter = stockMarketPosition.get("EMISORA").toString();
                    serie = stockMarketPosition.get("SERIE").toString();

                    sisburEmmissionOptional = sisburEmmissionRepository.findOneByValueTypeAndEmmiterAndSerie(valueType, emmiter, serie);

                    if (sisburEmmissionOptional.isPresent()) {
                        sisburEmmission = sisburEmmissionOptional.get();
                    } else {
                        sisburEmmission = new SisburEmmission();
                        sisburEmmission.setValueType(valueType);
                        sisburEmmission.setEmmiter(emmiter);
                        sisburEmmission.setSerie(serie);
                        sisburEmmission.setCreated(LocalDateTime.now());
                        
                        sisburEmmissionRepository.saveAndFlush(sisburEmmission);
                    }

                    //System.out.println("Precios registrados en la emisión: " + sisburEmmission.getEmmisionPrices().size());
                    
                    sisburEmmisionPrice = createSisburEmmisionPrice(stockMarketPosition);
                    sisburEmmisionPrice.setEmmissionId(sisburEmmission);

                    if (sisburEmmission.getEmmisionPrices().size() > 1) {
                        sisburEmmisionPriceLast = sisburEmmission.getEmmisionPrices().getLast();
                    } else {
                        sisburEmmissionPriceRepository.saveAndFlush(sisburEmmisionPrice);
                        continue;
                    }

                    sisburEmmission.getEmmisionPrices().add(sisburEmmisionPrice);

                    if (sisburEmmisionPriceLast != null) {
                        if (!sisburEmmisionPriceLast.equals(sisburEmmisionPrice)) {
                            totalUpdatedPrices++;
                            //System.out.println("Actualizacion de precio de emision: TV[" + valueType + "] Emisora[" + emmiter + "] Serie[" + serie + "]");
    
                            sisburEmmissionPriceRepository.saveAndFlush(sisburEmmisionPrice);
                        }
                    }
                } else {
                    unregisteredContracts++;
                    //System.out.println("Contrato: " + stockMarketPosition.get("CONTRATO").toString() + " no registrado en la Página Web ");
                }

                sisburEmmisionPriceLast = null;
		    }

            //System.out.println("Total Contratos no registrados: " + unregisteredContracts);
            //System.out.println("Total de actualizaciones de precio: " + totalUpdatedPrices);
        }
    }

    private SisburEmmissionPrice createSisburEmmisionPrice(Map<String, Object> rawData) {
        SisburEmmissionPrice sisburEmmisionPrice = new SisburEmmissionPrice();

        if (rawData.size() > 0) {
            sisburEmmisionPrice.setCleanPrice(Double.parseDouble(rawData.get("PRECIOLIMPIO").toString()));
            sisburEmmisionPrice.setDirtyPrice(Double.parseDouble(rawData.get("PRECIOSUCIO").toString()));
            sisburEmmisionPrice.setCleanPrice24(Double.parseDouble(rawData.get("PRECIOLIMPIO24").toString()));
            sisburEmmisionPrice.setDirtyPrice24(Double.parseDouble(rawData.get("PRECIOSUCIO24").toString()));
            sisburEmmisionPrice.setCreated(LocalDateTime.now());
        }

        return sisburEmmisionPrice;
    }

    public void bulkStockMarketPrices() {
        LocalDateTime today = LocalDateTime.now();
        List<SisburEmmission> sisburEmmissionList;
        List<SisburEmmissionPrice> sisburEmmissionPriceList;
        
        try {
            sisburEmmissionList = sisburEmmissionRepository.findAll();
            
            for (SisburEmmission sisburEmmission : sisburEmmissionList) {
                sisburEmmissionPriceList = sisburEmmission.getEmmisionPrices();

                
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
}
