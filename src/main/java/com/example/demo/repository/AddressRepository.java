package com.example.demo.repository;

import com.example.demo.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    // Busca la primera dirección guardada para un customer dado
    Optional<Address> findFirstByCustomerId(Long customerId);
}
