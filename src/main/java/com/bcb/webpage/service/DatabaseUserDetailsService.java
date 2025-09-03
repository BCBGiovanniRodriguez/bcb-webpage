package com.bcb.webpage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bcb.webpage.model.webpage.entity.CustomerSession;
import com.bcb.webpage.model.webpage.entity.customers.CustomerAuthenticated;
import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.webpage.repository.CustomerContractRepository;
import com.bcb.webpage.model.webpage.repository.CustomerSessionRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    @Autowired
    CustomerContractRepository contractRepository;

    @Autowired
    CustomerSessionRepository customerSessionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<CustomerContract> result = contractRepository.findOneByContractNumber(username);

        if (!result.isPresent()) {
            throw new UsernameNotFoundException("Registro no encontrado");
        } else {
            CustomerContract contract = result.get();
            CustomerCustomer customer = contract.getCustomer();
            
            try {
                // Invalidate last session
                List<CustomerSession> customerSessionList = customer.getSessions();
                if (customerSessionList != null && customerSessionList.size() > 0) {
                    CustomerSession oldCustomerSession = customer.getSessions()
                    .stream()
                    .filter(cs -> cs.isCurrent())
                    .findFirst()
                    .orElse(null);

                    oldCustomerSession.setCurrent(false);
                    customerSessionRepository.save(oldCustomerSession);
                }
                
                // Generate New Session
                LocalDateTime now = LocalDateTime.now();
                CustomerSession newCustomerSession = new CustomerSession();
                newCustomerSession.setCurrent(true);
                newCustomerSession.setCustomer(customer);
                newCustomerSession.setTimestamp(now);
                // Save Session
                customerSessionRepository.saveAndFlush(newCustomerSession);
                customerSessionRepository.flush();
                // Set contract as current, get all contracts
                contract.setCurrent(CustomerContract.CURRENT_TRUE);
                contractRepository.saveAndFlush(contract);

                for(CustomerContract contractTmp : customer.getContracts()) {
                    if (contract.getCustomerContractId() != contractTmp.getCustomerContractId()) {
                        contractTmp.setCurrent(CustomerContract.CURRENT_FALSE);
                        contractRepository.saveAndFlush(contractTmp);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getLocalizedMessage());
            }

            return new CustomerAuthenticated(customer, contract);
        }
    }

}
