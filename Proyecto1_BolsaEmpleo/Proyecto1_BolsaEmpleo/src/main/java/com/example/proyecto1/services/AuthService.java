package com.example.proyecto1.services;

import com.example.proyecto1.dtos.products.RegistroEmpresaForm;
import com.example.proyecto1.dtos.products.RegistroOferenteForm;
import com.example.proyecto1.models.Empresa;
import com.example.proyecto1.models.Oferente;
import com.example.proyecto1.models.Usuario;
import com.example.proyecto1.repositories.EmpresaRepository;
import com.example.proyecto1.repositories.OferenteRepository;
import com.example.proyecto1.repositories.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;

    public AuthService(UsuarioRepository usuarioRepository,
                       EmpresaRepository empresaRepository,
                       OferenteRepository oferenteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.oferenteRepository = oferenteRepository;
    }

    public Optional<Usuario> login(String correo, String clave) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoElectronico(correo);

        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getClave().equals(clave)) {
            return Optional.empty();
        }

        if (!Usuario.Rol.ADMIN.equals(usuario.getRol()) && usuario.getEstado() != Usuario.Estado.APROBADO) {
            return Optional.empty();
        }

        return Optional.of(usuario);
    }

    public String validarRegistroEmpresa(RegistroEmpresaForm form) {
        if (form == null) return "Datos inválidos.";
        if (estaVacio(form.getNombreEmpresa())) return "El nombre de la empresa es obligatorio.";
        if (estaVacio(form.getLocalizacion())) return "La localización es obligatoria.";
        if (estaVacio(form.getCorreoElectronico())) return "El correo electrónico es obligatorio.";
        if (estaVacio(form.getTelefono())) return "El teléfono es obligatorio.";
        if (estaVacio(form.getClave())) return "La clave es obligatoria.";
        if (correoYaExiste(form.getCorreoElectronico().trim())) return "Ya existe un usuario con ese correo.";
        return null;
    }

    public String validarRegistroOferente(RegistroOferenteForm form) {
        if (form == null) return "Datos inválidos.";
        if (estaVacio(form.getIdentificacion())) return "La identificación es obligatoria.";
        if (estaVacio(form.getNombreOferente())) return "El nombre es obligatorio.";
        if (estaVacio(form.getPrimerApellido())) return "El primer apellido es obligatorio.";
        if (estaVacio(form.getNacionalidad())) return "La nacionalidad es obligatoria.";
        if (estaVacio(form.getTelefono())) return "El teléfono es obligatorio.";
        if (estaVacio(form.getCorreoElectronico())) return "El correo electrónico es obligatorio.";
        if (estaVacio(form.getLugarResidencia())) return "El lugar de residencia es obligatorio.";
        if (estaVacio(form.getClave())) return "La clave es obligatoria.";
        if (correoYaExiste(form.getCorreoElectronico().trim())) return "Ya existe un usuario con ese correo.";
        if (oferenteRepository.existsByIdentificacion(form.getIdentificacion().trim())) {
            return "Ya existe un oferente con esa identificación.";
        }
        return null;
    }

    @Transactional
    public Empresa registrarEmpresa(Empresa empresa, Usuario usuario) {
        try {
            usuario.setCorreoElectronico(usuario.getCorreoElectronico().trim());
            usuario.setRol(Usuario.Rol.EMPRESA);
            usuario.setEstado(Usuario.Estado.PENDIENTE);
            Usuario usuarioGuardado = usuarioRepository.saveAndFlush(usuario);

            empresa.setNombreEmpresa(empresa.getNombreEmpresa().trim());
            empresa.setLocalizacion(empresa.getLocalizacion().trim());
            empresa.setCorreoElectronico(empresa.getCorreoElectronico().trim());
            empresa.setTelefono(empresa.getTelefono().trim());
            empresa.setDescripcion(empresa.getDescripcion() == null ? null : empresa.getDescripcion().trim());
            empresa.setUsuario(usuarioGuardado);

            return empresaRepository.saveAndFlush(empresa);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("No se pudo registrar la empresa. Verifique que el correo no esté repetido y que todos los datos sean válidos.");
        }
    }

    @Transactional
    public Oferente registrarOferente(Oferente oferente, Usuario usuario) {
        try {
            usuario.setCorreoElectronico(usuario.getCorreoElectronico().trim());
            usuario.setRol(Usuario.Rol.OFERENTE);
            usuario.setEstado(Usuario.Estado.PENDIENTE);
            Usuario usuarioGuardado = usuarioRepository.saveAndFlush(usuario);

            oferente.setIdentificacion(oferente.getIdentificacion().trim());
            oferente.setNombreOferente(oferente.getNombreOferente().trim());
            oferente.setPrimerApellido(oferente.getPrimerApellido().trim());
            oferente.setSegundoApellido(oferente.getSegundoApellido() == null ? null : oferente.getSegundoApellido().trim());
            oferente.setNacionalidad(oferente.getNacionalidad().trim());
            oferente.setTelefono(oferente.getTelefono().trim());
            oferente.setLugarResidencia(oferente.getLugarResidencia().trim());
            oferente.setUsuario(usuarioGuardado);

            return oferenteRepository.saveAndFlush(oferente);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("No se pudo registrar el oferente. Verifique que el correo o la identificación no estén repetidos y que todos los datos sean válidos.");
        }
    }

    public boolean correoYaExiste(String correo) {
        return correo != null && usuarioRepository.findByCorreoElectronico(correo.trim()).isPresent();
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }


}
