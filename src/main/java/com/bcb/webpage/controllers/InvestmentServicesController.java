package com.bcb.webpage.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bcb.webpage.model.webpage.entity.InvestmentServicesEntity;
import com.bcb.webpage.model.webpage.repository.InvestmentServicesRepository;

import jakarta.servlet.http.HttpServletResponse;


@Controller
public class InvestmentServicesController {

    @Autowired
    private InvestmentServicesRepository investmentServicesRepository;

    private static final String UPLOAD_DIR = "uploads/investment-services/";

    @GetMapping("/servicios-inversion/descargar")
    public void download(@RequestParam Integer tipo, HttpServletResponse response) {

        String contentType = null;
        String fileExt = null;
        String filePath;
        Optional<InvestmentServicesEntity> investmentServicesResult = null;
        InvestmentServicesEntity investmentServicesEntity = null;
        
        try {
            investmentServicesResult = investmentServicesRepository.findOneByTypeAndCurrent(tipo, 1);
            if (investmentServicesResult.isPresent()) {
                investmentServicesEntity = investmentServicesResult.get();
                
                if (investmentServicesEntity.getOriginalFilename().endsWith(".docx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                    fileExt = ".docx";
                } else if(investmentServicesEntity.getOriginalFilename().endsWith(".pdf")) {
                    contentType = "application/pdf";
                    fileExt = ".pdf";
                }

                response.setContentType(contentType);
                response.setHeader("Content-Disposition", "attachment; filename=" + investmentServicesEntity.getFilename() + fileExt);

                filePath = investmentServicesEntity.getFilepath() + investmentServicesEntity.getFilename()  + ".pdf";
                File pdfFile = new File(filePath);

                if (pdfFile.exists() && pdfFile.isFile()) {
                    InputStream inputStream = new FileInputStream(pdfFile);

                    byte[] buffer = new byte[8192]; // 8 KB buffer for efficiency
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        response.getOutputStream().write(buffer, 0, bytesRead);
                    }

                    inputStream.close();
                    response.getOutputStream().flush(); // Ensure all data is written
                } else {
                    System.out.println("InvestmentServicesController::No se encontro el documento");
                }
            } else {
                System.out.println("InvestmentServicesController::No se encontro registro");
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

    }
    
}
