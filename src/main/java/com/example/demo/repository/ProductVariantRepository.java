package com.example.demo.repository;

import com.example.demo.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

    /**
     * Trae todas las variantes junto con su producto padre e imágenes
     * en una sola consulta SQL (evita el problema N+1 de lazy loading).
     */
    @Query("SELECT DISTINCT pv FROM ProductVariant pv " +
           "JOIN FETCH pv.product p " +
           "LEFT JOIN FETCH p.images")
    List<ProductVariant> findAllWithProductAndImages();
}
