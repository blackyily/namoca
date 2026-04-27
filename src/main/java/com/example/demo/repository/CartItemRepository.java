package com.example.demo.repository;

import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    // Este ya lo tenías
    List<CartItem> findByCustomerId(Long customerId);

    // NUEVO: Agrega esta línea para que el CheckoutController deje de marcar error
    List<CartItem> findByCart(Cart cart);

    // Este lo usamos en el CartController para no duplicar items
    Optional<CartItem> findByCartAndProductVariant(Cart cart, ProductVariant productVariant);

    @Transactional
    void deleteByCustomerId(Long customerId);
}