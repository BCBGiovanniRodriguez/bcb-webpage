package com.bcb.webpage.model.webpage.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.bcb.webpage.dto.request.CustomerDetailRequest;
import com.bcb.webpage.dto.request.CustomerMovementPositionRequest;
import com.bcb.webpage.dto.request.CustomerStatementAccountRequest;
import com.bcb.webpage.dto.request.CustomerTaxCertificateRequest;
import com.bcb.webpage.dto.request.Login;
import com.bcb.webpage.dto.response.LoginResponse;
import com.bcb.webpage.dto.response.customer.CustomerDetailResponse;
import com.bcb.webpage.dto.response.position.CustomerMovementPositionResponse;
import com.bcb.webpage.dto.response.statement.CustomerStatementAccountResponse;
import com.bcb.webpage.dto.response.statement.CustomerStatementFileResponse;
import com.bcb.webpage.dto.response.taxcertificate.CustomerTaxCertificateDetailResponse;
import com.bcb.webpage.dto.response.taxcertificate.CustomerTaxCertificateResponse;
import com.bcb.webpage.model.webpage.entity.CustomerData;
import com.bcb.webpage.model.webpage.repository.CustomerDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BackendService {

    @Autowired
    private CustomerDataRepository customerDataRepository;

    @Value("${sisbur.backend.host}")
    private String host;

    @Value("${sisbur.backend.endpoint}")
    private String endpoint;
    
    private RestClient restClient = RestClient.create();

    private final String ENDPOINT_LOGING = "/login";
    
    private final String ENDPOINT_CONTRACT_DETAIL = "/detallecontrato";
    
    private final String ENDPOINT_MOVEMENT_AND_POSITION = "/movimientoyposicion";

    private final String ENDPOINT_STATEMENTS_FILE = "/estadocuenta";

    private final String ENDPOINT_STATEMENTS_INFO = "/estadocuentadatos";

    private final String ENDPOINT_TAX_CERTIFICATE_FILE = "/constancia";

    private final String ENDPOINT_TAX_CERTIFICATE_PERIODS = "/constanciadetalle";

    public String getBaseEndpoint() {
        return host + endpoint;
    }
    
    /**
     * Perform Login operation to backend application
     * 
     * @param login
     * @return ResponseEntity<LoginResponse> the result of 
     */
    public ResponseEntity<LoginResponse> login(Login login) {

        return this.restClient.post()
            .uri(getBaseEndpoint() + ENDPOINT_LOGING)
            .contentType(MediaType.APPLICATION_JSON)
            .body(login)
            .retrieve()
            .toEntity(LoginResponse.class);

    }

    public String getJson(ObjectMapper mapper, Object objectRequest, String endpoint) throws JsonProcessingException {
        String json = mapper.writeValueAsString(objectRequest);
        ResponseEntity<String> responseEntity = RestClient.create().post()
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

        return responseEntity.getBody();
    }

    /**
     * 
     * @param customerNumber
     * @param json
     * @param md5
     * @param equestType
     * @return CustomerData
     */
    public CustomerData createCustomerData(Long customerNumber, String json, String md5, int requestType) {
        CustomerData customerData = new CustomerData();

        try {
            customerData.setCreated(new Date());
            customerData.setCustomerNumber(customerNumber);
            customerData.setData(json);
            customerData.setHash(md5);
            customerData.setRequestType(requestType);

            customerDataRepository.saveAndFlush(customerData);
        } catch (Exception e) {
            System.out.println("Error on BackendService::createCustomerData: " + e.getLocalizedMessage());
        }

        return customerData;
    }

    public CustomerDetailResponse customerDetail(CustomerDetailRequest customerDetailRequest) throws NoSuchAlgorithmException, JsonMappingException, JsonProcessingException {
        CustomerDetailResponse customerDetailResponse = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = null;

        try {
            json = getJson(mapper, customerDetailRequest, ENDPOINT_CONTRACT_DETAIL);
            json = saveResponse(customerDetailRequest.getContrato(), json, CustomerData.REQUEST_CUSTOMER_DETAIL);
            
        } catch (Exception e) {
            System.out.println("Error on BackendService::customerDetail " + e.getLocalizedMessage());
        }

        customerDetailResponse = mapper.readValue(json, CustomerDetailResponse.class);

        return customerDetailResponse;
    }

    /**
     * 
     * @param positionRequest
     * @return
     */
    public CustomerMovementPositionResponse customerPosition(CustomerMovementPositionRequest positionRequest) {
        CustomerMovementPositionResponse customerPositionResponse = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = null;

        try {
            //json = getJson(mapper, positionRequest, ENDPOINT_MOVEMENT_AND_POSITION);

            json = saveResponse(positionRequest.getContrato(), 
                getJson(mapper, positionRequest, this.ENDPOINT_MOVEMENT_AND_POSITION), 
                CustomerData.REQUEST_CUSTOMER_POSITION_BALANCE);
            /*
            // Get md5sum
            String md5sumResponse = getMd5(json);
            // Get previous data
            Long customerNumber = Long.parseLong(positionRequest.getContrato());
            CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestType(customerNumber, CustomerData.REQUEST_CUSTOMER_POSITION_BALANCE);

            // If data exist compare md5's
            if (customerData != null) {// existing record, compare and save
                if (customerData.getHash().equals(md5sumResponse)) { // Same content, do not persist to db
                    // Ideality use the information from the database, dispose the data from request
                } else {// Data is different from previous, save new data
                    createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_POSITION_BALANCE);
                }
            } else { // no exists, add new record to db
                createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_POSITION_BALANCE);
            }
            */

            customerPositionResponse = mapper.readValue(json, CustomerMovementPositionResponse.class);

        } catch (Exception e) {
            System.out.println("Error on BackendService::customerPosition: " + e.getLocalizedMessage());
        }

        return customerPositionResponse;
    }
    

    /**
     * 
     * @param customerMovementPositionRequest
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    public CustomerMovementPositionResponse customerMovements(CustomerMovementPositionRequest movementRequest) throws NoSuchAlgorithmException, JsonMappingException, JsonProcessingException {
        CustomerMovementPositionResponse customerMovementResponse = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = null;

        try {
            json = getJson(mapper, movementRequest, ENDPOINT_MOVEMENT_AND_POSITION);
            System.out.println("RawData: " + json);
            // Get md5sum
            String md5sumResponse = getMd5(json);
            // Get previous data
            Long customerNumber = Long.parseLong(movementRequest.getContrato());
            CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestType(customerNumber, CustomerData.REQUEST_CUSTOMER_MOVEMENTS);

            // If data exist compare md5's
            if (customerData != null) { // existing record, compare and save
                if (customerData.getHash().equals(md5sumResponse)) { // Same content, do not persist to db
                    // Ideality use the information from the database, dispose the data from request
                } else {// Data is different from previous, save new data
                    createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_MOVEMENTS);
                }
            } else { // no exists, add new record to db
                createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_MOVEMENTS);
            }

            customerMovementResponse = mapper.readValue(json, CustomerMovementPositionResponse.class);

        } catch (Exception e) {
            System.out.println("Error on BackendService::customerMovement: " + e.getLocalizedMessage());
        }
        
        /*
        // Get json data
        System.out.println("PerformRequestTo: " + getBaseEndpoint() + ENDPOINT_MOVEMENT_AND_POSITION);

        System.out.println("Data: " + customerMovementPositionRequest.toString());
        ResponseEntity<String> jsonResponse = this.restClient.post()
            .uri(getBaseEndpoint() + ENDPOINT_MOVEMENT_AND_POSITION)
            .contentType(MediaType.APPLICATION_JSON)
            .body(customerMovementPositionRequest)
            .retrieve()
            .toEntity(String.class);

        System.out.println("StatusCode: " + jsonResponse.getStatusCode());
        
        String rawData = jsonResponse.getBody();
        System.out.println("RawJson: " + rawData);
        // Get md5sum
        String md5sumResponse = getMd5(rawData);
        // Get previous data
        String contractNumber = "101272";
        CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestType(Long.parseLong(contractNumber), CustomerData.REQUEST_CUSTOMER_POSITION_BALANCE);

        // If data exist compare md5's
        if (customerData != null) {// existing record, compare and save
            if (customerData.getMd5String().equals(md5sumResponse)) { // Same content, do not persist to db
                // Ideality use the information from the database, dispose the data from request
            } else {// Data is different from previous, save new data
                CustomerData newCustomerData = new CustomerData();
                newCustomerData.setCustomerNumber(Long.parseLong(contractNumber)); // @TODO Change the value
                newCustomerData.setData(rawData);
                newCustomerData.setMd5String(md5sumResponse);
                newCustomerData.setRequestType(CustomerData.REQUEST_CUSTOMER_POSITION_BALANCE);;
                newCustomerData.setCreated(new Date());

                customerDataRepository.saveAndFlush(newCustomerData);
            }
        } else { // no exists, add new record to db
            CustomerData newCustomerData = new CustomerData();
            newCustomerData.setCustomerNumber(Long.parseLong(contractNumber)); // @TODO Change the value
            newCustomerData.setData(rawData);
            newCustomerData.setMd5String(md5sumResponse);
            newCustomerData.setRequestType(CustomerData.REQUEST_CUSTOMER_POSITION_BALANCE);;
            newCustomerData.setCreated(new Date());
            
            customerDataRepository.saveAndFlush(newCustomerData);
        }

        ObjectMapper mapper = new ObjectMapper();
        CustomerMovementPositionResponse customerMovementPositionResponse = mapper.readValue(rawData, CustomerMovementPositionResponse.class);
        */
        /*
        responseSpec.
        return this.restClient.post()
            .uri(getBaseEndpoint() + ENDPOINT_MOVEMENT_AND_POSITION)
            .contentType(MediaType.APPLICATION_JSON)
            .body(customerMovementPositionRequest)
            .retrieve()
            .toEntity(CustomerMovementPositionResponse.class);
        */
        return customerMovementResponse;
    }

    public CustomerStatementAccountResponse getStatementsInfo(CustomerStatementAccountRequest statementRequest) {
        CustomerStatementAccountResponse statementAccountResponse = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = null;

        try {
            json = getJson(mapper, statementRequest, ENDPOINT_STATEMENTS_INFO);
            System.out.println("RawData: " + json);
            // Get md5sum
            String md5sumResponse = getMd5(json);
            // Get previous data
            Long customerNumber = Long.parseLong(statementRequest.getContrato());
            CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestType(customerNumber, CustomerData.REQUEST_CUSTOMER_STATEMENTS_DATA);

            // If data exist compare md5's
            if (customerData != null) { // existing record, compare and save
                if (customerData.getHash().equals(md5sumResponse)) { // Same content, do not persist to db
                    // Ideality use the information from the database, dispose the data from request
                } else {// Data is different from previous, save new data
                    createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_STATEMENTS_DATA);
                }
            } else { // no exists, add new record to db
                createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_STATEMENTS_DATA);
            }

            statementAccountResponse = mapper.readValue(json, CustomerStatementAccountResponse.class);

        } catch (Exception e) {
            System.out.println("Error on BackendService::getStatementsInfo: " + e.getLocalizedMessage());
        }

        return statementAccountResponse;
    }

    public CustomerStatementFileResponse getStatementsFile(CustomerStatementAccountRequest statementRequest) {
        CustomerStatementFileResponse statementFileResponse = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = null;

        try {
            json = getJson(mapper, statementRequest, ENDPOINT_STATEMENTS_FILE);
            // Get md5sum
            String md5sumResponse = getMd5(json);
            // Get previous data
            Long customerNumber = Long.parseLong(statementRequest.getContrato());
            CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestType(customerNumber, CustomerData.REQUEST_CUSTOMER_STATEMENTS_FILE);

            // If data exist compare md5's
            if (customerData != null) { // existing record, compare and save
                if (customerData.getHash().equals(md5sumResponse)) { // Same content, do not persist to db
                    // Ideality use the information from the database, dispose the data from request
                } else {// Data is different from previous, save new data
                    createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_STATEMENTS_FILE);
                }
            } else { // no exists, add new record to db
                createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_STATEMENTS_FILE);
            }

            statementFileResponse = mapper.readValue(json, CustomerStatementFileResponse.class);

        } catch (Exception e) {
            System.out.println("Error on BackendService::getStatementsFile: " + e.getLocalizedMessage());
        }

        return statementFileResponse;
    }

    public CustomerTaxCertificateDetailResponse getTaxCertificatesDetail(CustomerTaxCertificateRequest taxCertificateRequest) {
        CustomerTaxCertificateDetailResponse taxCertificateDetailResponse = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = null;

        try {
            json = getJson(mapper, taxCertificateRequest, ENDPOINT_TAX_CERTIFICATE_PERIODS);
            // Get md5sum
            String md5sumResponse = getMd5(json);
            // Get previous data
            Long customerNumber = Long.parseLong(taxCertificateRequest.getContrato());
            CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestType(customerNumber, CustomerData.REQUEST_CUSTOMER_TAX_CERTIFICATES_DATA);

            if (customerData != null) { // existing record, compare and save
                if (customerData.getHash().equals(md5sumResponse)) { // Same content, do not persist to db
                    // Ideality use the information from the database, dispose the data from request
                } else {// Data is different from previous, save new data
                    createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_TAX_CERTIFICATES_DATA);
                }
            } else { // no exists, add new record to db
                createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_TAX_CERTIFICATES_DATA);
            }

            taxCertificateDetailResponse = mapper.readValue(json, CustomerTaxCertificateDetailResponse.class);

        } catch (Exception e) {
            System.out.println("Error on BackendService::getTaxCertificatesDetail: " + e.getLocalizedMessage());
        }

        return taxCertificateDetailResponse;
    }

    public CustomerTaxCertificateResponse getTaxCertificateFile(CustomerTaxCertificateRequest taxCertificateRequest) {
        CustomerTaxCertificateResponse taxCertificateResponse = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = null;

        try {
            json = getJson(mapper, taxCertificateRequest, ENDPOINT_TAX_CERTIFICATE_FILE);
            // Get md5sum
            String md5sumResponse = getMd5(json);
            // Get previous data
            Long customerNumber = Long.parseLong(taxCertificateRequest.getContrato());
            CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestType(customerNumber, CustomerData.REQUEST_CUSTOMER_TAX_CERTIFICATES_FILE);

            if (customerData != null) { // existing record, compare and save
                if (customerData.getHash().equals(md5sumResponse)) { // Same content, do not persist to db
                    // Ideality use the information from the database, dispose the data from request
                } else {// Data is different from previous, save new data
                    createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_TAX_CERTIFICATES_FILE);
                }
            } else { // no exists, add new record to db
                createCustomerData(customerNumber, json, md5sumResponse, CustomerData.REQUEST_CUSTOMER_TAX_CERTIFICATES_FILE);
            }

            taxCertificateResponse = mapper.readValue(json, CustomerTaxCertificateResponse.class);
        } catch (Exception e) {
            System.out.println("Error on BackendService::getTaxCertificatesInfo: " + e.getLocalizedMessage());
        }

        return taxCertificateResponse;
    }

    private String saveResponse(String customerNumber, String json, Integer requestType) {
        String jsonHash;
        Long customerNumberLong;

        try {
            customerNumberLong = Long.parseLong(customerNumber);
            jsonHash = getHashString(json);

            CustomerData customerData = customerDataRepository.findTopByCustomerNumberAndRequestTypeAndHash(customerNumberLong, requestType, jsonHash);
            if (customerData == null) {
                createCustomerData(customerNumberLong, json, jsonHash, requestType);
            } else {
                json = customerData.getData();
            }
        } catch (Exception e) {
            System.out.println("Error on BackendService::saveResponse: " + e.getLocalizedMessage());
        }

        return json;
    }

    
    /**
     * 
     * @param texString
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String getMd5(String texString) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");

        digest.update(texString.getBytes(), 0, 0);
        byte[] digestResult = digest.digest();

        StringBuffer buffer = new StringBuffer();
        for(byte dr: digestResult) {
            buffer.append(Integer.toHexString((int) (dr & 0xff)));
        }

        return buffer.toString();
    }

    /**
     * 
     * @param jsonString
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String getHashString(String jsonString) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(jsonString.getBytes());
        StringBuilder stringBuilder = new StringBuilder();

        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);
        }

        return stringBuilder.toString();
    }
    
}
