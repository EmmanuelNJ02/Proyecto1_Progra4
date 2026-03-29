package com.example.proyecto1.services;

import com.example.proyecto1.models.Puesto;
import com.example.proyecto1.models.Usuario;
import com.example.proyecto1.repositories.PuestoRepository;
import com.example.proyecto1.repositories.UsuarioRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AdminService {
    private final UsuarioRepository usuarioRepository;
    private final PuestoRepository puestoRepository;

    public AdminService(UsuarioRepository usuarioRepository, PuestoRepository puestoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.puestoRepository = puestoRepository;
    }

    public List<Usuario> listarEmpresasPendientes() {
        return usuarioRepository.findByRolAndEstado(Usuario.Rol.EMPRESA, Usuario.Estado.PENDIENTE);
    }

    public List<Usuario> listarOferentesPendientes() {
        return usuarioRepository.findByRolAndEstado(Usuario.Rol.OFERENTE, Usuario.Estado.PENDIENTE);
    }

    public void aprobarUsuario(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario != null) {
            usuario.setEstado(Usuario.Estado.APROBADO);
            usuarioRepository.save(usuario);
        }
    }

    public Map<String, Long> obtenerPuestosPorMes() {
        Map<String, Long> datos = new LinkedHashMap<>();
        List<Puesto> puestos = puestoRepository.findAll();

        for (Month month : Month.values()) {
            long cantidad = puestos.stream()
                    .filter(p -> p.getFechaPublicacion() != null && p.getFechaPublicacion().getMonth() == month)
                    .count();
            String nombreMes = month.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            datos.put(capitalizar(nombreMes), cantidad);
        }
        return datos;
    }

    public Map<String, List<Puesto>> obtenerDetallePuestosPorMes() {
        Map<String, List<Puesto>> detalle = new LinkedHashMap<>();
        List<Puesto> puestos = new ArrayList<>(puestoRepository.findAll());
        puestos.sort(Comparator.comparing(Puesto::getFechaPublicacion, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        for (Month month : Month.values()) {
            String nombreMes = capitalizar(month.getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
            List<Puesto> puestosMes = new ArrayList<>();
            for (Puesto puesto : puestos) {
                if (puesto.getFechaPublicacion() != null && puesto.getFechaPublicacion().getMonth() == month) {
                    puestosMes.add(puesto);
                }
            }
            detalle.put(nombreMes, puestosMes);
        }
        return detalle;
    }

    public void generarReportePdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte-puestos.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font encabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

        document.add(new Paragraph("Reporte PDF de puestos solicitados por mes", titulo));
        document.add(new Paragraph("Resumen general de publicaciones registradas en el sistema.", subtitulo));
        document.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3f, 1.5f});

        PdfPCell celdaMes = new PdfPCell(new Phrase("Mes", encabezado));
        PdfPCell celdaCantidad = new PdfPCell(new Phrase("Cantidad de puestos", encabezado));
        tabla.addCell(celdaMes);
        tabla.addCell(celdaCantidad);

        long total = 0;
        for (Map.Entry<String, Long> entry : obtenerPuestosPorMes().entrySet()) {
            tabla.addCell(entry.getKey());
            tabla.addCell(String.valueOf(entry.getValue()));
            total += entry.getValue();
        }

        document.add(tabla);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total de puestos registrados: " + total, encabezado));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Detalle de puestos por mes", encabezado));
        document.add(new Paragraph("Se listan las publicaciones registradas para verificar que el reporte contempla los puestos creados en el sistema.", subtitulo));
        document.add(new Paragraph(" "));

        Map<String, List<Puesto>> detalle = obtenerDetallePuestosPorMes();
        for (Map.Entry<String, List<Puesto>> entry : detalle.entrySet()) {
            document.add(new Paragraph(entry.getKey(), encabezado));
            List<Puesto> puestosMes = entry.getValue();
            if (puestosMes.isEmpty()) {
                document.add(new Paragraph("No hay puestos registrados en este mes.", subtitulo));
                document.add(new Paragraph(" "));
                continue;
            }

            PdfPTable tablaDetalle = new PdfPTable(5);
            tablaDetalle.setWidthPercentage(100);
            tablaDetalle.setWidths(new float[]{0.8f, 3.5f, 1.5f, 1.2f, 1.0f});
            tablaDetalle.addCell(new PdfPCell(new Phrase("ID", encabezado)));
            tablaDetalle.addCell(new PdfPCell(new Phrase("Descripción", encabezado)));
            tablaDetalle.addCell(new PdfPCell(new Phrase("Empresa", encabezado)));
            tablaDetalle.addCell(new PdfPCell(new Phrase("Tipo", encabezado)));
            tablaDetalle.addCell(new PdfPCell(new Phrase("Activo", encabezado)));

            for (Puesto puesto : puestosMes) {
                tablaDetalle.addCell(String.valueOf(puesto.getId()));
                tablaDetalle.addCell(puesto.getDescripcionGeneral() != null ? puesto.getDescripcionGeneral() : "");
                tablaDetalle.addCell(puesto.getEmpresa() != null ? puesto.getEmpresa().getNombreEmpresa() : "");
                tablaDetalle.addCell(puesto.getTipoPublicacion() != null ? puesto.getTipoPublicacion().name() : "");
                tablaDetalle.addCell(Boolean.TRUE.equals(puesto.getActivo()) ? "Sí" : "No");
            }

            document.add(tablaDetalle);
            document.add(new Paragraph(" "));
        }

        document.close();
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}
