package com.example.edcmanagement.dto;

import java.time.LocalDateTime;

public class EchoResponse {
    private String terminalId;
    private LocalDateTime timestamp;
    private String status;
    private String message;
    
    public EchoResponse(String terminalId, LocalDateTime timestamp, String status, String message) {
        this.terminalId = terminalId;
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }
    
    public String getTerminalId() {
        return terminalId;
    }
    
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}