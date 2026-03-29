package com.example.proyecto1.repositories;

import com.example.proyecto1.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreoElectronico(String correoElectronico);

    List<Usuario> findByRolAndEstado(Usuario.Rol rol, Usuario.Estado estado);
}

