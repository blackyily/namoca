package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @GetMapping("/perfil")
    public String showProfile(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        
        if (customer == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderRepo.findByCustomerIdOrderByOrderDateDesc(customer.getCustomerId());
        
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orders);
        
        return "perfil";
    }

    @GetMapping("/perfil/pedido/{id}")
    public String showOrderDetail(@PathVariable Integer id, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        
        if (customer == null) {
            return "redirect:/login";
        }

        Order order = orderRepo.findById(id).orElse(null);
        
        // Seguridad: Verificar que el pedido pertenezca al cliente logueado
        if (order == null || !order.getCustomerId().equals(customer.getCustomerId())) {
            return "redirect:/perfil";
        }

        List<OrderItem> items = orderItemRepo.findByOrderId(id);
        
        // Calcular el total del pedido
        java.math.BigDecimal total = items.stream()
                .map(item -> item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("order", order);
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        
        return "order_detail";
    }
}
