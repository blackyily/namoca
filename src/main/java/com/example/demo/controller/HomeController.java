package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.example.demo.model.Customer;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/quienesomos")
    public String quienesSomos() {
        return "quienesSomos";
    }

    @GetMapping("/comprar")
    public String comprar() {
        return "comprar";
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "carrito";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            // Not logged in, redirect to login page and store the intended destination
            return "redirect:/login?redirect=/checkout";
        }
        
        model.addAttribute("customer", loggedInCustomer);
        return "checkout";
    }
}