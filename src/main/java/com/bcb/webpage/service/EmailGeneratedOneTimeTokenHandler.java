package com.bcb.webpage.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.authentication.ott.RedirectOneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.bcb.webpage.model.webpage.entity.ConfigurationEmailAccountEntity;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.webpage.entity.interfaces.EmailInterface;
import com.bcb.webpage.model.webpage.repository.ConfigurationEmailAccountRepository;
import com.bcb.webpage.model.webpage.repository.CustomerCustomerRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailGeneratedOneTimeTokenHandler implements OneTimeTokenGenerationSuccessHandler {

    @Autowired
    private CustomerCustomerRepository customerRepository;

    @Autowired
    private ConfigurationEmailAccountRepository configurationEmailAccountRepository;

    @Autowired
    private EmailService emailService;

    private final OneTimeTokenGenerationSuccessHandler redirectHandler = 
        new RedirectOneTimeTokenGenerationSuccessHandler("/ott/sent");

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken)
            throws IOException, ServletException {

        try {
            log.info("Token: " + oneTimeToken.getUsername());

            UriComponentsBuilder build = UriComponentsBuilder.fromUriString(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .path("/login/ott")
                .queryParam("token", oneTimeToken.getTokenValue());

            String magicLink = build.toUriString();
            String email = oneTimeToken.getUsername();
            CustomerCustomer customer = customerRepository.findByEmail(email).getFirst();

            Optional<ConfigurationEmailAccountEntity> result = configurationEmailAccountRepository.findOneByTypeAndTargetAndMode(EmailInterface.TYPE_SYSTEM_NOTIFICATION, EmailInterface.TARGET_SYSTEM_NOTIFICATION, EmailInterface.MODE_SHIPMENT);

            if (!result.isPresent()) {
                throw new Exception("No se encontró cuenta de email para envío de correo electrónicos, registre una cuenta");
            } else {
                ConfigurationEmailAccountEntity systemEmailAccount = result.get();
                
                Map<String, String> params = new HashMap<>();
                params.put("from", systemEmailAccount.getName());
                params.put("to", email);
                params.put("subject", "Solicitud de acceso por Token - BCB Casa de Bolsa");
                params.put("expirationTime", "5 minutos");
                params.put("username", customer.getCustomerFullName());
                params.put("magicLink", magicLink);

                emailService.sendMimeMessage2(emailService.getSystemEmailSender(systemEmailAccount), EmailInterface.ROUTE_TEMPLATE_OTT, params);
            }

        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }

        redirectHandler.handle(request, response, oneTimeToken);
    }
    
}
