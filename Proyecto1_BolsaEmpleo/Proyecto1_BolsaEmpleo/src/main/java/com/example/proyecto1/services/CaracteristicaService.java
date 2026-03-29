package com.example.proyecto1.services;

import com.example.proyecto1.models.Caracteristica;
import com.example.proyecto1.repositories.CaracteristicaRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CaracteristicaService {

    private final CaracteristicaRepository caracteristicaRepository;

    public CaracteristicaService(CaracteristicaRepository caracteristicaRepository) {
        this.caracteristicaRepository = caracteristicaRepository;
    }

    public List<Caracteristica> listarRaices() {
        return caracteristicaRepository.findByCaracteristicaPadreIsNull();
    }

    public List<Caracteristica> listarHijas(Integer padreId) {
        return caracteristicaRepository.findByCaracteristicaPadreId(padreId);
    }

    public List<Caracteristica> listarTodas() {
        return caracteristicaRepository.findAll();
    }

    public List<Caracteristica> listarHojas() {
        List<Caracteristica> todas = caracteristicaRepository.findAll();
        List<Caracteristica> hojas = new ArrayList<>();
        for (Caracteristica c : todas) {
            if (c.getSubCaracteristicas() == null || c.getSubCaracteristicas().isEmpty()) {
                hojas.add(c);
            }
        }
        return hojas;
    }

    public Optional<Caracteristica> buscarPorId(Integer id) {
        return caracteristicaRepository.findById(id);
    }

    public Caracteristica guardar(Caracteristica caracteristica) {
        return caracteristicaRepository.save(caracteristica);
    }

    public Caracteristica crear(String nombre, Integer padreId) {
        Caracteristica caracteristica = new Caracteristica();
        caracteristica.setNombreCaracteristica(nombre);
        if (padreId != null) {
            caracteristica.setCaracteristicaPadre(caracteristicaRepository.findById(padreId).orElse(null));
        }
        return caracteristicaRepository.save(caracteristica);
    }

    public Set<Integer> obtenerIdsIncluyendoDescendientes(Integer caracteristicaId) {
        Set<Integer> ids = new LinkedHashSet<>();
        if (caracteristicaId == null) {
            return ids;
        }
        agregarConDescendientes(caracteristicaId, ids);
        return ids;
    }

    private void agregarConDescendientes(Integer caracteristicaId, Set<Integer> ids) {
        if (caracteristicaId == null || ids.contains(caracteristicaId)) {
            return;
        }
        ids.add(caracteristicaId);
        for (Caracteristica hija : listarHijas(caracteristicaId)) {
            agregarConDescendientes(hija.getId(), ids);
        }
    }

    public Set<Integer> obtenerIdsIncluyendoAncestros(Integer caracteristicaId) {
        Set<Integer> ids = new LinkedHashSet<>();
        if (caracteristicaId == null) {
            return ids;
        }
        agregarConAncestros(caracteristicaId, ids);
        return ids;
    }

    private void agregarConAncestros(Integer caracteristicaId, Set<Integer> ids) {
        if (caracteristicaId == null || ids.contains(caracteristicaId)) {
            return;
        }
        ids.add(caracteristicaId);
        Caracteristica actual = caracteristicaRepository.findById(caracteristicaId).orElse(null);
        if (actual != null && actual.getCaracteristicaPadre() != null && actual.getCaracteristicaPadre().getId() != null) {
            agregarConAncestros(actual.getCaracteristicaPadre().getId(), ids);
        }
    }

    public boolean esMismaODescendiente(Integer posibleDescendienteId, Integer posibleAncestroId) {
        if (posibleDescendienteId == null || posibleAncestroId == null) {
            return false;
        }
        return obtenerIdsIncluyendoAncestros(posibleDescendienteId).contains(posibleAncestroId);
    }

    public boolean esMismaOAncestro(Integer posibleAncestroId, Integer posibleDescendienteId) {
        if (posibleAncestroId == null || posibleDescendienteId == null) {
            return false;
        }
        return obtenerIdsIncluyendoAncestros(posibleDescendienteId).contains(posibleAncestroId);
    }
}
