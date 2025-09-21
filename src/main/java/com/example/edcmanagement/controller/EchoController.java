package com.example.edcmanagement.controller;

import com.example.edcmanagement.dto.ApiResponse;
import com.example.edcmanagement.dto.EchoLogStatsDto;
import com.example.edcmanagement.dto.EchoRequest;
import com.example.edcmanagement.dto.EchoResponse;
import com.example.edcmanagement.entity.EchoLog;
import com.example.edcmanagement.service.EchoLogService;
import com.example.edcmanagement.service.SignatureService;
import com.example.edcmanagement.service.TerminalEDCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/edc")
@Validated
public class EchoController {
    
    private static final Logger logger = LoggerFactory.getLogger(EchoController.class);
    
    @Autowired
    private EchoLogService echoLogService;
    
    @Autowired
    private SignatureService signatureService;
    
    @Autowired
    private TerminalEDCService terminalService;
    
    /**
     * Echo endpoint - receives requests from EDC terminals
     */
    @PostMapping("/echo")
    public ResponseEntity<EchoResponse> echo(@Valid @RequestBody EchoRequest request, 
                                           HttpServletRequest httpRequest) {
        
        LocalDateTime requestTimestamp = LocalDateTime.now();
        String terminalId = request.getTerminalId();
        String signature = httpRequest.getHeader("Signature");
        String clientIp = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        logger.info("Echo request received from terminal: {} at {}", terminalId, requestTimestamp);
        
        try {
            // Validate signature
            boolean isSignatureValid = signatureService.validateSignatureWithTolerance(
                signature, terminalId, requestTimestamp, 2); // 2 minutes tolerance
            
            if (!isSignatureValid) {
                logger.warn("Invalid signature for terminal: {}", terminalId);
                
                // Log the failed attempt
                echoLogService.createEchoLog(terminalId, clientIp, userAgent, 
                    false, "UNAUTHORIZED", "Invalid signature");
                
                EchoResponse response = new EchoResponse(terminalId, requestTimestamp, 
                    "ERROR", "Unauthorized - Invalid signature");
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Check if terminal exists
            if (!terminalService.existsByTerminalId(terminalId)) {
                logger.warn("Unknown terminal ID: {}", terminalId);
                
                // Log the failed attempt
                echoLogService.createEchoLog(terminalId, clientIp, userAgent, 
                    true, "NOT_FOUND", "Terminal not found");
                
                EchoResponse response = new EchoResponse(terminalId, requestTimestamp, 
                    "ERROR", "Terminal not found");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Update terminal last ping
            terminalService.updateLastPing(terminalId);
            
            // Log successful echo
            echoLogService.createEchoLog(terminalId, clientIp, userAgent, 
                true, "SUCCESS", null);
            
            logger.info("Echo processed successfully for terminal: {}", terminalId);
            
            EchoResponse response = new EchoResponse(terminalId, requestTimestamp, 
                "SUCCESS", "Echo received successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing echo request for terminal: {}", terminalId, e);
            
            // Log the error
            echoLogService.createEchoLog(terminalId, clientIp, userAgent, 
                null, "ERROR", "Internal server error: " + e.getMessage());
            
            EchoResponse response = new EchoResponse(terminalId, requestTimestamp, 
                "ERROR", "Internal server error");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get all echo logs with pagination
     */
    @GetMapping("/echo-logs")
    public ResponseEntity<ApiResponse<Page<EchoLog>>> getAllEchoLogs(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "requestTimestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<EchoLog> echoLogs = echoLogService.getAllEchoLogs(pageable);
            
            ApiResponse<Page<EchoLog>> response = ApiResponse.success(
                "Echo logs retrieved successfully", echoLogs);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving echo logs", e);
            ApiResponse<Page<EchoLog>> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get echo logs by terminal ID
     */
    @GetMapping("/echo-logs/terminal/{terminalId}")
    public ResponseEntity<ApiResponse<List<EchoLog>>> getEchoLogsByTerminalId(
            @PathVariable String terminalId) {
        
        try {
            List<EchoLog> echoLogs = echoLogService.getEchoLogsByTerminalId(terminalId);
            
            ApiResponse<List<EchoLog>> response = ApiResponse.success(
                "Echo logs retrieved successfully for terminal: " + terminalId, echoLogs);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving echo logs for terminal: {}", terminalId, e);
            ApiResponse<List<EchoLog>> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get echo logs by terminal ID with pagination
     */
    @GetMapping("/echo-logs/terminal/{terminalId}/paged")
    public ResponseEntity<ApiResponse<Page<EchoLog>>> getEchoLogsByTerminalIdPaged(
            @PathVariable String terminalId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, 
                Sort.by("requestTimestamp").descending());
            
            Page<EchoLog> echoLogs = echoLogService.getEchoLogsByTerminalId(terminalId, pageable);
            
            ApiResponse<Page<EchoLog>> response = ApiResponse.success(
                "Echo logs retrieved successfully for terminal: " + terminalId, echoLogs);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving paged echo logs for terminal: {}", terminalId, e);
            ApiResponse<Page<EchoLog>> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get today's echo logs
     */
    @GetMapping("/echo-logs/today")
    public ResponseEntity<ApiResponse<List<EchoLog>>> getTodayEchoLogs() {
        try {
            List<EchoLog> echoLogs = echoLogService.getTodayEchoLogs();
            
            ApiResponse<List<EchoLog>> response = ApiResponse.success(
                "Today's echo logs retrieved successfully", echoLogs);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving today's echo logs", e);
            ApiResponse<List<EchoLog>> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get today's echo logs for a specific terminal
     */
    @GetMapping("/echo-logs/today/terminal/{terminalId}")
    public ResponseEntity<ApiResponse<List<EchoLog>>> getTodayEchoLogsByTerminalId(
            @PathVariable String terminalId) {
        
        try {
            List<EchoLog> echoLogs = echoLogService.getTodayEchoLogsByTerminalId(terminalId);
            
            ApiResponse<List<EchoLog>> response = ApiResponse.success(
                "Today's echo logs retrieved successfully for terminal: " + terminalId, echoLogs);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving today's echo logs for terminal: {}", terminalId, e);
            ApiResponse<List<EchoLog>> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get echo log statistics
     */
    @GetMapping("/echo-logs/stats")
    public ResponseEntity<ApiResponse<Object>> getEchoLogStats() {
        try {
            long totalLogs = echoLogService.getAllEchoLogs().size();
            long validSignatures = echoLogService.getEchoLogCountBySignatureValid(true);
            long invalidSignatures = echoLogService.getEchoLogCountBySignatureValid(false);

            EchoLogStatsDto stats = new EchoLogStatsDto(totalLogs, validSignatures, invalidSignatures);

            ApiResponse<Object> response = ApiResponse.success(
                "Echo log statistics retrieved successfully", stats);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrieving echo log statistics", e);
            ApiResponse<Object> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    
    /**
     * Utility method to get client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            return xForwardedForHeader.split(",")[0].trim();
        }
        
        String xRealIpHeader = request.getHeader("X-Real-IP");
        if (xRealIpHeader != null && !xRealIpHeader.isEmpty()) {
            return xRealIpHeader;
        }
        
        return request.getRemoteAddr();
    }
}