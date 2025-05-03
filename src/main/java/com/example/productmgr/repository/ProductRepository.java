package com.example.productmgr.repository;

import com.example.productmgr.model.Product;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    
    @Query("SELECT p.*, c.id as c_id, c.name as c_name, c.description as c_description " +
           "FROM products p " +
           "JOIN categories c ON p.category_id = c.id " +
           "WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);
    
    @Query("SELECT p.*, c.id as c_id, c.name as c_name, c.description as c_description " +
           "FROM products p " +
           "JOIN categories c ON p.category_id = c.id " +
           "ORDER BY p.id")
    List<Product> findAllWithCategory();
    
    @Query("SELECT p.*, c.id as c_id, c.name as c_name, c.description as c_description " +
           "FROM products p " +
           "JOIN categories c ON p.category_id = c.id " +
           "WHERE p.name LIKE CONCAT('%', :keyword, '%') " +
           "   OR p.jan_code LIKE CONCAT('%', :keyword, '%') " +
           "ORDER BY p.id")
    List<Product> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT p.*, c.id as c_id, c.name as c_name, c.description as c_description " +
           "FROM products p " +
           "JOIN categories c ON p.category_id = c.id " +
           "WHERE c.id = :categoryId " +
           "ORDER BY p.id")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT p.*, c.id as c_id, c.name as c_name, c.description as c_description " +
           "FROM products p " +
           "JOIN categories c ON p.category_id = c.id " +
           "WHERE p.status = :status " +
           "ORDER BY p.id")
    List<Product> findByStatus(@Param("status") String status);
    
    @Query("SELECT p.*, c.id as c_id, c.name as c_name, c.description as c_description " +
           "FROM products p " +
           "JOIN categories c ON p.category_id = c.id " +
           "WHERE p.stock_quantity = 0 " +
           "ORDER BY p.id")
    List<Product> findOutOfStock();
    
    @Query("SELECT p.*, c.id as c_id, c.name as c_name, c.description as c_description " +
           "FROM products p " +
           "JOIN categories c ON p.category_id = c.id " +
           "WHERE p.stock_quantity > 0 AND p.stock_quantity <= 10 " +
           "ORDER BY p.id")
    List<Product> findLowStock();
    
    boolean existsByJanCode(String janCode);
}