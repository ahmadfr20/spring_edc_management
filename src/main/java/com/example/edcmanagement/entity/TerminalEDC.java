package com.example.edcmanagement.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "terminal_edc")
public class TerminalEDC {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "terminal_id", unique = true, nullable = false)
    @NotBlank(message = "Terminal ID is required")
    private String terminalId;
    
    @Column(name = "location", nullable = false)
    @NotBlank(message = "Location is required")
    private String location;
    
    @Column(name = "status", nullable = false)
    @NotBlank(message = "Status is required")
    private String status;
    
    @Column(name = "merchant_name")
    private String merchantName;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "port")
    private Integer port;
    
    @Column(name = "created_at", nullable = false)
    @NotNull
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_ping")
    private LocalDateTime lastPing;
    
    // Constructors
    public TerminalEDC() {
        this.createdAt = LocalDateTime.now();
        this.status = "INACTIVE";
    }
    
    public TerminalEDC(String terminalId, String location, String status) {
        this();
        this.terminalId = terminalId;
        this.location = location;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getLastPing() {
        return lastPing;
    }
    
    public void setLastPing(LocalDateTime lastPing) {
        this.lastPing = lastPing;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}