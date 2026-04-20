package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;

@Controller
public class LoginController {

    private final CustomerRepository repo;

    public LoginController(CustomerRepository repo) {
        this.repo = repo;
    }

    // Mostrar formulario
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }
    

    
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                               @RequestParam String password,
                               Model model) {

        Customer user = repo.findByEmailAndPassword(email, password);

        if (user != null) {
            return "redirect:/home"; 
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }
    }
}