package com.example.proyecto1.controllers;

import com.example.proyecto1.dtos.products.HabilidadForm;

import com.example.proyecto1.models.Oferente;
import com.example.proyecto1.services.CaracteristicaService;
import com.example.proyecto1.services.OferenteService;

import jakarta.servlet.http.HttpSession;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;

@Controller
@RequestMapping("/oferente")
public class OferenteController {

    private final OferenteService oferenteService;
    private final CaracteristicaService caracteristicaService;

    public OferenteController(OferenteService oferenteService, CaracteristicaService caracteristicaService) {
        this.oferenteService = oferenteService;
        this.caracteristicaService = caracteristicaService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer usuarioId = obtenerUsuarioOferente(session);
        if (usuarioId == null) {
            return "redirect:/login";
        }
        model.addAttribute("oferente", oferenteService.buscarPorUsuarioId(usuarioId).orElse(null));
        return "oferente/dashboard";
    }

    @GetMapping("/habilidades")
    public String habilidades(HttpSession session, Model model) {
        Integer usuarioId = obtenerUsuarioOferente(session);
        if (usuarioId == null) {
            return "redirect:/login";
        }

        model.addAttribute("habilidadForm", new HabilidadForm());
        model.addAttribute("habilidades", oferenteService.listarHabilidades(usuarioId));
        model.addAttribute("caracteristicas", caracteristicaService.listarTodas());
        model.addAttribute("oferente", oferenteService.buscarPorUsuarioId(usuarioId).orElse(null));
        return "oferente/habilidades";
    }

    @PostMapping("/habilidades")
    public String guardarHabilidad(@ModelAttribute HabilidadForm habilidadForm, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer usuarioId = obtenerUsuarioOferente(session);
        if (usuarioId == null) {
            return "redirect:/login";
        }

        String error = oferenteService.validarHabilidad(habilidadForm.getCaracteristicaId(), habilidadForm.getNivel());
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorHabilidad", error);
            return "redirect:/oferente/habilidades";
        }

        try {
            oferenteService.guardarHabilidad(usuarioId, habilidadForm.getCaracteristicaId(), habilidadForm.getNivel());
            redirectAttributes.addFlashAttribute("mensajeHabilidad", "Habilidad guardada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorHabilidad", e.getMessage() == null || e.getMessage().isBlank()
                    ? "No se pudo guardar la habilidad."
                    : e.getMessage());
        }
        return "redirect:/oferente/habilidades";
    }

    @PostMapping("/cv")
    public String subirCv(@RequestParam("archivo") MultipartFile archivo, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer usuarioId = obtenerUsuarioOferente(session);
        if (usuarioId == null) {
            return "redirect:/login";
        }
        try {
            oferenteService.subirCurriculum(usuarioId, archivo);
            redirectAttributes.addFlashAttribute("mensajeCv", "CV subido correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCv", e.getMessage() == null || e.getMessage().isBlank()
                    ? "No se pudo subir el archivo."
                    : e.getMessage());
        }
        return "redirect:/oferente/habilidades";
    }

    @GetMapping("/cv")
    public ResponseEntity<Resource> verMiCv(HttpSession session) {
        Integer usuarioId = obtenerUsuarioOferente(session);
        if (usuarioId == null) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "/login").build();
        }

        Oferente oferente = oferenteService.buscarPorUsuarioId(usuarioId).orElse(null);
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

    private Integer obtenerUsuarioOferente(HttpSession session) {
        Object usuarioId = session.getAttribute("usuarioId");
        Object rol = session.getAttribute("usuarioRol");
        if (usuarioId == null || rol == null || !"OFERENTE".equals(rol.toString())) {
            return null;
        }
        return (Integer) usuarioId;
    }
}
