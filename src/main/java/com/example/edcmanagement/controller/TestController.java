package com.example.edcmanagement.controller;

import com.example.edcmanagement.dto.ApiResponse;
import com.example.edcmanagement.service.SignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@ConditionalOnProperty(name = "test.endpoints.enabled", havingValue = "true")
public class TestController {
    
    @Autowired
    private SignatureService signatureService;

    @GetMapping("/generate-signature")
    public ResponseEntity<ApiResponse<Object>> generateSignature(
        @RequestParam String terminalId,
        @RequestParam(required = false) String dateTime) {

    try {
        LocalDateTime requestDateTime;
        if (dateTime != null && !dateTime.isEmpty()) {
            requestDateTime = LocalDateTime.parse(dateTime,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            requestDateTime = LocalDateTime.now();
        }

        String signature = signatureService.generateSignatureForTesting(terminalId, requestDateTime);

        Map<String, Object> result = new HashMap<>();
        result.put("terminalId", terminalId);
        result.put("dateTime", requestDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.put("signature", signature);
        result.put("curlExample", generateCurlExample(terminalId, signature));

        ApiResponse<Object> response = ApiResponse.success(
            "Signature generated successfully", result);

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        ApiResponse<Object> response = ApiResponse.error("Error generating signature: " + e.getMessage());
        return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/validate-signature")
    public ResponseEntity<ApiResponse<Object>> validateSignature(
            @RequestParam String terminalId,
            @RequestParam String signature,
            @RequestParam(required = false) String dateTime) {

        try {
            LocalDateTime requestDateTime;
            if (dateTime != null && !dateTime.isEmpty()) {
                requestDateTime = LocalDateTime.parse(dateTime,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } else {
                requestDateTime = LocalDateTime.now();
            }

            boolean isValid = signatureService.validateSignature(signature, terminalId, requestDateTime);
            boolean isValidWithTolerance = signatureService.validateSignatureWithTolerance(
                signature, terminalId, requestDateTime, 2);

            Map<String, Object> result = new HashMap<>();
            result.put("terminalId", terminalId);
            result.put("signature", signature);
            result.put("dateTime", requestDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.put("isValid", isValid);
            result.put("isValidWithTolerance", isValidWithTolerance);

            ApiResponse<Object> response = ApiResponse.success(
                "Signature validation completed", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<Object> response = ApiResponse.error("Error validating signature: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        ApiResponse<String> response = ApiResponse.success(
            "EDC Management API is running", "OK");
        return ResponseEntity.ok(response);
    }

    private String generateCurlExample(String terminalId, String signature) {
        return String.format(
            "curl -X POST http://localhost:8080/api/edc/echo \\\n" +
            "  -H \"Content-Type: application/json\" \\\n" +
            "  -H \"Signature: %s\" \\\n" +
            "  -d '{\"terminalId\": \"%s\"}'",
            signature, terminalId
        );
    }
}
