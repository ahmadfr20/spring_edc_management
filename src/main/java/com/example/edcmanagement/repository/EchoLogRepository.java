package com.example.edcmanagement.repository;

import com.example.edcmanagement.entity.EchoLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EchoLogRepository extends JpaRepository<EchoLog, Long> {
    
    List<EchoLog> findByTerminalIdOrderByRequestTimestampDesc(String terminalId);
    
    Page<EchoLog> findByTerminalIdOrderByRequestTimestampDesc(String terminalId, Pageable pageable);
    
    List<EchoLog> findByRequestTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT e FROM EchoLog e WHERE e.terminalId = :terminalId " +
           "AND e.requestTimestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY e.requestTimestamp DESC")
    List<EchoLog> findByTerminalIdAndTimestampRange(@Param("terminalId") String terminalId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);
    
    long countByTerminalId(String terminalId);
    
    long countBySignatureValid(Boolean signatureValid);
}