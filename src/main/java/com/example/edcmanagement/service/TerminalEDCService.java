package com.example.edcmanagement.service;

import com.example.edcmanagement.entity.TerminalEDC;
import com.example.edcmanagement.repository.TerminalEDCRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TerminalEDCService {
    
    private static final Logger logger = LoggerFactory.getLogger(TerminalEDCService.class);
    
    @Autowired
    private TerminalEDCRepository terminalRepository;
    
    /**
     * Create a new terminal
     */
    public TerminalEDC createTerminal(TerminalEDC terminal) {
        logger.info("Creating new terminal with ID: {}", terminal.getTerminalId());
        
        if (terminalRepository.existsByTerminalId(terminal.getTerminalId())) {
            throw new RuntimeException("Terminal with ID " + terminal.getTerminalId() + " already exists");
        }
        
        terminal.setCreatedAt(LocalDateTime.now());
        TerminalEDC savedTerminal = terminalRepository.save(terminal);
        
        logger.info("Terminal created successfully with ID: {}", savedTerminal.getTerminalId());
        return savedTerminal;
    }
    
    /**
     * Get all terminals
     */
    @Transactional(readOnly = true)
    public List<TerminalEDC> getAllTerminals() {
        logger.debug("Retrieving all terminals");
        return terminalRepository.findAll();
    }
    
    /**
     * Get terminals with pagination
     */
    @Transactional(readOnly = true)
    public Page<TerminalEDC> getAllTerminals(Pageable pageable) {
        logger.debug("Retrieving terminals with pagination");
        return terminalRepository.findAll(pageable);
    }
    
    /**
     * Get terminal by ID
     */
    @Transactional(readOnly = true)
    public Optional<TerminalEDC> getTerminalById(Long id) {
        logger.debug("Retrieving terminal by ID: {}", id);
        return terminalRepository.findById(id);
    }
    
    /**
     * Get terminal by terminal ID
     */
    @Transactional(readOnly = true)
    public Optional<TerminalEDC> getTerminalByTerminalId(String terminalId) {
        logger.debug("Retrieving terminal by terminal ID: {}", terminalId);
        return terminalRepository.findByTerminalId(terminalId);
    }
    
    /**
     * Update terminal
     */
    public TerminalEDC updateTerminal(Long id, TerminalEDC terminalDetails) {
        logger.info("Updating terminal with ID: {}", id);
        
        TerminalEDC terminal = terminalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terminal not found with ID: " + id));
        
        // Check if terminalId is being changed and if new terminalId already exists
        if (!terminal.getTerminalId().equals(terminalDetails.getTerminalId())) {
            if (terminalRepository.existsByTerminalId(terminalDetails.getTerminalId())) {
                throw new RuntimeException("Terminal with ID " + terminalDetails.getTerminalId() + " already exists");
            }
        }
        
        terminal.setTerminalId(terminalDetails.getTerminalId());
        terminal.setLocation(terminalDetails.getLocation());
        terminal.setStatus(terminalDetails.getStatus());
        terminal.setMerchantName(terminalDetails.getMerchantName());
        terminal.setIpAddress(terminalDetails.getIpAddress());
        terminal.setPort(terminalDetails.getPort());
        terminal.setUpdatedAt(LocalDateTime.now());
        
        TerminalEDC updatedTerminal = terminalRepository.save(terminal);
        logger.info("Terminal updated successfully with ID: {}", updatedTerminal.getTerminalId());
        
        return updatedTerminal;
    }
    
    /**
     * Delete terminal by ID
     */
    public void deleteTerminal(Long id) {
        logger.info("Deleting terminal with ID: {}", id);
        
        TerminalEDC terminal = terminalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terminal not found with ID: " + id));
        
        terminalRepository.delete(terminal);
        logger.info("Terminal deleted successfully with ID: {}", terminal.getTerminalId());
    }
    
    /**
     * Get terminals by status
     */
    @Transactional(readOnly = true)
    public List<TerminalEDC> getTerminalsByStatus(String status) {
        logger.debug("Retrieving terminals by status: {}", status);
        return terminalRepository.findByStatus(status);
    }
    
    /**
     * Search terminals by location
     */
    @Transactional(readOnly = true)
    public List<TerminalEDC> searchTerminalsByLocation(String location) {
        logger.debug("Searching terminals by location: {}", location);
        return terminalRepository.findByLocationContainingIgnoreCase(location);
    }
    
    /**
     * Update terminal last ping time
     */
    public void updateLastPing(String terminalId) {
        logger.debug("Updating last ping for terminal: {}", terminalId);
        
        Optional<TerminalEDC> terminalOpt = terminalRepository.findByTerminalId(terminalId);
        if (terminalOpt.isPresent()) {
            TerminalEDC terminal = terminalOpt.get();
            terminal.setLastPing(LocalDateTime.now());
            terminal.setStatus("ACTIVE");
            terminalRepository.save(terminal);
            
            logger.debug("Last ping updated for terminal: {}", terminalId);
        } else {
            logger.warn("Terminal not found for ping update: {}", terminalId);
        }
    }
    
    /**
     * Check if terminal exists by terminal ID
     */
    @Transactional(readOnly = true)
    public boolean existsByTerminalId(String terminalId) {
        return terminalRepository.existsByTerminalId(terminalId);
    }
    
    /**
     * Get terminals by status with pagination
     */
    @Transactional(readOnly = true)
    public Page<TerminalEDC> getTerminalsByStatus(String status, Pageable pageable) {
        logger.debug("Retrieving terminals by status with pagination: {}", status);
        return terminalRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }
}