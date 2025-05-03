package com.example.productmgr.repository;

import com.example.productmgr.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    
    List<Category> findAllByOrderByNameAsc();
    
    boolean existsByName(String name);
}