package com.example.proyecto1.repositories;

import com.example.proyecto1.models.Oferente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OferenteRepository extends JpaRepository<Oferente, Integer> {

    Optional<Oferente> findByUsuarioId(Integer usuarioId);
    boolean existsByIdentificacion(String identificacion);
}
