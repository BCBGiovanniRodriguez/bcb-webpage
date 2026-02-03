package com.bcb.webpage.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bcb.webpage.model.webpage.entity.RiskManagementEntity;
import com.bcb.webpage.model.webpage.repository.RiskManagementRepository;

import jakarta.servlet.http.HttpServletResponse;


@Controller
public class RiskManagementController {

    @Autowired
    private RiskManagementRepository riskManagementRepository;

    @GetMapping("/administracion-de-riesgos/descargar")
    public void getMethodName(@RequestParam Integer tipo, HttpServletResponse response) {
        response.setContentType("application/pdf");
        String filePath;

        Optional<RiskManagementEntity> riskManagementResult = null;
        RiskManagementEntity riskManagementEntity = null;

        try {
            riskManagementResult = riskManagementRepository.findOneByTypeAndCurrent(tipo, 1);

            if (riskManagementResult.isPresent()) {
                riskManagementEntity = riskManagementResult.get();
                response.setHeader("Content-Disposition", "attachment; filename=" + riskManagementEntity.getFilename() + ".pdf");
    
                filePath = riskManagementEntity.getFilepath() + riskManagementEntity.getFilename()  + ".pdf";
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
                    System.out.println("RiskManagementController::No se encontro el documento");
                }
            } else {
                System.out.println("RiskManagementController::No se encontro registro");
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
    
}
