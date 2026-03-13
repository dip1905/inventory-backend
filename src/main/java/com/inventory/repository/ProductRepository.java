package com.inventory.repository;

import com.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Search by name or description
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String name, String description);

    // Filter by category
    List<Product> findByCategoryId(Long categoryId);

    // Low stock products
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.lowStockThreshold")
    List<Product> findLowStockProducts();

    // Dashboard stats
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity <= p.lowStockThreshold")
    Long countLowStockProducts();

    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Product p")
    Double getTotalInventoryValue();
}
