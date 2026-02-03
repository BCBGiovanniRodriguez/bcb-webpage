package com.bcb.webpage.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bcb.webpage.model.webpage.entity.FinantialStatementEntity;
import com.bcb.webpage.model.webpage.repository.FinantialStatementRepository;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class FinantialStatementController {

    @Autowired
    private FinantialStatementRepository finantialStatementRepository;

    private static final String UPLOAD_DIR = "uploads/finantial-statements/";

    @GetMapping("/estados-financieros/descargar")
    public void download(@RequestParam Integer t, @RequestParam Integer y, @RequestParam(required = false) Integer p,
         HttpServletResponse response) {

        response.setContentType("application/pdf");
        String filePath;
        Optional<FinantialStatementEntity> finantialStatementResult = null;

        try {
            if (FinantialStatementEntity.TYPE_YEARLY == t) {
                finantialStatementResult = finantialStatementRepository.findOneByTypeAndYearAndStatus(t, y, 1);
            } else if(FinantialStatementEntity.TYPE_QUARTERLY == t) {
                finantialStatementResult = finantialStatementRepository.findOneByTypeAndYearAndPeriodAndStatus(t, y, p, 1);
            }

            if (finantialStatementResult != null && finantialStatementResult.isPresent()) {
                FinantialStatementEntity finantialStatementEntity = finantialStatementResult.get();

                //Path path = Paths.get(finantialStatementEntity.getPath() + finantialStatementEntity.getOriginalfileName());
                filePath = finantialStatementEntity.getPath() + finantialStatementEntity.getFileName() + ".pdf";
                File pdfFile = new File(filePath);

                response.setHeader("Content-Disposition", "attachment; filename=" + finantialStatementEntity.getFileName() + ".pdf");

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
                    System.out.println("FinantialStatementController::No se encontro el documento");
                }
            } else {
                System.out.println("FinantialStatementController::No se encontro registro");
            }

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

    }
}
