package com.bcb.webpage.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bcb.webpage.model.webpage.entity.FinantialStatementEntity;
import com.bcb.webpage.model.webpage.repository.FinantialStatementRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class IndexController {

    @Autowired
    private FinantialStatementRepository finantialStatementRepository;
    
    @GetMapping("/inicio-de-sesion")
    public String inicioSesion(Model model) {
        return "public/login";
    }

    @GetMapping("/")
    public String index() {
        return "public/index";
    }

    @GetMapping("/acerca-de-nosotros")
    public String aboutUs(Model model) {
        return "public/about-us";
    }

    @GetMapping("/productos-y-servicios")
    public String productsAndServices(Model model) {
        return "public/products-and-services";
    }

    @GetMapping("/personas-y-familias")
    public String personsAndFamilies(Model model) {
        return "public/persons-and-families";
    }

    @GetMapping("/inversionista-institucional")
    public String institutionalInvestments(Model model) {
        return "public/institutional-investments";
    }

    @GetMapping("/buro-de-entidades-financieras")
    public String finantialInstitutions(Model model) {
        return "public/finantial-institution";
    }

    @GetMapping("/mapa-del-sitio")
    public String siteMap(Model model) {
        return "public/site-map";
    }

    @GetMapping("/une")
    public String une(Model model) {
        return "public/une";
    }

    @GetMapping("/estados-financieros")
    public String finantialStatement(Model model) {
        ObjectMapper mapper = new ObjectMapper();
        Set<Integer> yearSet = new TreeSet<>();
        List<Integer> yearList = new ArrayList<>();

        List<FinantialStatementEntity> finantialStatementList = new ArrayList<>();
        List<Object> yearlyResults = new ArrayList<>();
        List<Object> quarterlyResults = new ArrayList<>();
        String yearlyJsonData = "";
        String quarterlyJsonData = "";

        try {
            finantialStatementList = finantialStatementRepository.findAll();

            for (FinantialStatementEntity finantialStatementEntity : finantialStatementList) {
                yearSet.add(finantialStatementEntity.getYear());
                
                Map<String, String> element = new HashMap<>();
                element.put("year", finantialStatementEntity.getYear() + "");
                element.put("type", finantialStatementEntity.getType() + "");
                element.put("period", finantialStatementEntity.getPeriod() + "");
                
                if (finantialStatementEntity.getType() == FinantialStatementEntity.TYPE_YEARLY) {
                    yearlyResults.add(element);
                } else if(finantialStatementEntity.getType() == FinantialStatementEntity.TYPE_QUARTERLY) {
                    element.put("periodAsString", finantialStatementEntity.getPeriodAsString());
                    quarterlyResults.add(element);
                }
            }

            yearlyJsonData = mapper.writeValueAsString(yearlyResults);
            quarterlyJsonData = mapper.writeValueAsString(quarterlyResults);

            yearList = yearSet.stream().sorted(Collections.reverseOrder()).toList();
            
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        model.addAttribute("yearSet", yearSet);
        model.addAttribute("yearList", yearList);
        model.addAttribute("yearlyJsonData", yearlyJsonData);
        model.addAttribute("quarterlyJsonData", quarterlyJsonData);

        return "public/finantial-statement";
    }

    @GetMapping("/administracion-de-riesgos")
    public String riskManagement(Model model) {
        return "public/risk-management";
    }

    @GetMapping("/remuneraciones")
    public String considerations(Model model) {
        return "public/considerations";
    }

    @GetMapping("/servicios-de-inversion")
    public String investmentServices(Model model) {
        return "public/investment-services";
    }

    @GetMapping("/aviso-de-privacidad")
    public String privacyNotice(Model model) {
        return "public/privacy-notice";
    }

    @GetMapping("/organos-de-control")
    public String commitees(Model model) {
        return "public/commitees";
    }

    @GetMapping("/gestion-de-inversion-y-mandatos")
    public String investmentManagementAndOrders(Model model) {
        return "public/investment-management-and-orders";
    }
    
    @GetMapping("/mandatos")
    public String orders(Model model) {
        return "public/orders";
    }

    @GetMapping("/fiduciario")
    public String trusts(Model model) {
        return "public/trusts";
    }

    @GetMapping("/fideicomisos-publicos")
    public String publicTrusts(Model model) {
        return "public/public-trusts";
    }

    @GetMapping("/fideicomisos-personas-fisicas")
    public String physicalPersonTrusts(Model model) {
        return "public/physical-person-trusts";
    }
    
    @GetMapping("/fideicomisos-personas-morales")
    public String enterprisePersonTrusts(Model model) {
        return "public/enterprise-person-trusts";
    }

    @GetMapping("/alternativas-de-inversion")
    public String investmentAlternatives(Model model) {
        return "public/investment-alternatives";
    }

    @GetMapping("/divisas")
    public String currencies(Model model) {
        return "public/currencies";
    }

    @GetMapping("/planeacion-financiera")
    public String finantialPlanning(Model model) {
        return "public/finantial-planning";
    }

    @GetMapping("/estrategias-selectivas")
    public String strategies(Model model) {
        return "public/strategies";
    }

    @GetMapping("/planes-de-prevision-social-pensiones-y-ahorro-institucional")
    public String socialSecurityPlansPensionsAndInstitutionalSavings(Model model) {
        return "public/social-security-plans";
    }

    @GetMapping("/record-keeping")
    public String recordKeeping(Model model) {
        return "public/record-keeping";
    }

    @GetMapping("/tesorerias")
    public String treasury(Model model) {
        return "public/treasury";
    }

    @GetMapping("/banca-de-inversion")
    public String investmentBank(Model model) {
        return "public/investment-bank";
    }

}
