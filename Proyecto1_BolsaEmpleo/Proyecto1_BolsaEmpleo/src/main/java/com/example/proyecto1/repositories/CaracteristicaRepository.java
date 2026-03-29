package com.example.proyecto1.repositories;

import com.example.proyecto1.models.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Integer> {
    List<Caracteristica> findByCaracteristicaPadreIsNull();
    List<Caracteristica> findByCaracteristicaPadreId(Integer padreId);
    List<Caracteristica> findByNombreCaracteristicaContainingIgnoreCase(String nombreCaracteristica);
}
