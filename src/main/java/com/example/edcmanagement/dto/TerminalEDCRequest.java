package com.example.edcmanagement.dto;

import javax.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import java.time.LocalDateTime;

public class TerminalEDCRequest {
    @NotBlank(message = "Terminal ID is required")
    private String terminalId;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String merchantName;
    private String ipAddress;
    private Integer port;
    
    // Constructors
    public TerminalEDCRequest() {}
    
    // Getters and Setters
    public String getTerminalId() {
        return terminalId;
    }
    
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMerchantName() {
        return merchantName;
    }
    
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
}