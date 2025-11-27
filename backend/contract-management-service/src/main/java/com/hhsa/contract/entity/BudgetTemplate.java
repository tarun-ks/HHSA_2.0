package com.hhsa.contract.entity;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Budget Template entity.
 * Master list of available budget templates/categories.
 */
@Entity
@Table(name = "budget_templates", indexes = {
    @Index(name = "idx_budget_templates_name", columnList = "name")
})
public class BudgetTemplate extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

