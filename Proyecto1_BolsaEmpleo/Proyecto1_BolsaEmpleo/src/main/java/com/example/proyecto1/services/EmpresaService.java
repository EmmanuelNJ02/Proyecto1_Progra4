package com.example.proyecto1.services;

import com.example.proyecto1.dtos.products.CandidatoResultadoDTO;
import com.example.proyecto1.dtos.products.PuestoForm;
import com.example.proyecto1.models.Caracteristica;
import com.example.proyecto1.models.Empresa;
import com.example.proyecto1.models.Oferente;
import com.example.proyecto1.models.OferenteHabilidad;
import com.example.proyecto1.models.Puesto;
import com.example.proyecto1.models.PuestoCaracteristica;
import com.example.proyecto1.repositories.CaracteristicaRepository;
import com.example.proyecto1.repositories.EmpresaRepository;
import com.example.proyecto1.repositories.OferenteHabilidadRepository;
import com.example.proyecto1.repositories.OferenteRepository;
import com.example.proyecto1.repositories.PuestoCaracteristicaRepository;
import com.example.proyecto1.repositories.PuestoRepository;
import com.example.proyecto1.exceptions.NoEncontrado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final PuestoRepository puestoRepository;
    private final PuestoCaracteristicaRepository puestoCaracteristicaRepository;
    private final CaracteristicaRepository caracteristicaRepository;
    private final OferenteRepository oferenteRepository;
    private final OferenteHabilidadRepository oferenteHabilidadRepository;

    public EmpresaService(EmpresaRepository empresaRepository,
                          PuestoRepository puestoRepository,
                          PuestoCaracteristicaRepository puestoCaracteristicaRepository,
                          CaracteristicaRepository caracteristicaRepository,
                          OferenteRepository oferenteRepository,
                          OferenteHabilidadRepository oferenteHabilidadRepository) {
        this.empresaRepository = empresaRepository;
        this.puestoRepository = puestoRepository;
        this.puestoCaracteristicaRepository = puestoCaracteristicaRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.oferenteRepository = oferenteRepository;
        this.oferenteHabilidadRepository = oferenteHabilidadRepository;
    }

    public List<Puesto> obtenerPuestosEmpresa(Integer usuarioId) {
        return puestoRepository.findByEmpresaUsuarioIdOrderByFechaPublicacionDesc(usuarioId);
    }

    public Optional<Puesto> buscarPuesto(Integer puestoId) {
        return puestoRepository.findById(puestoId);
    }

    public Optional<Puesto> buscarPuestoDeEmpresa(Integer puestoId, Integer usuarioId) {
        return puestoRepository.findByIdAndEmpresaUsuarioId(puestoId, usuarioId);
    }

    public void desactivarPuesto(Integer puestoId) {
        Puesto puesto = puestoRepository.findById(puestoId).orElse(null);
        if (puesto != null) {
            puesto.setActivo(false);
            puestoRepository.save(puesto);
        }
    }

    public void desactivarPuestoDeEmpresa(Integer puestoId, Integer usuarioId) {
        Puesto puesto = puestoRepository.findByIdAndEmpresaUsuarioId(puestoId, usuarioId).orElse(null);
        if (puesto != null) {
            puesto.setActivo(false);
            puestoRepository.save(puesto);
        }
    }

    public void activarPuestoDeEmpresa(Integer puestoId, Integer usuarioId) {
        Puesto puesto = puestoRepository.findByIdAndEmpresaUsuarioId(puestoId, usuarioId).orElse(null);
        if (puesto != null) {
            puesto.setActivo(true);
            puestoRepository.save(puesto);
        }
    }

    public String validarPuesto(PuestoForm form) {
        if (form == null) {
            return "Los datos del puesto son inválidos.";
        }
        if (form.getDescripcionGeneral() == null || form.getDescripcionGeneral().isBlank()) {
            return "La descripción es obligatoria.";
        }
        if (form.getSalario() == null || form.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            return "El salario debe ser mayor que cero.";
        }
        if (form.getTipoPublicacion() == null || form.getTipoPublicacion().isBlank()) {
            return "Debe seleccionar el tipo de publicación.";
        }
        try {
            Puesto.TipoPublicacion.valueOf(form.getTipoPublicacion());
        } catch (Exception e) {
            return "El tipo de publicación es inválido.";
        }

        int requisitosValidos = 0;
        if (esParRequisitoValido(form.getCaracteristicaId1(), form.getNivel1())) requisitosValidos++;
        else if (form.getCaracteristicaId1() != null || form.getNivel1() != null) return "Complete correctamente el requisito 1.";
        if (esParRequisitoValido(form.getCaracteristicaId2(), form.getNivel2())) requisitosValidos++;
        else if (form.getCaracteristicaId2() != null || form.getNivel2() != null) return "Complete correctamente el requisito 2.";
        if (esParRequisitoValido(form.getCaracteristicaId3(), form.getNivel3())) requisitosValidos++;
        else if (form.getCaracteristicaId3() != null || form.getNivel3() != null) return "Complete correctamente el requisito 3.";

        if (requisitosValidos == 0) {
            return "Debe registrar al menos un requisito con su nivel.";
        }

        Set<Integer> ids = new LinkedHashSet<>();
        if (!agregarId(ids, form.getCaracteristicaId1()) || !agregarId(ids, form.getCaracteristicaId2()) || !agregarId(ids, form.getCaracteristicaId3())) {
            return "No puede repetir características dentro del mismo puesto.";
        }

        return null;
    }

    private boolean esParRequisitoValido(Integer caracteristicaId, Integer nivel) {
        return caracteristicaId != null && nivel != null && nivel >= 1 && nivel <= 5;
    }

    private boolean agregarId(Set<Integer> ids, Integer id) {
        return id == null || ids.add(id);
    }

    @Transactional
    public Puesto crearPuesto(PuestoForm form, Integer usuarioId) {
        Empresa empresa = empresaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NoEncontrado("No se encontró la empresa"));

        Puesto puesto = new Puesto();
        puesto.setEmpresa(empresa);
        puesto.setDescripcionGeneral(form.getDescripcionGeneral().trim());
        puesto.setSalario(form.getSalario());
        puesto.setTipoPublicacion(Puesto.TipoPublicacion.valueOf(form.getTipoPublicacion()));
        puesto.setActivo(true);
        puesto = puestoRepository.saveAndFlush(puesto);

        guardarRequisito(puesto, form.getCaracteristicaId1(), form.getNivel1());
        guardarRequisito(puesto, form.getCaracteristicaId2(), form.getNivel2());
        guardarRequisito(puesto, form.getCaracteristicaId3(), form.getNivel3());
        return puestoRepository.findById(puesto.getId()).orElse(puesto);
    }

    private void guardarRequisito(Puesto puesto, Integer caracteristicaId, Integer nivel) {
        if (caracteristicaId == null || nivel == null) {
            return;
        }

        Caracteristica caracteristica = caracteristicaRepository.findById(caracteristicaId)
                .orElseThrow(() -> new IllegalArgumentException("La característica seleccionada no existe."));

        PuestoCaracteristica requisito = new PuestoCaracteristica();
        requisito.setId(new PuestoCaracteristica.PuestoCaracteristicaId(puesto.getId(), caracteristicaId));
        requisito.setPuesto(puesto);
        requisito.setCaracteristica(caracteristica);
        requisito.setNivelDeseado(nivel);
        puestoCaracteristicaRepository.saveAndFlush(requisito);
    }

    public List<PuestoCaracteristica> listarRequisitos(Integer puestoId) {
        return puestoCaracteristicaRepository.findByPuesto_Id(puestoId);
    }

    public List<CandidatoResultadoDTO> buscarCandidatos(Integer puestoId) {
        List<PuestoCaracteristica> requisitos = puestoCaracteristicaRepository.findByPuesto_Id(puestoId);
        List<Oferente> oferentes = oferenteRepository.findAll();
        List<CandidatoResultadoDTO> resultados = new ArrayList<>();

        for (Oferente oferente : oferentes) {
            List<OferenteHabilidad> habilidades = oferenteHabilidadRepository.findByOferente_UsuarioId(oferente.getUsuarioId());
            int cumplidos = 0;
            double puntajeTotal = 0.0;
            for (PuestoCaracteristica requisito : requisitos) {
                double puntajeRequisito = calcularPuntajeRequisito(habilidades, requisito);
                if (puntajeRequisito >= 1.0) {
                    cumplidos++;
                }
                if (puntajeRequisito > 0) {
                    puntajeTotal += puntajeRequisito;
                }
            }

            if (!requisitos.isEmpty() && puntajeTotal > 0) {
                double porcentaje = (puntajeTotal * 100.0) / requisitos.size();
                String nombreCompleto = oferente.getNombreOferente() + " " + oferente.getPrimerApellido();
                resultados.add(new CandidatoResultadoDTO(oferente.getUsuarioId(), nombreCompleto, cumplidos, requisitos.size(), porcentaje));
            }
        }

        resultados.sort(Comparator.comparingDouble(CandidatoResultadoDTO::getPorcentajeCoincidencia).reversed());
        return resultados;
    }

    private double calcularPuntajeRequisito(List<OferenteHabilidad> habilidades, PuestoCaracteristica requisito) {
        if (requisito == null || requisito.getCaracteristica() == null || requisito.getCaracteristica().getId() == null) {
            return 0.0;
        }

        Integer requisitoId = requisito.getCaracteristica().getId();
        Integer nivelDeseado = requisito.getNivelDeseado();
        if (nivelDeseado == null || nivelDeseado < 1) {
            nivelDeseado = 1;
        }

        for (OferenteHabilidad habilidad : habilidades) {
            if (habilidad.getCaracteristica() == null
                    || habilidad.getCaracteristica().getId() == null
                    || habilidad.getNivel() == null) {
                continue;
            }

            Integer habilidadId = habilidad.getCaracteristica().getId();
            Integer nivelHabilidad = habilidad.getNivel();
            if (!habilidadId.equals(requisitoId)) {
                continue;
            }

            if (nivelHabilidad >= nivelDeseado) {
                return 1.0;
            }

            int diferencia = nivelDeseado - nivelHabilidad;
            double puntajeParcial = 1.0 - (diferencia * 0.10);
            if (puntajeParcial < 0.0) {
                puntajeParcial = 0.0;
            }
            return puntajeParcial;
        }
        return 0.0;
    }

    public Optional<Oferente> buscarOferentePorUsuarioId(Integer usuarioId) {
        return oferenteRepository.findByUsuarioId(usuarioId);
    }

    public List<OferenteHabilidad> listarHabilidadesOferente(Integer usuarioId) {
        return oferenteHabilidadRepository.findByOferente_UsuarioId(usuarioId);
    }


}
