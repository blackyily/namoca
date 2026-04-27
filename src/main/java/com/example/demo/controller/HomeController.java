package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.example.demo.model.Customer;
import com.example.demo.model.VolunteerApplication;
import com.example.demo.repository.VolunteerApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/donar")
    public String donar() {
        return "donar";
    }

    @GetMapping("/quienesSomos")
    public String quienesSomos(HttpSession session, Model model) {
        // Pasamos el customer a la vista para que Thymeleaf pueda mostrar/ocultar
        // elementos
        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        model.addAttribute("loggedInCustomer", loggedInCustomer);
        return "quienesSomos";
    }

    @PostMapping("/quienesSomos/aplicar")
    public String aplicarVoluntariado(@RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("edad") Integer edad,
            @RequestParam("genero") String genero,
            @RequestParam("carrera") String carrera,
            @RequestParam("universidad") String universidad,
            @RequestParam("semestre") String semestre,
            @RequestParam("habilidades") String habilidades,
            @RequestParam("experiencia") String experiencia,
            @RequestParam("disponibilidad") String disponibilidad,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Customer loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        if (loggedInCustomer == null) {
            // Not logged in, redirect to login page and store the intended destination
            return "redirect:/login?redirect=/quienesomos";
        }

        VolunteerApplication application = new VolunteerApplication();
        application.setCustomer(loggedInCustomer);
        application.setNombreCompleto(nombreCompleto);
        application.setEdad(edad);
        application.setGenero(genero);
        application.setCarrera(carrera);
        application.setUniversidad(universidad);
        application.setSemestre(semestre);
        application.setHabilidades(habilidades);
        application.setExperiencia(experiencia);
        application.setDisponibilidad(disponibilidad);

        volunteerApplicationRepository.save(application);

        redirectAttributes.addFlashAttribute("successMessage", "Se mandaron tus datos correctamente");
        return "redirect:/quienesSomos";
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "carrito";
    }

}