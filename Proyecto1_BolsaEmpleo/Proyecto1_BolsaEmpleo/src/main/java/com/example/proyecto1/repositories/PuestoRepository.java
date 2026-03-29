package com.example.proyecto1.repositories;

import com.example.proyecto1.models.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Integer> {

    List<Puesto> findByEmpresaUsuarioId(Integer empresaId);

    List<Puesto> findByEmpresaUsuarioIdOrderByFechaPublicacionDesc(Integer empresaId);

    List<Puesto> findByTipoPublicacionAndActivoTrue(Puesto.TipoPublicacion tipoPublicacion);

    List<Puesto> findTop5ByTipoPublicacionAndActivoTrueOrderByFechaPublicacionDesc(Puesto.TipoPublicacion tipoPublicacion);

    Optional<Puesto> findByIdAndEmpresaUsuarioId(Integer id, Integer empresaUsuarioId);
}
