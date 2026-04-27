package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class CheckoutController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepo;

    // PASO 1: Carrito Inicial
    @GetMapping("/checkout")
    public String verCarrito(HttpSession session, Model model) {
        Customer customer = getCustomerOrNull(session);
        if (customer == null)
            return "redirect:/login?redirect=/checkout";

        List<CartItem> productos = getItemsForCustomer(customer);
        model.addAttribute("productos", productos);
        model.addAttribute("total", calculateTotal(productos));

        return "carrito_checkout";
    }

    // PASO 2: Formulario de Dirección
    @GetMapping("/checkout/direccion")
    public String verDireccion(HttpSession session, Model model) {
        Customer customer = getCustomerOrNull(session);
        if (customer == null)
            return "redirect:/login?redirect=/checkout/direccion";

        List<CartItem> productos = getItemsForCustomer(customer);
        model.addAttribute("productos", productos);
        model.addAttribute("total", calculateTotal(productos));

        Address direccion = addressRepository.findFirstByCustomerId(customer.getCustomerId())
                .orElse(new Address());

        model.addAttribute("direccion", direccion);
        model.addAttribute("estados", stateRepository.findByActiveTrue());

        return "checkout_direccion";
    }

    @PostMapping("/checkout/direccion")
    public String guardarDireccion(@RequestParam String street,
            @RequestParam String city,
            @RequestParam Integer stateId,
            @RequestParam String zipCode,
            HttpSession session) {
        Customer customer = getCustomerOrNull(session);
        if (customer == null)
            return "redirect:/login";

        Address direccion = addressRepository.findFirstByCustomerId(customer.getCustomerId())
                .orElse(new Address());

        direccion.setCustomerId(customer.getCustomerId());
        direccion.setStreet(street);
        direccion.setCity(city);
        direccion.setZipCode(zipCode);

        State state = stateRepository.findById(stateId).orElse(null);
        direccion.setState(state);

        Address savedAddress = addressRepository.save(direccion);
        session.setAttribute("checkoutAddressId", savedAddress.getAddressId());

        return "redirect:/checkout/pagar";
    }

    // PASO 3: Subida de Comprobante
    @GetMapping("/checkout/pagar")
    public String verPago(HttpSession session, Model model) {
        Customer customer = getCustomerOrNull(session);
        if (customer == null)
            return "redirect:/login";

        List<CartItem> productos = getItemsForCustomer(customer);
        model.addAttribute("productos", productos);
        model.addAttribute("total", calculateTotal(productos));

        if (session.getAttribute("checkoutAddressId") == null)
            return "redirect:/checkout/direccion";

        return "checkout_pagar";
    }

    @PostMapping("/checkout/pagar")
    @Transactional
    public String procesarPago(@RequestParam("comprobante") MultipartFile comprobante,
            HttpSession session,
            RedirectAttributes ra) throws IOException {
        Customer customer = getCustomerOrNull(session);
        if (customer == null)
            return "redirect:/login";

        Integer addressId = (Integer) session.getAttribute("checkoutAddressId");
        if (addressId == null)
            return "redirect:/checkout/direccion";

        List<CartItem> productos = getItemsForCustomer(customer);
        if (productos.isEmpty())
            return "redirect:/comprar";

        // 1. Guardar archivo
        String nombreFinal = "";
        if (!comprobante.isEmpty()) {
            String folder = System.getProperty("user.dir") + "/uploads/comprobantes/";
            Files.createDirectories(Paths.get(folder));
            nombreFinal = UUID.randomUUID() + "_" + comprobante.getOriginalFilename();
            Path path = Paths.get(folder + nombreFinal);
            Files.copy(comprobante.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        // 2. Crear Orden
        Order order = new Order();
        order.setCustomerId(customer.getCustomerId());
        order.setAddressId(addressId);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("PENDIENTE");
        order.setComprobanteUrl(nombreFinal);
        Order savedOrder = orderRepository.save(order);

        // 3. Mover items de Carrito a Orden
        for (CartItem item : productos) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getOrderId());
            // Seteamos el objeto completo para que JPA gestione la FK product_id
            orderItem.setProduct(item.getProductVariant().getProduct());
            // Seteamos la variante para tener el detalle exacto (Menta, Carbón, etc.)
            orderItem.setProductVariant(item.getProductVariant());
            
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getPriceAtAdd());
            orderItem.setDiscountAmount(BigDecimal.ZERO);
            orderItemRepository.save(orderItem);
        }

        // 4. Vaciar Carrito en la DB
        cartItemRepository.deleteByCustomerId(customer.getCustomerId());

        session.removeAttribute("checkoutAddressId");
        ra.addFlashAttribute("successMessage", "¡Pedido finalizado con éxito! Validaremos tu pago pronto.");
        return "redirect:/perfil";
    }

    private Customer getCustomerOrNull(HttpSession session) {
        return (Customer) session.getAttribute("loggedInCustomer");
    }

    private BigDecimal calculateTotal(List<CartItem> items) {
        return items.stream()
                .map(item -> item.getPriceAtAdd().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Helper robusto para obtener productos
    private List<CartItem> getItemsForCustomer(Customer customer) {
        List<CartItem> items = cartItemRepository.findByCustomerId(customer.getCustomerId());
        if (items.isEmpty()) {
            cartRepo.findByCustomerId(customer.getCustomerId()).ifPresent(cart -> {
                items.addAll(cartItemRepository.findByCart(cart));
            });
        }
        return items;
    }
}