package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.repository.ProductVariantRepository;
import com.example.demo.model.ProductVariant;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @GetMapping("/comprar")
    public String comprar(Model model) {
        // Traemos las variantes con su producto e imágenes en un solo query
        List<ProductVariant> variantes = productVariantRepository.findAllWithProductAndImages();
        model.addAttribute("variantes", variantes);
        return "comprar";
    }
}
