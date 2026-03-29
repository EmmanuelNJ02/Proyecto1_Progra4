package com.example.proyecto1.controllers;

import com.example.proyecto1.dtos.products.BusquedaPuestoForm;
import com.example.proyecto1.models.Puesto;
import com.example.proyecto1.services.CaracteristicaService;
import com.example.proyecto1.services.PublicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PublicController {

    private final PublicService publicService;
    private final CaracteristicaService caracteristicaService;

    public PublicController(PublicService publicService, CaracteristicaService caracteristicaService) {
        this.publicService = publicService;
        this.caracteristicaService = caracteristicaService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        List<Puesto> puestos = publicService.obtenerUltimos5Publicos();
        model.addAttribute("puestosRecientes", puestos);
        model.addAttribute("resumenesRequisitos", construirResumenes(puestos));
        return "public/index";
    }

    @GetMapping({"/buscar-puestos", "/buscar"})
    public String mostrarBusqueda(Model model) {
        model.addAttribute("busquedaForm", new BusquedaPuestoForm());
        model.addAttribute("todasLasCaracteristicas", caracteristicaService.listarTodas());
        return "public/buscar-puestos";
    }

    @PostMapping({"/buscar-puestos", "/buscar"})
    public String procesarBusqueda(@ModelAttribute("busquedaForm") BusquedaPuestoForm busquedaForm, Model model) {
        model.addAttribute("todasLasCaracteristicas", caracteristicaService.listarTodas());

        if (busquedaForm.getCaracteristicaId() == null) {
            model.addAttribute("error", "Debe seleccionar una característica.");
            return "public/buscar-puestos";
        }

        model.addAttribute("resultados", publicService.buscarPuestosPublicosPorCaracteristica(busquedaForm.getCaracteristicaId()));
        return "public/buscar-puestos";
    }

    private Map<Integer, String> construirResumenes(List<Puesto> puestos) {
        Map<Integer, String> resumenes = new LinkedHashMap<>();
        for (Puesto puesto : puestos) {
            resumenes.put(puesto.getId(), publicService.construirResumenRequisitos(puesto));
        }
        return resumenes;
    }
}
