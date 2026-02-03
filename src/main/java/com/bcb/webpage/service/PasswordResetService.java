package com.bcb.webpage.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bcb.webpage.model.webpage.entity.ConfigurationEmailAccountEntity;
import com.bcb.webpage.model.webpage.entity.PasswordResetToken;
import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.webpage.entity.interfaces.EmailInterface;
import com.bcb.webpage.model.webpage.repository.ConfigurationEmailAccountRepository;
import com.bcb.webpage.model.webpage.repository.CustomerContractRepository;
import com.bcb.webpage.model.webpage.repository.CustomerCustomerRepository;
import com.bcb.webpage.model.webpage.repository.PasswordResetTokenRepository;
import com.bcb.webpage.service.sisbur.SisBurService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PasswordResetService {

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private SisBurService sisBurService;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private CustomerContractRepository contractRepository;

    @Autowired
    private CustomerCustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfigurationEmailAccountRepository emailAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void generateToken(String customerNumber) throws Exception {
        Optional<CustomerContract> contractFoundResult = contractRepository.findOneByContractNumber(customerNumber);
        
        if (!contractFoundResult.isPresent()) {
            throw new Exception("Número de Contrato no encontrado");
        } else {
            CustomerContract customerContract = contractFoundResult.get();

            // Search for requested tokens
            Optional<PasswordResetToken> passwordResetTokenOptional = passwordResetTokenRepository.findOneByCustomerContractAndStatus(customerContract, PasswordResetToken.STATUS_ENABLED);

            if (passwordResetTokenOptional.isPresent()) {
                throw new Exception("Tiene un token activo");
            } else {
                Optional<ConfigurationEmailAccountEntity> configurationEmailAccountOptional = emailAccountRepository.findOneByTypeAndTargetAndMode(EmailInterface.TYPE_SYSTEM_NOTIFICATION, EmailInterface.TARGET_SYSTEM_NOTIFICATION, EmailInterface.MODE_SHIPMENT);
                if (!configurationEmailAccountOptional.isPresent()) {
                    throw new Exception("Cuenta de correo de envío no configurada");
                } else {
                    String token = UUID.randomUUID().toString();
            
                    this.savePasswordResetToken(token, customerContract, 10);
                    this.sendEmail(configurationEmailAccountOptional.get(), customerContract, token);
                }
            }
        }
    }

    private void savePasswordResetToken(String token, CustomerContract customerContract, Integer minutes) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setCustomerContract(customerContract);
        passwordResetToken.setExpirationDate(LocalDateTime.now().plusMinutes(minutes));
        passwordResetToken.setStatus(PasswordResetToken.STATUS_ENABLED);
        passwordResetToken.setRequestedDate(LocalDateTime.now());
        passwordResetToken.setRemoteIpAddressRequester(httpServletRequest.getRemoteAddr());
        passwordResetToken.setRemoteUserAgentRequester(httpServletRequest.getHeader("user-agent"));

        passwordResetTokenRepository.saveAndFlush(passwordResetToken);
    }

    private void sendEmail(ConfigurationEmailAccountEntity systemEmail, CustomerContract customerContract, String token) throws MessagingException {
        String rootUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String link = rootUrl + "/nuevo-password?token=" + token;
        Map<String, String> params = new HashMap<>();
        params.put("from", systemEmail.getName());
        params.put("to", customerContract.getCustomer().getEmail());
        params.put("subject", "Solicitud de Restablecimiento de Contraseña - BCB Casa de Bolsa");
        params.put("username", customerContract.getCustomer().getCustomerFullName());
        params.put("expirationTime", "10 minutos");
        params.put("resetPasswordLink", link);

        emailService.sendRecoverPasswordMessage(emailService.getSystemEmailSender(systemEmail), EmailInterface.ROUTE_TEMPLATE_RECOVER_PASSWORD, params);
    }


    public void resetPassword(String token, String password) throws Exception {

        Optional<PasswordResetToken> resetTokenResult = passwordResetTokenRepository.findOneByTokenAndStatus(token, PasswordResetToken.STATUS_ENABLED);

        if (!resetTokenResult.isPresent()) {
            throw new Exception("Token Inválido");
        } else {
            PasswordResetToken passwordResetToken = resetTokenResult.get();

            if (!passwordResetToken.isExpired()) {
                CustomerContract customerContract = passwordResetToken.getCustomerContract();
                //!! -> Update on backend side
                sisBurService.updateCustomerContractPassword(customerContract.getCustomer().getCustomerKey(), customerContract.getContractNumber(), password, customerContract.isInitial());
                //!! -> Update on frontend side
                String newPassword = this.passwordEncoder.encode(password);
                customerContract.setPassword(newPassword);

                if (customerContract.isInitial()) {
                    customerContract.setInitial(0);
                }
                
                contractRepository.saveAndFlush(customerContract);
            }
            
            passwordResetToken.setProcessedDate(LocalDateTime.now());
            passwordResetToken.setStatus(PasswordResetToken.STATUS_DISABLED);
            passwordResetToken.setRemoteIpAddressProcessor(httpServletRequest.getRemoteAddr());
            passwordResetToken.setRemoteUserAgentProcessor(httpServletRequest.getHeader("user-agent"));

            passwordResetTokenRepository.saveAndFlush(passwordResetToken);

            if (passwordResetToken.isExpired()) {
                throw new Exception("Token Expirado");
            }
        }
    }

}
