package com.example.proyecto1.controllers;

import com.example.proyecto1.dtos.products.LoginForm;
import com.example.proyecto1.dtos.products.RegistroEmpresaForm;
import com.example.proyecto1.dtos.products.RegistroOferenteForm;

import com.example.proyecto1.models.Empresa;
import com.example.proyecto1.models.Oferente;
import com.example.proyecto1.models.Usuario;

import com.example.proyecto1.services.AuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "auth/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute("loginForm") LoginForm loginForm,
                                HttpSession session,
                                Model model) {
        Optional<Usuario> usuarioOpt = authService.login(loginForm.getCorreo(), loginForm.getClave());

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("loginForm", loginForm);
            model.addAttribute("error", "Credenciales inválidas o usuario no aprobado.");
            return "auth/login";
        }

        Usuario usuario = usuarioOpt.get();
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioRol", usuario.getRol().name());
        session.setAttribute("usuarioCorreo", usuario.getCorreoElectronico());

        return switch (usuario.getRol()) {
            case ADMIN -> "redirect:/admin/dashboard";
            case EMPRESA -> "redirect:/empresa/dashboard";
            case OFERENTE -> "redirect:/oferente/dashboard";
        };
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping({"/registro/empresa", "/empresa/registro"})
    public String mostrarRegistroEmpresa(Model model) {
        model.addAttribute("registroEmpresaForm", new RegistroEmpresaForm());
        return "auth/registro-empresa";
    }

    @PostMapping({"/registro/empresa", "/empresa/registro"})
    public String procesarRegistroEmpresa(@ModelAttribute("registroEmpresaForm") RegistroEmpresaForm form,
                                          Model model) {
        String errorValidacion = authService.validarRegistroEmpresa(form);
        if (errorValidacion != null) {
            model.addAttribute("registroEmpresaForm", form);
            model.addAttribute("error", errorValidacion);
            return "auth/registro-empresa";
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setCorreoElectronico(form.getCorreoElectronico());
            usuario.setClave(form.getClave());

            Empresa empresa = new Empresa();
            empresa.setNombreEmpresa(form.getNombreEmpresa());
            empresa.setLocalizacion(form.getLocalizacion());
            empresa.setCorreoElectronico(form.getCorreoElectronico());
            empresa.setTelefono(form.getTelefono());
            empresa.setDescripcion(form.getDescripcion());

            authService.registrarEmpresa(empresa, usuario);
            model.addAttribute("mensaje", "Registro enviado correctamente. Debe esperar aprobación del administrador.");
            return "auth/registro-exitoso";
        } catch (Exception e) {
            model.addAttribute("registroEmpresaForm", form);
            model.addAttribute("error", e.getMessage() == null || e.getMessage().isBlank()
                    ? "No se pudo registrar la empresa."
                    : e.getMessage());
            return "auth/registro-empresa";
        }
    }

    @GetMapping({"/registro/oferente", "/oferente/registro"})
    public String mostrarRegistroOferente(Model model) {
        model.addAttribute("registroOferenteForm", new RegistroOferenteForm());
        return "auth/registro-oferente";
    }

    @PostMapping({"/registro/oferente", "/oferente/registro"})
    public String procesarRegistroOferente(@ModelAttribute("registroOferenteForm") RegistroOferenteForm form,
                                           Model model) {
        String errorValidacion = authService.validarRegistroOferente(form);
        if (errorValidacion != null) {
            model.addAttribute("registroOferenteForm", form);
            model.addAttribute("error", errorValidacion);
            return "auth/registro-oferente";
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setCorreoElectronico(form.getCorreoElectronico());
            usuario.setClave(form.getClave());

            Oferente oferente = new Oferente();
            oferente.setIdentificacion(form.getIdentificacion());
            oferente.setNombreOferente(form.getNombreOferente());
            oferente.setPrimerApellido(form.getPrimerApellido());
            oferente.setSegundoApellido(form.getSegundoApellido());
            oferente.setNacionalidad(form.getNacionalidad());
            oferente.setTelefono(form.getTelefono());
            oferente.setLugarResidencia(form.getLugarResidencia());

            authService.registrarOferente(oferente, usuario);
            model.addAttribute("mensaje", "Registro enviado correctamente. Debe esperar aprobación del administrador.");
            return "auth/registro-exitoso";
        } catch (Exception e) {
            model.addAttribute("registroOferenteForm", form);
            model.addAttribute("error", e.getMessage() == null || e.getMessage().isBlank()
                    ? "No se pudo registrar el oferente."
                    : e.getMessage());
            return "auth/registro-oferente";
        }
    }
}
