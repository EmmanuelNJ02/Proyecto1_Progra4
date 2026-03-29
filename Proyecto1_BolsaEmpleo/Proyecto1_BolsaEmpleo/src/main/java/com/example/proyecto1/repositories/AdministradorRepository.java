package com.example.proyecto1.repositories;

import com.example.proyecto1.models.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
}
