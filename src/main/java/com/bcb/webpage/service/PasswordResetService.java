package com.bcb.webpage.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

@Service
public class PasswordResetService {

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

    private JavaMailSender mailSender;

    private PasswordEncoder passwordEncoder;

    public void generateToken(String customerNumber) throws Exception {
        Optional<CustomerContract> contractFoundResult = contractRepository.findOneByContractNumber(customerNumber);

        if (!contractFoundResult.isPresent()) {
            throw new Exception("Número de Contrato no encontrado");
        } else {
            CustomerContract customerContract = contractFoundResult.get();
            String token = UUID.randomUUID().toString();
    
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(token);
            passwordResetToken.setContract(customerContract);
            passwordResetToken.setExpirationDate(LocalDateTime.now().plusMinutes(10));

            passwordResetTokenRepository.saveAndFlush(passwordResetToken);

            String rootUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String link = rootUrl + "/nuevo-password?token=" + token;

            Optional<ConfigurationEmailAccountEntity> result = emailAccountRepository.findOneByTypeAndTargetAndMode(EmailInterface.TYPE_SYSTEM_NOTIFICATION, EmailInterface.TARGET_SYSTEM_NOTIFICATION, EmailInterface.MODE_SHIPMENT);
            if (result.isPresent()) {
                ConfigurationEmailAccountEntity systemEmail = result.get();

                Map<String, String> params = new HashMap<>();
                params.put("from", systemEmail.getName());
                params.put("to", customerContract.getCustomer().getEmail());
                params.put("subject", "Solicitud de Reestablecimiento de Contraseña - BCB Casa de Bolsa");

                params.put("username", customerContract.getCustomer().getCustomerFullName());
                params.put("expirationTime", "10 minutos");
                params.put("resetPasswordLink", link);

                emailService.sendRecoverPasswordMessage(emailService.getSystemEmailSender(systemEmail), EmailInterface.ROUTE_TEMPLATE_RECOVER_PASSWORD, params);

            }
        }
    }

    public void resetPassword(String token, String password) throws Exception {

        Optional<PasswordResetToken> resetTokenResult = passwordResetTokenRepository.findOneByToken(token);

        if (!resetTokenResult.isPresent()) {
            throw new Exception("Token Inválido");
        } else {
            PasswordResetToken passwordResetToken = resetTokenResult.get();

            if (passwordResetToken.isExpired()) {
                throw new Exception("Token Expirado");
            } else {
                //!! -> Update on backend side
                String newPassword = new BCryptPasswordEncoder().encode(password);
                
                CustomerContract customerContract = passwordResetToken.getContract();

                CustomerCustomer customerCustomer = customerContract.getCustomer();
                customerCustomer.setPassword(newPassword);
                customerRepository.saveAndFlush(customerCustomer);
                
            }

            passwordResetTokenRepository.delete(passwordResetToken);
        }
    }

}
