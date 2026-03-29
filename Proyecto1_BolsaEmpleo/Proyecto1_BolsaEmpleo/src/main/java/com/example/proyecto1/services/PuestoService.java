package com.example.proyecto1.services;

import com.example.proyecto1.models.Puesto;
import com.example.proyecto1.repositories.PuestoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PuestoService {

    private final PuestoRepository puestoRepository;

    public PuestoService(PuestoRepository puestoRepository) {
        this.puestoRepository = puestoRepository;
    }

    public Puesto guardar(Puesto puesto) {
        return puestoRepository.save(puesto);
    }

    public Optional<Puesto> buscarPorId(Integer id) {
        return puestoRepository.findById(id);
    }

    public List<Puesto> listarPorEmpresa(Integer empresaId) {
        return puestoRepository.findByEmpresaUsuarioId(empresaId);
    }

    public List<Puesto> listarPublicosActivos() {
        return puestoRepository.findByTipoPublicacionAndActivoTrue(Puesto.TipoPublicacion.PUBLICO);
    }

    public List<Puesto> ultimos5Publicos() {
        return puestoRepository.findTop5ByTipoPublicacionAndActivoTrueOrderByFechaPublicacionDesc(Puesto.TipoPublicacion.PUBLICO);
    }

    public void desactivar(Integer puestoId) {
        Puesto puesto = puestoRepository.findById(puestoId)
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));
        puesto.setActivo(false);
        puestoRepository.save(puesto);
    }
}

