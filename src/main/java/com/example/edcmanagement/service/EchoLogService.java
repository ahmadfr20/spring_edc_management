package com.example.edcmanagement.service;

import com.example.edcmanagement.entity.EchoLog;
import com.example.edcmanagement.repository.EchoLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EchoLogService {
    
    private static final Logger logger = LoggerFactory.getLogger(EchoLogService.class);
    
    @Autowired
    private EchoLogRepository echoLogRepository;

    public EchoLog saveEchoLog(EchoLog echoLog) {
        logger.info("Saving echo log for terminal: {}", echoLog.getTerminalId());
        
        if (echoLog.getRequestTimestamp() == null) {
            echoLog.setRequestTimestamp(LocalDateTime.now());
        }
        
        EchoLog savedLog = echoLogRepository.save(echoLog);
        logger.debug("Echo log saved with ID: {}", savedLog.getId());
        
        return savedLog;
    }
    
    public EchoLog createEchoLog(String terminalId, String clientIp, String userAgent, 
                                Boolean signatureValid, String responseStatus, String errorMessage) {
        
        EchoLog echoLog = new EchoLog(terminalId);
        echoLog.setClientIp(clientIp);
        echoLog.setUserAgent(userAgent);
        echoLog.setSignatureValid(signatureValid);
        echoLog.setResponseStatus(responseStatus);
        echoLog.setErrorMessage(errorMessage);
        
        return saveEchoLog(echoLog);
    }
    
    @Transactional(readOnly = true)
    public List<EchoLog> getAllEchoLogs() {
        logger.debug("Retrieving all echo logs");
        return echoLogRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<EchoLog> getAllEchoLogs(Pageable pageable) {
        logger.debug("Retrieving echo logs with pagination");
        return echoLogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<EchoLog> getEchoLogsByTerminalId(String terminalId) {
        logger.debug("Retrieving echo logs for terminal: {}", terminalId);
        return echoLogRepository.findByTerminalIdOrderByRequestTimestampDesc(terminalId);
    }
    

    @Transactional(readOnly = true)
    public Page<EchoLog> getEchoLogsByTerminalId(String terminalId, Pageable pageable) {
        logger.debug("Retrieving echo logs for terminal with pagination: {}", terminalId);
        return echoLogRepository.findByTerminalIdOrderByRequestTimestampDesc(terminalId, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<EchoLog> getEchoLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Retrieving echo logs between {} and {}", startTime, endTime);
        return echoLogRepository.findByRequestTimestampBetween(startTime, endTime);
    }
    
    @Transactional(readOnly = true)
    public List<EchoLog> getEchoLogsByTerminalIdAndTimeRange(String terminalId, 
                                                           LocalDateTime startTime, 
                                                           LocalDateTime endTime) {
        logger.debug("Retrieving echo logs for terminal {} between {} and {}", 
                    terminalId, startTime, endTime);
        return echoLogRepository.findByTerminalIdAndTimestampRange(terminalId, startTime, endTime);
    }
    
    @Transactional(readOnly = true)
    public long getEchoLogCountByTerminalId(String terminalId) {
        logger.debug("Getting echo log count for terminal: {}", terminalId);
        return echoLogRepository.countByTerminalId(terminalId);
    }
    
    @Transactional(readOnly = true)
    public long getEchoLogCountBySignatureValid(Boolean signatureValid) {
        logger.debug("Getting echo log count by signature validity: {}", signatureValid);
        return echoLogRepository.countBySignatureValid(signatureValid);
    }
    
    @Transactional(readOnly = true)
    public List<EchoLog> getTodayEchoLogs() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        
        logger.debug("Retrieving today's echo logs");
        return getEchoLogsByTimeRange(startOfDay, endOfDay);
    }
    
    @Transactional(readOnly = true)
    public List<EchoLog> getTodayEchoLogsByTerminalId(String terminalId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        
        logger.debug("Retrieving today's echo logs for terminal: {}", terminalId);
        return getEchoLogsByTerminalIdAndTimeRange(terminalId, startOfDay, endOfDay);
    }
}