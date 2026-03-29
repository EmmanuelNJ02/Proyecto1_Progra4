package com.example.proyecto1.services;

import com.example.proyecto1.models.Usuario;
import com.example.proyecto1.repositories.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoElectronico(correo);
    }

    public List<Usuario> listarPendientesPorRol(Usuario.Rol rol) {
        return usuarioRepository.findByRolAndEstado(rol, Usuario.Estado.PENDIENTE);
    }

    public void aprobarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstado(Usuario.Estado.APROBADO);
        usuarioRepository.save(usuario);
    }

    public void rechazarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstado(Usuario.Estado.RECHAZADO);
        usuarioRepository.save(usuario);
    }
}
