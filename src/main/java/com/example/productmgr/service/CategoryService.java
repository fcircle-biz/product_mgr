package com.example.productmgr.service;

import com.example.productmgr.model.Category;
import com.example.productmgr.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    
    public List<Category> findAll() {
        return (List<Category>) categoryRepository.findAll();
    }
    
    public List<Category> findAllSorted() {
        return categoryRepository.findAllByOrderByNameAsc();
    }
    
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }
    
    @Transactional
    public Category save(Category category) {
        if (category.getId() == null) {
            // 新規カテゴリ
            if (categoryRepository.existsByName(category.getName())) {
                throw new RuntimeException("同名のカテゴリが既に存在します");
            }
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
        } else {
            // 既存カテゴリの更新
            Category existingCategory = categoryRepository.findById(category.getId())
                    .orElseThrow(() -> new RuntimeException("更新対象のカテゴリが見つかりません"));
            
            // 名前が変更されていて、その名前が他のカテゴリで使用されている場合はエラー
            if (!existingCategory.getName().equals(category.getName()) 
                    && categoryRepository.existsByName(category.getName())) {
                throw new RuntimeException("同名のカテゴリが既に存在します");
            }
            
            category.setCreatedAt(existingCategory.getCreatedAt());
            category.setUpdatedAt(LocalDateTime.now());
        }
        
        return categoryRepository.save(category);
    }
    
    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }
}