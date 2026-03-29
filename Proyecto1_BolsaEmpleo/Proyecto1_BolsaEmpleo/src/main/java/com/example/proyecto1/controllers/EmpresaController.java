package com.example.proyecto1.controllers;

import com.example.proyecto1.dtos.products.PuestoForm;
import com.example.proyecto1.models.Oferente;
import com.example.proyecto1.models.OferenteHabilidad;
import com.example.proyecto1.models.Puesto;
import com.example.proyecto1.services.CaracteristicaService;
import com.example.proyecto1.services.EmpresaService;
import com.example.proyecto1.services.OferenteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.example.proyecto1.exceptions.NoEncontrado;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final CaracteristicaService caracteristicaService;
    private final OferenteService oferenteService;

    public EmpresaController(EmpresaService empresaService,
                             CaracteristicaService caracteristicaService,
                             OferenteService oferenteService) {
        this.empresaService = empresaService;
        this.caracteristicaService = caracteristicaService;
        this.oferenteService = oferenteService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer usuarioId = obtenerUsuarioEmpresa(session);
        if (usuarioId == null) {
            return "redirect:/login";
        }

        model.addAttribute("puestos", empresaService.obtenerPuestosEmpresa(usuarioId));
        return "empresa/dashboard";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoPuesto(HttpSession session, Model model) {
        Integer usuarioId = obtenerUsuarioEmpresa(session);
        if (usuarioId == null) {
            return "redirect:/login";
        }

        model.addAttribute("puestoForm", new PuestoForm());
        model.addAttribute("caracteristicas", caracteristicaService.listarTodas());
        return "empresa/nuevo-puesto";
    }

    @PostMapping("/guardar")
    public String guardarPuesto(@ModelAttribute("puestoForm") PuestoForm form, HttpSession session, Model model) {
        Integer usuarioId = obtenerUsuarioEmpresa(session);
        if (usuarioId == null) {
            return "redirect:/login";
        }

        String error = empresaService.validarPuesto(form);
        if (error != null) {
            model.addAttribute("puestoForm", form);
            model.addAttribute("error", error);
            model.addAttribute("caracteristicas", caracteristicaService.listarTodas());
            return "empresa/nuevo-puesto";
        }

        try {
            empresaService.crearPuesto(form, usuarioId);
            return "redirect:/empresa/dashboard";
        } catch (Exception e) {
            model.addAttribute("puestoForm", form);
            model.addAttribute("error", e.getMessage() == null || e.getMessage().isBlank()
                    ? "No se pudo guardar el puesto."
                    : e.getMessage());
            model.addAttribute("caracteristicas", caracteristicaService.listarTodas());
            return "empresa/nuevo-puesto";
        }
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Integer id, HttpSession session) {
        Integer usuarioId = obtenerUsuarioEmpresa(session);
        if (usuarioId == null) {
            return "redirect:/login";
        }
        empresaService.desactivarPuestoDeEmpresa(id, usuarioId);
        return "redirect:/empresa/dashboard";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Integer id, HttpSession session) {
        Integer usuarioId = obtenerUsuarioEmpresa(session);
        if (usuarioId == null) {
            return "redirect:/login";
        }
        empresaService.activarPuestoDeEmpresa(id, usuarioId);
        return "redirect:/empresa/dashboard";
    }

    @GetMapping("/candidatos/{puestoId}")
    public String verCandidatos(@PathVariable Integer puestoId, HttpSession session, Model model) {
        Integer usuarioId = obtenerUsuarioEmpresa(session);
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Puesto puesto = empresaService.buscarPuestoDeEmpresa(puestoId, usuarioId)
                .orElseThrow(() -> new NoEncontrado("El puesto no fue encontrado"));


        model.addAttribute("puesto", puesto);
        model.addAttribute("candidatos", empresaService.buscarCandidatos(puestoId));
        return "empresa/candidatos";
    }

    @GetMapping("/candidato/{usuarioId}")
    public String verDetalleCandidato(@PathVariable Integer usuarioId, HttpSession session, Model model) {
        Integer sesionUsuarioId = obtenerUsuarioEmpresa(session);
        if (sesionUsuarioId == null) {
            return "redirect:/login";
        }

        Oferente oferente = empresaService.buscarOferentePorUsuarioId(usuarioId)
                .orElseThrow(() -> new NoEncontrado("El oferente no existe"));

        List<OferenteHabilidad> habilidades = empresaService.listarHabilidadesOferente(usuarioId);
        model.addAttribute("oferente", oferente);
        model.addAttribute("habilidades", habilidades);
        return "empresa/detalle-candidato";
    }

    @GetMapping("/candidato/{usuarioId}/cv")
    public ResponseEntity<Resource> verCurriculum(@PathVariable Integer usuarioId, HttpSession session) {
        Integer sesionUsuarioId = obtenerUsuarioEmpresa(session);
        if (sesionUsuarioId == null) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "/login").build();
        }

        Oferente oferente = empresaService.buscarOferentePorUsuarioId(usuarioId).orElse(null);
        if (oferente == null || oferente.getRutaCurriculum() == null || oferente.getRutaCurriculum().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        Path path = oferenteService.resolverRutaCv(oferente.getRutaCurriculum());
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + resource.getFilename())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private Integer obtenerUsuarioEmpresa(HttpSession session) {
        Object usuarioId = session.getAttribute("usuarioId");
        Object rol = session.getAttribute("usuarioRol");
        if (usuarioId == null || rol == null || !"EMPRESA".equals(rol.toString())) {
            return null;
        }
        return (Integer) usuarioId;
    }
}
