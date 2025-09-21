package com.example.edcmanagement.controller;

import com.example.edcmanagement.dto.ApiResponse;
import com.example.edcmanagement.dto.TerminalEDCRequest;
import com.example.edcmanagement.entity.TerminalEDC;
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

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/edc/terminals")
@Validated
public class TerminalEDCController {
    
    private static final Logger logger = LoggerFactory.getLogger(TerminalEDCController.class);
    
    @Autowired
    private TerminalEDCService terminalService;

    @PostMapping
    public ResponseEntity<ApiResponse<TerminalEDC>> createTerminal(@Valid @RequestBody TerminalEDCRequest request) {
        try {
            logger.info("Creating new terminal with ID: {}", request.getTerminalId());
            
            TerminalEDC terminal = new TerminalEDC();
            terminal.setTerminalId(request.getTerminalId());
            terminal.setLocation(request.getLocation());
            terminal.setStatus(request.getStatus());
            terminal.setMerchantName(request.getMerchantName());
            terminal.setIpAddress(request.getIpAddress());
            terminal.setPort(request.getPort());
            
            TerminalEDC createdTerminal = terminalService.createTerminal(terminal);
            
            ApiResponse<TerminalEDC> response = ApiResponse.success(
                "Terminal created successfully", createdTerminal);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            logger.error("Error creating terminal: {}", e.getMessage());
            ApiResponse<TerminalEDC> response = ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error creating terminal", e);
            ApiResponse<TerminalEDC> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TerminalEDC>>> getAllTerminals(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<TerminalEDC> terminals = terminalService.getAllTerminals(pageable);
            
            ApiResponse<Page<TerminalEDC>> response = ApiResponse.success(
                "Terminals retrieved successfully", terminals);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving terminals", e);
            ApiResponse<Page<TerminalEDC>> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TerminalEDC>> getTerminalById(@PathVariable Long id) {
        try {
            Optional<TerminalEDC> terminal = terminalService.getTerminalById(id);
            
            if (terminal.isPresent()) {
                ApiResponse<TerminalEDC> response = ApiResponse.success(
                    "Terminal found", terminal.get());
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<TerminalEDC> response = ApiResponse.error(
                    "Terminal not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving terminal by ID: {}", id, e);
            ApiResponse<TerminalEDC> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/terminal/{terminalId}")
    public ResponseEntity<ApiResponse<TerminalEDC>> getTerminalByTerminalId(@PathVariable String terminalId) {
        try {
            Optional<TerminalEDC> terminal = terminalService.getTerminalByTerminalId(terminalId);
            
            if (terminal.isPresent()) {
                ApiResponse<TerminalEDC> response = ApiResponse.success(
                    "Terminal found", terminal.get());
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<TerminalEDC> response = ApiResponse.error(
                    "Terminal not found with terminal ID: " + terminalId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving terminal by terminal ID: {}", terminalId, e);
            ApiResponse<TerminalEDC> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TerminalEDC>> updateTerminal(
            @PathVariable Long id, 
            @Valid @RequestBody TerminalEDCRequest request) {
        
        try {
            TerminalEDC terminalDetails = new TerminalEDC();
            terminalDetails.setTerminalId(request.getTerminalId());
            terminalDetails.setLocation(request.getLocation());
            terminalDetails.setStatus(request.getStatus());
            terminalDetails.setMerchantName(request.getMerchantName());
            terminalDetails.setIpAddress(request.getIpAddress());
            terminalDetails.setPort(request.getPort());
            
            TerminalEDC updatedTerminal = terminalService.updateTerminal(id, terminalDetails);
            
            ApiResponse<TerminalEDC> response = ApiResponse.success(
                "Terminal updated successfully", updatedTerminal);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error updating terminal: {}", e.getMessage());
            ApiResponse<TerminalEDC> response = ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error updating terminal", e);
            ApiResponse<TerminalEDC> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTerminal(@PathVariable Long id) {
        try {
            terminalService.deleteTerminal(id);
            
            ApiResponse<Void> response = ApiResponse.success("Terminal deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error deleting terminal: {}", e.getMessage());
            ApiResponse<Void> response = ApiResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error deleting terminal", e);
            ApiResponse<Void> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TerminalEDC>>> getTerminalsByStatus(@PathVariable String status) {
        try {
            List<TerminalEDC> terminals = terminalService.getTerminalsByStatus(status);
            
            ApiResponse<List<TerminalEDC>> response = ApiResponse.success(
                "Terminals retrieved successfully", terminals);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error retrieving terminals by status: {}", status, e);
            ApiResponse<List<TerminalEDC>> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TerminalEDC>>> searchTerminalsByLocation(
            @RequestParam String location) {
        
        try {
            List<TerminalEDC> terminals = terminalService.searchTerminalsByLocation(location);
            
            ApiResponse<List<TerminalEDC>> response = ApiResponse.success(
                "Terminals retrieved successfully", terminals);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error searching terminals by location: {}", location, e);
            ApiResponse<List<TerminalEDC>> response = ApiResponse.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}