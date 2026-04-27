package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ForgotPasswordController {

    @Autowired
    private CustomerRepository customerRepo;

    // Pantalla inicial para pedir el correo
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }

    // Paso 1: Verificar el correo
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, Model model) {
        Optional<Customer> customerOpt = customerRepo.findByEmail(email);

        if (customerOpt.isPresent()) {
            // Guardamos el email en la sesión para el siguiente paso
            session.setAttribute("emailParaReset", email);
            return "redirect:/reset-password";
        } else {
            model.addAttribute("error", "No existe ninguna cuenta asociada a ese correo.");
            return "forgot_password";
        }
    }

    // Pantalla para ingresar la nueva contraseña
    @GetMapping("/reset-password")
    public String showResetPasswordForm(HttpSession session) {
        // Si el usuario llega aquí sin haber pasado por forgot-password, lo regresamos
        if (session.getAttribute("emailParaReset") == null) {
            return "redirect:/forgot-password";
        }
        return "reset_password";
    }

    // Paso 2: Actualizar la contraseña en la base de datos
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String password,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes ra,
            Model model) {

        String email = (String) session.getAttribute("emailParaReset");

        if (email == null) {
            return "redirect:/forgot-password";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "reset_password";
        }

        Optional<Customer> customerOpt = customerRepo.findByEmail(email);

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            // Guardamos la nueva contraseña (texto plano según tu lógica actual)
            customer.setPassword(password);
            customerRepo.save(customer);

            // Limpiamos la sesión después del cambio
            session.removeAttribute("emailParaReset");

            ra.addFlashAttribute("success", "¡Tu contraseña ha sido actualizada!");
            return "redirect:/login";
        } else {
            return "redirect:/forgot-password";
        }
    }
}