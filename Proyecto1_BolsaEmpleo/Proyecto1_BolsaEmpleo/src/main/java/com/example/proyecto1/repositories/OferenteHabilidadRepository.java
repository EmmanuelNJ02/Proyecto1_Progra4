package com.example.proyecto1.repositories;

import com.example.proyecto1.models.OferenteHabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OferenteHabilidadRepository extends JpaRepository<OferenteHabilidad, OferenteHabilidad.OferenteHabilidadId> {

    List<OferenteHabilidad> findByOferente_UsuarioId(Integer oferenteId);
    
}

