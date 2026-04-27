package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Customer;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByEmailAndPassword(String email, String password);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByResetToken(String resetToken);

}