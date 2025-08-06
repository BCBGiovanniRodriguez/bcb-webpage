package com.bcb.webpage.service;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.bcb.webpage.dto.report.MoneyMarketDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.json.data.JsonDataSource;

@Service
public class ReportService {
    
    private String primaryOutputPath = "./contracts/";

    private String secondaryOutputPath = "/reports/money_market/";

    public void generateMoneyMarketreport(String contractNumber, List<MoneyMarketDto> positionList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMddhhmmss");
        String outputPath = primaryOutputPath + contractNumber + secondaryOutputPath;
        
        try {
            Path path = Paths.get(outputPath);
            Files.createDirectories(path);

            outputPath += dateFormat.format(new Date());
            System.out.println("OutputPath: " + outputPath);
            
            ClassPathResource resource = new ClassPathResource("MoneyMarket_Letter_Landscape.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
            
            Map<String, Object> parameters = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = mapper.writeValueAsString(positionList);
            ByteArrayInputStream jsonDataInputStream = new ByteArrayInputStream(jsonData.getBytes());
            JsonDataSource jsonDataSource = new JsonDataSource(jsonDataInputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jsonDataSource);

            String fileName = dateFormat.format(new Date()) + "_Reporte_Mercado_De_Dinero.pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath + "/" + fileName);
            
        } catch (Exception e) {
            // TODO: handle exception
        }

        
    }

}
