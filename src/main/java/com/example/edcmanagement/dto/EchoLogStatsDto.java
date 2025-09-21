package com.example.edcmanagement.dto;

public class EchoLogStatsDto {
    private long totalLogs;
    private long validSignatures;
    private long invalidSignatures;
    private double validSignatureRate;

    public EchoLogStatsDto(long totalLogs, long validSignatures, long invalidSignatures) {
        this.totalLogs = totalLogs;
        this.validSignatures = validSignatures;
        this.invalidSignatures = invalidSignatures;
        this.validSignatureRate = totalLogs > 0 ? 
            (double) validSignatures / totalLogs * 100 : 0.0;
    }

    public long getTotalLogs() { return totalLogs; }
    public long getValidSignatures() { return validSignatures; }
    public long getInvalidSignatures() { return invalidSignatures; }
    public double getValidSignatureRate() { return validSignatureRate; }
}
