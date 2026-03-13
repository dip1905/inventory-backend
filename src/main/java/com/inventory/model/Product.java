package com.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;

    private Integer lowStockThreshold = 5;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

//    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Transient
    public boolean isLowStock() {
        return this.quantity <= this.lowStockThreshold;
    }
}