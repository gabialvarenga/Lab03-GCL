package com.labGCL03.moeda_estudantil.repositories;

import com.labGCL03.moeda_estudantil.entities.Transaction;
import com.labGCL03.moeda_estudantil.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByReceiverIdOrderByDateDesc(Long receiverId);
    
    List<Transaction> findBySenderIdOrderByDateDesc(Long senderId);
    
    List<Transaction> findByTypeOrderByDateDesc(TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.receiver.id = :userId OR t.sender.id = :userId ORDER BY t.date DESC")
    List<Transaction> findAllTransactionsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE (t.receiver.id = :userId OR t.sender.id = :userId) " +
           "AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    List<Transaction> findTransactionsByUserIdAndDateRange(@Param("userId") Long userId,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.receiver.id = :userId AND t.type = 'RECEIVED'")
    Long getTotalReceivedByUser(@Param("userId") Long userId);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.sender.id = :userId AND t.type = 'SENT'")
    Long getTotalSentByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.type = :type AND t.date BETWEEN :startDate AND :endDate")
    Long countTransactionsByTypeAndDateRange(@Param("type") TransactionType type,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.sender.id IN " +
           "(SELECT te.id FROM Teacher te WHERE te.institution.id = :institutionId) " +
           "ORDER BY t.date DESC")
    List<Transaction> findTransactionsByInstitution(@Param("institutionId") Long institutionId);
}