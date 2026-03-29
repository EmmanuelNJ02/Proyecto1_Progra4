package com.example.proyecto1.repositories;

import com.example.proyecto1.models.PuestoCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuestoCaracteristicaRepository extends JpaRepository<PuestoCaracteristica, PuestoCaracteristica.PuestoCaracteristicaId> {

    List<PuestoCaracteristica> findByCaracteristica_Id(Integer caracteristicaId);

    List<PuestoCaracteristica> findByPuesto_Id(Integer puestoId);

}


