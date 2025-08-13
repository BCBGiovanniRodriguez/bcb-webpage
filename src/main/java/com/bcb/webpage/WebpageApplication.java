package com.bcb.webpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.bcb.webpage.model.sisbur.service.LegacyService;
import com.bcb.webpage.model.sisbur.service.MovementService;
import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.webpage.services.CustomerService;

@SpringBootApplication
@EnableScheduling
public class WebpageApplication implements CommandLineRunner {

	//@Autowired
	//private SisburBackendService sisburBackendService;

	@Autowired
	private MovementService movementService;

	@Autowired
	private LegacyService legacyService;

	@Autowired
	private CustomerService customerService;

	public static void main(String[] args) {
		SpringApplication.run(WebpageApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Run run!!");

		try {
			List<CustomerContract> customerContractList = customerService.getCustomersContracts();
			List<Long> contractIdExcludedList = new ArrayList<>();

			for (CustomerContract contract : customerContractList) {
				contractIdExcludedList.add(Long.parseLong(contract.getContractNumber()));
			}
			// Search for new contracts
			List<Map<String, Object>> customerContracts = legacyService.getCustomerContracts(contractIdExcludedList);
			customerService.saveCustomers(customerContracts);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

}
