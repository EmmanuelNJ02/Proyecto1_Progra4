package com.example.proyecto1.services;

import com.example.proyecto1.models.Caracteristica;
import com.example.proyecto1.models.Oferente;
import com.example.proyecto1.models.OferenteHabilidad;
import com.example.proyecto1.repositories.CaracteristicaRepository;
import com.example.proyecto1.repositories.OferenteHabilidadRepository;
import com.example.proyecto1.repositories.OferenteRepository;
import com.example.proyecto1.config.AppProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OferenteService {

    private final OferenteRepository oferenteRepository;
    private final OferenteHabilidadRepository oferenteHabilidadRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    private final AppProperties appProperties;

    public OferenteService(OferenteRepository oferenteRepository,
                           OferenteHabilidadRepository oferenteHabilidadRepository,
                           CaracteristicaRepository caracteristicaRepository,
                           AppProperties appProperties) {
        this.oferenteRepository = oferenteRepository;
        this.oferenteHabilidadRepository = oferenteHabilidadRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.appProperties = appProperties;
    }

    public Optional<Oferente> buscarPorUsuarioId(Integer usuarioId) {
        return oferenteRepository.findByUsuarioId(usuarioId);
    }

    public List<OferenteHabilidad> listarHabilidades(Integer usuarioId) {
        List<OferenteHabilidad> habilidades = oferenteHabilidadRepository.findByOferente_UsuarioId(usuarioId);
        habilidades.sort(Comparator.comparing(h -> h.getCaracteristica().getNombreCaracteristica(), String.CASE_INSENSITIVE_ORDER));
        return habilidades;
    }

    public String validarHabilidad(Integer caracteristicaId, Integer nivel) {
        if (caracteristicaId == null) {
            return "Debe seleccionar una característica.";
        }
        if (nivel == null || nivel < 1 || nivel > 5) {
            return "El nivel debe estar entre 1 y 5.";
        }
        return null;
    }

    @Transactional
    public void guardarHabilidad(Integer usuarioId, Integer caracteristicaId, Integer nivel) {
        Oferente oferente = oferenteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el oferente."));
        Caracteristica caracteristica = caracteristicaRepository.findById(caracteristicaId)
                .orElseThrow(() -> new IllegalArgumentException("La característica seleccionada no existe."));

        OferenteHabilidad.OferenteHabilidadId id = new OferenteHabilidad.OferenteHabilidadId(oferente.getUsuarioId(), caracteristicaId);
        OferenteHabilidad habilidad = oferenteHabilidadRepository.findById(id).orElse(new OferenteHabilidad());
        habilidad.setId(id);
        habilidad.setOferente(oferente);
        habilidad.setCaracteristica(caracteristica);
        habilidad.setNivel(nivel);
        oferenteHabilidadRepository.saveAndFlush(habilidad);
    }

    @Transactional
    public void subirCurriculum(Integer usuarioId, MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un archivo PDF.");
        }

        String nombre = StringUtils.cleanPath(archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename());
        String contentType = archivo.getContentType();
        boolean esPdfPorNombre = nombre.toLowerCase().endsWith(".pdf");
        boolean esPdfPorTipo = contentType != null && contentType.equalsIgnoreCase("application/pdf");
        if (!esPdfPorNombre && !esPdfPorTipo) {
            throw new IllegalArgumentException("Solo se permiten archivos en formato PDF.");
        }

        Oferente oferente = oferenteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el oferente."));

        Path carpeta = obtenerDirectorioCv();
        Files.createDirectories(carpeta);

        String nombreArchivo = "cv_" + usuarioId + "_" + UUID.randomUUID() + ".pdf";
        Path destino = carpeta.resolve(nombreArchivo).normalize();
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        if (oferente.getRutaCurriculum() != null && !oferente.getRutaCurriculum().isBlank()) {
            Path anterior = carpeta.resolve(oferente.getRutaCurriculum()).normalize();
            Files.deleteIfExists(anterior);
        }

        oferente.setRutaCurriculum(nombreArchivo);
        oferenteRepository.saveAndFlush(oferente);
    }

    public Path resolverRutaCv(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            return obtenerDirectorioCv().resolve("archivo-no-encontrado.pdf");
        }

        Path actual = obtenerDirectorioCv().resolve(nombreArchivo).normalize();
        if (Files.exists(actual)) {
            return actual;
        }

        Path legacyPrincipal = Paths.get("uploads", "cv").toAbsolutePath().normalize().resolve(nombreArchivo).normalize();
        if (Files.exists(legacyPrincipal)) {
            return legacyPrincipal;
        }

        Path legacySecundario = Paths.get("Proyecto1_BolsaEmpleo", "uploads", "cv").toAbsolutePath().normalize().resolve(nombreArchivo).normalize();
        if (Files.exists(legacySecundario)) {
            return legacySecundario;
        }

        return actual;
    }

    private Path obtenerDirectorioCv() {
        String rutaConfigurada = appProperties.getUploadDir();
        try {
            if (rutaConfigurada != null && !rutaConfigurada.isBlank()) {
                return Paths.get(rutaConfigurada).toAbsolutePath().normalize();
            }
        } catch (InvalidPathException e) {
            // Si la ruta configurada es inválida, se utiliza la ruta por defecto.
        }
        return Paths.get(System.getProperty("user.home"), "bolsa_empleo_uploads", "cv").toAbsolutePath().normalize();
    }
}
