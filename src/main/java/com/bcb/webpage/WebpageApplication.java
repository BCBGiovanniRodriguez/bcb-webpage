package com.bcb.webpage;

import java.io.ObjectInputFilter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.bcb.webpage.model.sisbur.service.LegacyService;
import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.services.CustomerService;
import com.bcb.webpage.service.PasswordResetService;

@SpringBootApplication
@EnableScheduling
public class WebpageApplication implements CommandLineRunner {

	@Autowired
	private LegacyService legacyService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PasswordResetService passwordResetService;

	public static void main(String[] args) {
		// Mitigation for CVE-2025-10492: set a global deserialization filter that rejects
		// deserialization of classes from the JasperReports library until a fixed
		// library version is available. This reduces the risk of remote code execution
		// via untrusted serialized data.
		/*
		try {
			ObjectInputFilter filter = info -> {
				Class<?> sClass = info.serialClass();
				if (sClass != null) {
					String name = sClass.getName();
					// Reject any attempt to deserialize JasperReports classes
					if (name.startsWith("net.sf.jasperreports")) {
						return ObjectInputFilter.Status.REJECTED;
					}
					// Allow common safe packages used by the application
					if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("com.bcb.")) {
						return ObjectInputFilter.Status.ALLOWED;
					}
				}
				return ObjectInputFilter.Status.UNDECIDED;
			};
			ObjectInputFilter.Config.setSerialFilter(filter);
			System.out.println("Global deserialization filter set to block JasperReports classes (CVE mitigation)");
		} catch (Throwable t) {
			System.err.println("Failed to set global deserialization filter: " + t.getMessage());
		}
		*/
		SpringApplication.run(WebpageApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Iniciando Proceso de Búsqueda de Nuevos Contratos");

		try {
			List<CustomerContract> customerContractList = customerService.getCustomersContracts();
			List<String> contractIdExcludedList = new ArrayList<>();

			for (CustomerContract contract : customerContractList) {
				contractIdExcludedList.add(contract.getContractNumber());
			}

			List<Map<String, Object>> unregisteredCustomerContracts = new ArrayList<>();
			// Get all contracts
			List<Map<String, Object>> customerContracts = legacyService.getCustomerContracts(LegacyService.CONTRACT_INITIAL_YES);

			if (!customerContracts.isEmpty()) {
				String contractNumber;

				for (Map<String,Object> customerContractMap : customerContracts) {
					contractNumber = customerContractMap.get("CONTRATO").toString();
					// Filter contracts
					if (!contractIdExcludedList.contains(contractNumber)) {
						unregisteredCustomerContracts.add(customerContractMap);
					}
				}

				if (!unregisteredCustomerContracts.isEmpty()) {
					customerService.setTownshipList(legacyService.getTownshipLists(unregisteredCustomerContracts));
					customerService.setCityList(legacyService.getCityLists(unregisteredCustomerContracts));
					customerService.setStateList(legacyService.getStateLists(unregisteredCustomerContracts));
					customerService.setCountryList(legacyService.getCountryLists(unregisteredCustomerContracts));
					customerService.setProfileList(legacyService.getProfileLists());
		
					customerService.saveCustomers(unregisteredCustomerContracts);
				}
			}
			System.out.println("Terminado Proceso de Migración de Nuevos Contratos");

			unregisteredCustomerContracts.clear();
			customerContracts = legacyService.getCustomerContracts(LegacyService.CONTRACT_INITIAL_NO);
			if (!customerContracts.isEmpty()) {
				String contractNumber;
				
				for (Map<String,Object> customerContractMap : customerContracts) {
					contractNumber = customerContractMap.get("CONTRATO").toString();
					// Filter contracts
					if (!contractIdExcludedList.contains(contractNumber)) {
						unregisteredCustomerContracts.add(customerContractMap);
					}
				}

				if (!unregisteredCustomerContracts.isEmpty()) {
					// Set data to service
					customerService.setTownshipList(legacyService.getTownshipLists(unregisteredCustomerContracts));
					customerService.setCityList(legacyService.getCityLists(unregisteredCustomerContracts));
					customerService.setStateList(legacyService.getStateLists(unregisteredCustomerContracts));
					customerService.setCountryList(legacyService.getCountryLists(unregisteredCustomerContracts));
					customerService.setProfileList(legacyService.getProfileLists());
		
					customerService.saveCustomers(unregisteredCustomerContracts);
				}
			}

			System.out.println("Terminado Proceso de Migración de Contratos Existentes");
		} catch (Exception e) {
			System.out.println("Error en Proceso de Búsqueda de Nuevos Contratos:: [" + e.getLocalizedMessage() +"]");
		}
	}

	@Scheduled(cron = "0 0 9-16 * * MON-FRI")
	public void searchNewContractsScheduledTask() {
		//System.out.println("Scheduled Task executed at " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		try {
			System.out.println("Iniciando Proceso de Búsqueda de Nuevos Contratos: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			List<CustomerContract> customerContractList = customerService.getCustomersContracts();
			List<String> contractIdExcludedList = new ArrayList<>();

			for (CustomerContract contract : customerContractList) {
				contractIdExcludedList.add(contract.getContractNumber());
			}

			List<Map<String, Object>> unregisteredCustomerContracts = new ArrayList<>();
			// Get all contracts
			List<Map<String, Object>> customerContracts = legacyService.getCustomerContracts(LegacyService.CONTRACT_INITIAL_YES);

			if (!customerContracts.isEmpty()) {
				String contractNumber;

				for (Map<String,Object> customerContractMap : customerContracts) {
					contractNumber = customerContractMap.get("CONTRATO").toString();
					// Filter contracts
					if (!contractIdExcludedList.contains(contractNumber)) {
						unregisteredCustomerContracts.add(customerContractMap);
					}
				}

				if (!unregisteredCustomerContracts.isEmpty()) {
					customerService.setTownshipList(legacyService.getTownshipLists(unregisteredCustomerContracts));
					customerService.setCityList(legacyService.getCityLists(unregisteredCustomerContracts));
					customerService.setStateList(legacyService.getStateLists(unregisteredCustomerContracts));
					customerService.setCountryList(legacyService.getCountryLists(unregisteredCustomerContracts));
					customerService.setProfileList(legacyService.getProfileLists());
		
					customerService.saveCustomers(unregisteredCustomerContracts);
				}
			}
			System.out.println("Terminado Proceso de Migración de Nuevos Contratos");
		} catch (Exception e) {
			System.out.println("Error en Proceso de Búsqueda de Nuevos Contratos:: [" + e.getLocalizedMessage() +"]");
		}

	}

	@Scheduled(cron = "0 * 7-18 * * MON-FRI")
	public void updateStockMarketPrices() {
		DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		if (!legacyService.isStockMarketActive()) {
			System.out.println("Mercado de Capitales cerrado");
		} else {
			long timeElapsed;
			Instant start = Instant.now();
			List<String> customerContractNumberList = legacyService.getCustomerContractStockMarketPositionList();
			System.out.println("TotalContratosClientes: " + customerContractNumberList.size());

			List<Map<String, Object>> stockMarketCustomerCurrentPosition = legacyService.getCurrentCustomerStockMarketPosition(customerContractNumberList);
			customerService.registerStockMarketPosition(stockMarketCustomerCurrentPosition);
			
			timeElapsed = Duration.between(start, Instant.now()).toMillis();
			System.out.println("[" + LocalDateTime.now().format(isoFormatter) + "] Termino actualización de precios Mercado de Capitales - Tiempo transcurrido: " + timeElapsed + " milisegundos");
		}
	}

	@Scheduled(cron = "0 */10 * * * MON-FRI")
	public void invalidateCustomerPasswordResetTokens() {
		try {
			System.out.println("Iniciando Proceso Invalidación de Tokens Expirados: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			passwordResetService.invalidateExpiredTokens();
			System.out.println("Terminado Proceso Invalidación de Tokens Expirados");
		} catch (Exception e) {
			System.out.println("Error al invalidar tokens expirados: " + e.getLocalizedMessage());
		}
	}
}
