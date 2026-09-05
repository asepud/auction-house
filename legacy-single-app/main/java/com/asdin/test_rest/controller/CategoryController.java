package com.asdin.test_rest.controller;

import com.asdin.test_rest.domain.Category;
import com.asdin.test_rest.repository.CategoryRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/** Public category catalogue with administrator creation endpoint. */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepository categories;

    public CategoryController(CategoryRepository categories) {
        this.categories = categories;
    }

    @GetMapping
    List<Category> all() {
        return categories.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Category> create(@RequestBody CategoryRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categories.save(Category.builder().name(r.name.trim()).build()));
    }

    @Data
    @NoArgsConstructor
    static class CategoryRequest {
        @NotBlank
        private String name;
    }
}
