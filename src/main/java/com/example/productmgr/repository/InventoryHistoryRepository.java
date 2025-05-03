package com.example.productmgr.repository;

import com.example.productmgr.model.InventoryHistory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryHistoryRepository extends CrudRepository<InventoryHistory, Long> {

    @Query("SELECT ih.*, p.id as p_id, p.name as p_name " +
           "FROM inventory_history ih " +
           "JOIN products p ON ih.product_id = p.id " +
           "WHERE ih.product_id = :productId " +
           "ORDER BY ih.created_at DESC")
    List<InventoryHistory> findByProductId(@Param("productId") Long productId);
    
    @Query("SELECT ih.*, p.id as p_id, p.name as p_name, u.id as u_id, u.username as u_username, u.full_name as u_full_name " +
           "FROM inventory_history ih " +
           "JOIN products p ON ih.product_id = p.id " +
           "JOIN users u ON ih.user_id = u.id " +
           "WHERE ih.product_id = :productId " +
           "ORDER BY ih.created_at DESC " +
           "LIMIT :limit")
    List<InventoryHistory> findLatestByProductId(@Param("productId") Long productId, @Param("limit") int limit);
    
    @Query("SELECT ih.*, p.id as p_id, p.name as p_name, u.id as u_id, u.username as u_username, u.full_name as u_full_name " +
           "FROM inventory_history ih " +
           "JOIN products p ON ih.product_id = p.id " +
           "JOIN users u ON ih.user_id = u.id " +
           "WHERE ih.created_at BETWEEN :startDate AND :endDate " +
           "ORDER BY ih.created_at DESC")
    List<InventoryHistory> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ih.*, p.id as p_id, p.name as p_name, u.id as u_id, u.username as u_username, u.full_name as u_full_name " +
           "FROM inventory_history ih " +
           "JOIN products p ON ih.product_id = p.id " +
           "JOIN users u ON ih.user_id = u.id " +
           "WHERE ih.type = :type " +
           "ORDER BY ih.created_at DESC")
    List<InventoryHistory> findByType(@Param("type") String type);
}