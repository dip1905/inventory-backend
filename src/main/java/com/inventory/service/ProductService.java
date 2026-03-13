package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.model.Tag;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SupplierRepository;
import com.inventory.repository.TagRepository;
import com.inventory.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private TagRepository tagRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        if (product.getSupplier() != null && product.getSupplier().getId() != null) {
            supplierRepository.findById(product.getSupplier().getId())
                .ifPresent(product::setSupplier);
        }
        if (product.getTags() != null && !product.getTags().isEmpty()) {
            List<Tag> tags = new ArrayList<>();
            for (Tag t : product.getTags()) {
                tagRepository.findById(t.getId()).ifPresent(tags::add);
            }
            product.setTags(tags);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());
        product.setLowStockThreshold(productDetails.getLowStockThreshold());
        product.setCategory(productDetails.getCategory());

        if (productDetails.getSupplier() != null &&
            productDetails.getSupplier().getId() != null) {
            supplierRepository.findById(productDetails.getSupplier().getId())
                .ifPresent(product::setSupplier);
        } else {
            product.setSupplier(null);
        }

        if (productDetails.getTags() != null) {
            List<Tag> tags = new ArrayList<>();
            for (Tag t : productDetails.getTags()) {
                tagRepository.findById(t.getId()).ifPresent(tags::add);
            }
            product.setTags(tags);
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository
            .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    public List<Product> filterByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Map<String, Object> getDashboardStats() {
        List<Product> products = productRepository.findAll();

        long totalCategories = products.stream()
            .filter(p -> p.getCategory() != null)
            .map(p -> p.getCategory().getId())
            .distinct()
            .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", products.size());
        stats.put("totalCategories", totalCategories);
        stats.put("lowStockCount", productRepository.countLowStockProducts());
        stats.put("totalInventoryValue", productRepository.getTotalInventoryValue());
        return stats;
    }
}