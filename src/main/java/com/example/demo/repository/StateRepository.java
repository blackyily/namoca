package com.example.demo.repository;

import com.example.demo.model.State;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StateRepository extends JpaRepository<State, Integer> {
    List<State> findByActiveTrue(); // Esto servirá para el combo del HTML
}