package com.example.edcmanagement.service;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SignatureService {
    
    private static final Logger logger = LoggerFactory.getLogger(SignatureService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Value("${app.hmac.secret}")
    private String secretKey;
    
    public boolean validateSignature(String signature, String terminalId, LocalDateTime requestDateTime) {
        try {
            if (signature == null || signature.trim().isEmpty()) {
                logger.warn("Signature is null or empty");
                return false;
            }
            
            if (terminalId == null || terminalId.trim().isEmpty()) {
                logger.warn("Terminal ID is null or empty");
                return false;
            }
            
            // Create the key: dateTime|EDCmgmt2025!.?
            String dateTimeString = requestDateTime.format(DATE_TIME_FORMATTER);
            String key = dateTimeString + "|" + secretKey;
            
            // Create the message to be signed (terminalId)
            String message = terminalId;
            
            // Generate expected signature
            String expectedSignature = generateHmacSHA256(message, key);
            
            logger.debug("Generated key: {}", key);
            logger.debug("Message: {}", message);
            logger.debug("Expected signature: {}", expectedSignature);
            logger.debug("Received signature: {}", signature);
            
            // Compare signatures
            boolean isValid = expectedSignature.equals(signature);
            
            if (isValid) {
                logger.info("Signature validation successful for terminal: {}", terminalId);
            } else {
                logger.warn("Signature validation failed for terminal: {}", terminalId);
            }
            
            return isValid;
            
        } catch (Exception e) {
            logger.error("Error validating signature for terminal: {}", terminalId, e);
            return false;
        }
    }
    
    /**
     * Generates HMAC-SHA256 signature
     * @param message The message to be signed
     * @param key The secret key
     * @return The HMAC-SHA256 signature as hex string
     */
    private String generateHmacSHA256(String message, String key) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(message);
    }
    
    /**
     * Generates a signature for testing purposes
     * @param terminalId The terminal ID
     * @param dateTime The datetime
     * @return The generated signature
     */
    public String generateSignatureForTesting(String terminalId, LocalDateTime dateTime) {
        String dateTimeString = dateTime.format(DATE_TIME_FORMATTER);
        String key = dateTimeString + "|" + secretKey;
        return generateHmacSHA256(terminalId, key);
    }
    
    /**
     * Validates signature with tolerance for time differences
     * @param signature The signature from the request header
     * @param terminalId The terminal ID from the request body
     * @param requestDateTime The datetime when the request was received
     * @param toleranceMinutes The tolerance in minutes for time differences
     * @return true if signature is valid within the tolerance, false otherwise
     */
    public boolean validateSignatureWithTolerance(String signature, String terminalId, 
                                                 LocalDateTime requestDateTime, int toleranceMinutes) {
        
        // Try exact time first
        if (validateSignature(signature, terminalId, requestDateTime)) {
            return true;
        }
        
        // Try with tolerance
        for (int i = 1; i <= toleranceMinutes; i++) {
            // Try past minutes
            if (validateSignature(signature, terminalId, requestDateTime.minusMinutes(i))) {
                logger.info("Signature valid with {} minutes tolerance (past) for terminal: {}", i, terminalId);
                return true;
            }
            
            // Try future minutes
            if (validateSignature(signature, terminalId, requestDateTime.plusMinutes(i))) {
                logger.info("Signature valid with {} minutes tolerance (future) for terminal: {}", i, terminalId);
                return true;
            }
        }
        
        return false;
    }
}