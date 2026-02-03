package com.bcb.webpage.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bcb.webpage.model.webpage.entity.ConfigurationEmailAccountEntity;
import com.bcb.webpage.model.webpage.entity.interfaces.EmailInterface;
import com.bcb.webpage.model.webpage.repository.ConfigurationEmailAccountRepository;
import com.bcb.webpage.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class SendEmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfigurationEmailAccountRepository configurationEmailAccountRepository;

    @PostMapping("/api/send-email")
    public String sendEmailPost(@RequestBody Map<String, Object> data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resultMap = new HashMap<>();
        String jsonResponse = "Exito!";

        try {
            Optional<ConfigurationEmailAccountEntity> systemEmailResult = configurationEmailAccountRepository.findOneByTypeAndMode(EmailInterface.TYPE_SYSTEM_NOTIFICATION, EmailInterface.MODE_SHIPMENT);
            
            if (!systemEmailResult.isPresent()) {
                throw new Exception("No se ha configurado cuenta de envío de correos");
            } else {
                ConfigurationEmailAccountEntity systemEmail = systemEmailResult.get();
                Integer type = Integer.parseInt(data.get("type").toString());
                Integer mode = EmailInterface.MODE_RECEPTION;

                Optional<ConfigurationEmailAccountEntity> bcbEmailResult = configurationEmailAccountRepository.findOneByTypeAndMode(type, mode);

                if (!bcbEmailResult.isPresent()) {
                    throw new Exception("No se encontró cuenta de correo ");
                } else {
                    ConfigurationEmailAccountEntity bcbEmail = bcbEmailResult.get();

                    Map<String, String> params = new HashMap<>();
                    params.put("from", systemEmail.getName());
                    params.put("to", bcbEmail.getName());
                    params.put("subject", "Mensaje desde Página Web BCB");
                    params.put("customer_name", data.get("name").toString());
                    params.put("customer_phone", data.get("phone").toString());
                    params.put("customer_email", data.get("email").toString());
                    params.put("customer_message", data.get("message").toString());

                    emailService.sendPublicCustomerMessage(emailService.getSystemEmailSender(systemEmail), EmailInterface.ROUTE_TEMPLATE_PUBLIC_CUSTOMER, params);
                }
                
                resultMap.put("status", 1);
                resultMap.put("message", "Petición Correcta");
                resultMap.put("data", "");
                
                jsonResponse = mapper.writeValueAsString(resultMap);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());

            resultMap.put("status", 0);
            resultMap.put("message", "Error en RequestRequestController::create[" + e.getLocalizedMessage() + "]");
            resultMap.put("data", null);

            jsonResponse = mapper.writeValueAsString(resultMap);
        }
        
        return jsonResponse;
    }
    
}
