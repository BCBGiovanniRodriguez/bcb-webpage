package com.bcb.webpage.service.sisbur;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.repository.CustomerContractRepository;
import com.bcb.webpage.model.webpage.repository.CustomerCustomerRepository;
import com.bcb.webpage.service.DatabaseUserDetailsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SisburBackendService {

    @Autowired
    private DatabaseUserDetailsService databaseUserDetailsService;

    @Autowired
    private CustomerCustomerRepository customerRepository;

    @Autowired
    private CustomerContractRepository contractRepository;

    @Value("${sisbur.ms.host}")
    private String host;

    @Value("${sisbur.ms.endpoint}")
    private String endpoint;

    private final String ENDPOINT_CUSTOMER_CONTRACT = "/customer-contract/customer-active";

    SisburBackendService(DatabaseUserDetailsService databaseUserDetailsService) {
        this.databaseUserDetailsService = databaseUserDetailsService;
    }

    private String getBaseEndpoint() {
        return host + endpoint;
    }

    private String getJson(String endpoint, String queryParams) throws JsonProcessingException {
        //String json = mapper.writeValueAsString(objectRequest);
        String uri = getBaseEndpoint() + endpoint;

        if (queryParams != null) {
            uri += queryParams;
        }

        System.out.println("Uri: " + uri);
        String responseString = "";
        RestClient client = RestClient.create();
        responseString = client.get().uri(uri).retrieve().body(String.class);
        /*
        ResponseEntity<String> responseEntity = RestClient.create().get(uri)
            .uri(getBaseEndpoint() + endpoint)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Accept-Encoding", "gzip, deflate, br")
            .header("Connection", "keep-alive")
            .body(json)
            .retrieve()
            .toEntity(String.class);
        
        HttpStatusCode statusCode = responseEntity.getStatusCode();

        if (statusCode.isError() || statusCode.is5xxServerError()) {
            System.out.println("Error on request: ");
        } else if(statusCode.is2xxSuccessful()) { // Successfull but still can be an error on the processing
            
        }
        */

        return responseString;
    }

    public void findNewContracts() {
        List<CustomerContract> activeContracts = contractRepository.findByStatus(1);
        String contractNumbers = "";
        String queryParam = "";
        ObjectMapper mapper = new ObjectMapper();
        //Map<String, Object> 

        try {
            if (activeContracts.size() > 0) {
                for (CustomerContract customerContract : activeContracts) {
                    contractNumbers += customerContract.getContractNumber() + ",";
                }
            }
            //contractNumbers = "?contractNumbers="+contractNumbers;
            queryParam = contractNumbers.length() > 0 ? "?contractNumbers="+ contractNumbers : null;
            String data = this.getJson(this.ENDPOINT_CUSTOMER_CONTRACT, queryParam);
            System.out.println(data);

            mapper.readValue(data, Map.class);
        } catch (Exception e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        }
    }
    
    public void verifyCustomers() {
        // Start process
        
        // Get list of customers

        // Iterate customers

        // Verify customer section

        // Update customer section

        // End process
    }

    

}
