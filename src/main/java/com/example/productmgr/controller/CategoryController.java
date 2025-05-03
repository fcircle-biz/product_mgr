package com.example.productmgr.controller;

import com.example.productmgr.model.Category;
import com.example.productmgr.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAllSorted());
        return "category/list";
    }
    
    @GetMapping("/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/create";
    }
    
    @PostMapping
    public String createCategory(@Valid Category category,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "category/create";
        }
        
        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("message", "カテゴリを登録しました");
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "category/create";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("カテゴリが見つかりません"));
        
        model.addAttribute("category", category);
        return "category/edit";
    }
    
    @PostMapping("/{id}")
    public String updateCategory(@PathVariable Long id,
                                @Valid Category category,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "category/edit";
        }
        
        try {
            category.setId(id);
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("message", "カテゴリを更新しました");
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "category/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "カテゴリを削除しました");
            return "redirect:/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/categories";
        }
    }
}