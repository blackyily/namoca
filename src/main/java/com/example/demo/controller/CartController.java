package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CartController {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private CartItemRepository cartItemRepo;

    @Autowired
    private ProductVariantRepository productVariantRepo;

    @PostMapping("/agregar")
    @ResponseBody
    public ResponseEntity<?> agregarAlCarrito(@RequestParam Integer variantId, 
                                            @RequestParam BigDecimal price,
                                            HttpSession session) {
        
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.status(401).body("Debe iniciar sesión para agregar productos");
        }

        // DEBUG solicitado para consola
        System.out.println("DEBUG: Agregando variante " + variantId + " al cliente " + customer.getCustomerId());

        // 1. Verificar/Crear Cart para el customer
        Cart cart = cartRepo.findByCustomerId(customer.getCustomerId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(customer.getCustomerId());
                    Cart savedCart = cartRepo.save(newCart);
                    System.out.println("DEBUG: Nuevo Cart creado con ID: " + savedCart.getCartId());
                    return savedCart;
                });

        // 2. Buscar si el producto ya está en el carrito
        ProductVariant variant = productVariantRepo.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Optional<CartItem> existingItem = cartItemRepo.findByCartAndProductVariant(cart, variant);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepo.save(item);
            System.out.println("DEBUG: Cantidad incrementada para variantId " + variantId);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setCustomerId(customer.getCustomerId());
            newItem.setProductVariant(variant);
            newItem.setProductId(variant.getProduct().getProductId()); // Asignamos el productId requerido
            newItem.setQuantity(1);
            newItem.setPriceAtAdd(price);
            cartItemRepo.save(newItem);
            System.out.println("DEBUG: Nuevo CartItem guardado en DB (Product ID: " + variant.getProduct().getProductId() + ")");
        }

        return ResponseEntity.ok("Producto agregado correctamente");
    }

    @PostMapping("/vaciar")
    public String vaciarCarrito(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer != null) {
            cartItemRepo.deleteByCustomerId(customer.getCustomerId());
        }
        return "redirect:/checkout";
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Integer> getCartCount(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer == null) {
            return ResponseEntity.ok(0);
        }
        
        // Buscamos items asociados al Cart o al CustomerId
        Integer count = getItemsForCustomer(customer).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        
        return ResponseEntity.ok(count);
    }

    // Helper para obtener items de forma robusta
    private java.util.List<CartItem> getItemsForCustomer(Customer customer) {
        java.util.List<CartItem> items = cartItemRepo.findByCustomerId(customer.getCustomerId());
        if (items.isEmpty()) {
            cartRepo.findByCustomerId(customer.getCustomerId()).ifPresent(cart -> {
                items.addAll(cartItemRepo.findByCart(cart));
            });
        }
        return items;
    }
}
