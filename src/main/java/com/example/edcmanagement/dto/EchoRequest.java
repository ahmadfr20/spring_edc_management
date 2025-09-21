package com.example.edcmanagement.dto;

import javax.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import java.time.LocalDateTime;

public class EchoRequest {
    @NotBlank(message = "Terminal ID is required")
    private String terminalId;
    
    public EchoRequest() {}
    
    public String getTerminalId() {
        return terminalId;
    }
    
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}