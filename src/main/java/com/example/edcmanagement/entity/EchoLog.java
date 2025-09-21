package com.example.edcmanagement.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "echo_log")
public class EchoLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "terminal_id", nullable = false)
    @NotBlank(message = "Terminal ID is required")
    private String terminalId;
    
    @Column(name = "request_timestamp", nullable = false)
    @NotNull
    private LocalDateTime requestTimestamp;
    
    @Column(name = "response_status")
    private String responseStatus;
    
    @Column(name = "client_ip")
    private String clientIp;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "signature_valid")
    private Boolean signatureValid;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    // Constructors
    public EchoLog() {
        this.requestTimestamp = LocalDateTime.now();
    }
    
    public EchoLog(String terminalId) {
        this();
        this.terminalId = terminalId;
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
    
    public LocalDateTime getRequestTimestamp() {
        return requestTimestamp;
    }
    
    public void setRequestTimestamp(LocalDateTime requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }
    
    public String getResponseStatus() {
        return responseStatus;
    }
    
    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public Boolean getSignatureValid() {
        return signatureValid;
    }
    
    public void setSignatureValid(Boolean signatureValid) {
        this.signatureValid = signatureValid;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}