package com.example.proyecto1.controllers;

import com.example.proyecto1.dtos.products.CaracteristicaForm;
import com.example.proyecto1.services.AdminService;
import com.example.proyecto1.services.CaracteristicaService;

import com.lowagie.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.proyecto1.exceptions.NoEncontrado;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final CaracteristicaService caracteristicaService;

    public AdminController(AdminService adminService, CaracteristicaService caracteristicaService) {
        this.adminService = adminService;
        this.caracteristicaService = caracteristicaService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("puestosPorMes", adminService.obtenerPuestosPorMes());
        return "admin/dashboard";
    }

    @GetMapping("/empresas-pendientes")
    public String empresasPendientes(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("usuarios", adminService.listarEmpresasPendientes());
        return "admin/empresas-pendientes";
    }

    @GetMapping("/oferentes-pendientes")
    public String oferentesPendientes(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("usuarios", adminService.listarOferentesPendientes());
        return "admin/oferentes-pendientes";
    }

    @GetMapping("/aprobar/{usuarioId}")
    public String aprobar(@PathVariable Integer usuarioId, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/login";

        try {
            adminService.aprobarUsuario(usuarioId);
        } catch (NoEncontrado e) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/caracteristicas")
    public String caracteristicas(HttpSession session, Model model) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("caracteristicas", caracteristicaService.listarTodas());
        model.addAttribute("raices", caracteristicaService.listarRaices());
        model.addAttribute("caracteristicaForm", new CaracteristicaForm());
        return "admin/caracteristicas";
    }

    @PostMapping("/caracteristicas")
    public String guardarCaracteristica(@ModelAttribute CaracteristicaForm form, HttpSession session) {
        if (!esAdmin(session)) {
            return "redirect:/login";
        }
        if (form.getNombre() != null && !form.getNombre().isBlank()) {
            caracteristicaService.crear(form.getNombre().trim(), form.getPadreId());
        }
        return "redirect:/admin/caracteristicas";
    }

    @GetMapping("/reporte-pdf")
    public void reportePdf(HttpServletResponse response, HttpSession session) throws DocumentException, IOException {
        if (!esAdmin(session)) {
            response.sendRedirect("/login");
            return;
        }
        adminService.generarReportePdf(response);
    }

    private boolean esAdmin(HttpSession session) {
        Object usuarioId = session.getAttribute("usuarioId");
        Object rol = session.getAttribute("usuarioRol");
        return usuarioId != null && rol != null && "ADMIN".equals(rol.toString());
    }
}
