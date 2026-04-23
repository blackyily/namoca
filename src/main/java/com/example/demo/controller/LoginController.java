package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;

@Controller
public class LoginController {

    private final CustomerRepository repo;

    public LoginController(CustomerRepository repo) {
        this.repo = repo;
    }

    // Mostrar formulario de login
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "login";
    }
    
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String redirect,
                               HttpSession session,
                               Model model) {

        Customer user = repo.findByEmailAndPassword(email, password);

        if (user != null) {
            session.setAttribute("loggedInCustomer", user);
            if (redirect != null && !redirect.isEmpty()) {
                return "redirect:" + redirect;
            }
            return "redirect:/home"; 
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            model.addAttribute("redirect", redirect);
            return "login";
        }
    }

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarRegistro(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "registro";
    }

    // Procesar formulario de registro
    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String firstName,
                                   @RequestParam String lastName,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam String confirmPassword,
                                   @RequestParam(required = false) String redirect,
                                   Model model) {
        
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            model.addAttribute("redirect", redirect);
            return "registro";
        }

        // Simplemente guardar sin validación de duplicados para esta demostración
        // Aunque se podría mejorar buscando por correo antes de guardar.
        
        Customer newCustomer = new Customer();
        newCustomer.setFirstName(firstName);
        newCustomer.setLastName(lastName);
        newCustomer.setEmail(email);
        newCustomer.setPassword(password);
        
        repo.save(newCustomer);

        String redirectUrl = "/login?registrado=true";
        if (redirect != null && !redirect.isEmpty()) {
            redirectUrl += "&redirect=" + redirect;
        }
        return "redirect:" + redirectUrl;
    }
}