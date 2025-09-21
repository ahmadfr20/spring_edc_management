package com.example.edcmanagement.repository;

import com.example.edcmanagement.entity.TerminalEDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerminalEDCRepository extends JpaRepository<TerminalEDC, Long> {
    
    Optional<TerminalEDC> findByTerminalId(String terminalId);
    
    List<TerminalEDC> findByStatus(String status);
    
    List<TerminalEDC> findByLocationContainingIgnoreCase(String location);

    // Gantikan query manual → otomatis generate LIKE %location% dengan case-insensitive
    List<TerminalEDC> findByStatusAndLocationContainingIgnoreCase(String status, String location);
    
    Page<TerminalEDC> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    boolean existsByTerminalId(String terminalId);
}
