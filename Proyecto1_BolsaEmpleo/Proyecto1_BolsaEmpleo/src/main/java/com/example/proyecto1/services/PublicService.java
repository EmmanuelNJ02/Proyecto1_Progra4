package com.example.proyecto1.services;

import com.example.proyecto1.dtos.products.PuestoResultadoDTO;
import com.example.proyecto1.models.Caracteristica;
import com.example.proyecto1.models.Puesto;
import com.example.proyecto1.models.PuestoCaracteristica;

import com.example.proyecto1.repositories.PuestoCaracteristicaRepository;
import com.example.proyecto1.repositories.PuestoRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class PublicService {

    private final PuestoRepository puestoRepository;
    private final PuestoCaracteristicaRepository puestoCaracteristicaRepository;

    public PublicService(PuestoRepository puestoRepository,
                         PuestoCaracteristicaRepository puestoCaracteristicaRepository) {
        this.puestoRepository = puestoRepository;
        this.puestoCaracteristicaRepository = puestoCaracteristicaRepository;
    }

    public List<Puesto> obtenerUltimos5Publicos() {
        return puestoRepository.findTop5ByTipoPublicacionAndActivoTrueOrderByFechaPublicacionDesc(
                Puesto.TipoPublicacion.PUBLICO
        );
    }

    public List<PuestoResultadoDTO> buscarPuestosPublicosPorCaracteristica(Integer caracteristicaId) {
        Set<Integer> puestosIds = new LinkedHashSet<>();

        List<PuestoCaracteristica> coincidencias = puestoCaracteristicaRepository.findByCaracteristica_Id(caracteristicaId);
        for (PuestoCaracteristica pc : coincidencias) {
            if (pc.getPuesto() != null &&
                    Boolean.TRUE.equals(pc.getPuesto().getActivo()) &&
                    pc.getPuesto().getTipoPublicacion() == Puesto.TipoPublicacion.PUBLICO) {
                puestosIds.add(pc.getPuesto().getId());
            }
        }

        List<PuestoResultadoDTO> resultados = new ArrayList<>();
        for (Integer id : puestosIds) {
            Puesto puesto = puestoRepository.findById(id).orElse(null);
            if (puesto != null) {
                resultados.add(new PuestoResultadoDTO(
                        puesto.getId(),
                        puesto.getEmpresa().getNombreEmpresa(),
                        puesto.getDescripcionGeneral(),
                        puesto.getSalario()
                ));
            }
        }

        return resultados;
    }

    public String construirResumenRequisitos(Puesto puesto) {
        if (puesto == null || puesto.getId() == null) {
            return "Sin requisitos";
        }
        List<PuestoCaracteristica> requisitos = puestoCaracteristicaRepository.findByPuesto_Id(puesto.getId());
        if (requisitos.isEmpty()) {
            return "Sin requisitos";
        }
        StringBuilder sb = new StringBuilder();
        for (PuestoCaracteristica requisito : requisitos) {
            Caracteristica c = requisito.getCaracteristica();
            if (c != null) {
                if (!sb.isEmpty()) {
                    sb.append(" | ");
                }
                sb.append(c.getNombreCaracteristica()).append(" nivel ").append(requisito.getNivelDeseado());
            }
        }
        return sb.toString();
    }
}
