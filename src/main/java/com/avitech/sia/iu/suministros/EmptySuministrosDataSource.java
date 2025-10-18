package com.avitech.sia.iu.suministros;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementación "vacía" para correr la UI sin BD.
 * Devuelve colecciones vacías/valores neutros y "true" en escrituras.
 */
public class EmptySuministrosDataSource implements SuministrosDataSource {

    @Override
    public Kpis getKpis() {
        return new Kpis(0, 0, 0, BigDecimal.ZERO);
    }

    @Override
    public List<Movimiento> getMovimientos(MovFilter filter) {
        return List.of(); // sin datos
    }

    @Override
    public List<StockItem> getStock(StockFilter filter) {
        return List.of(); // sin datos
    }

    @Override
    public List<Producto> getProductos() {
        return List.of(
                // Puedes dejar alguno para pruebas manuales
                new Producto(1L, "Concentrado Ponedoras", "kg"),
                new Producto(2L, "Maíz Amarillo", "kg"),
                new Producto(3L, "Vitamina D", "L"),
                new Producto(4L, "Desinfectante Ambiental", "L")
        );
    }

    @Override
    public List<Ubicacion> getUbicaciones() {
        return List.of(
                new Ubicacion(1L, "Almacén Principal"),
                new Ubicacion(2L, "Almacén Medicamentos"),
                new Ubicacion(3L, "Almacén Sanitario"),
                new Ubicacion(10L, "Galpón 1"),
                new Ubicacion(11L, "Galpón 2"),
                new Ubicacion(12L, "Galpón 3")
        );
    }

    @Override
    public List<Responsable> getResponsables() {
        return List.of(
                new Responsable(1L, "María García"),
                new Responsable(2L, "Juan Pérez"),
                new Responsable(3L, "Carlos Ruiz"),
                new Responsable(4L, "Luis Torres")
        );
    }

    @Override
    public List<String> getUnidades() {
        return List.of("kg", "L", "unid");
    }

    @Override
    public List<String> getCategorias() {
        return List.of("Alimento", "Sanidad", "Vitaminas", "Insumos");
    }

    @Override
    public boolean saveEntrada(EntradaHeader header, List<EntradaLine> detalle) {
        // TODO: validar y persistir en BD en la implementación real
        if (header == null || detalle == null || detalle.isEmpty()) return false;
        if (header.fecha() == null || header.ubicacionId() == null) return false;
        return true; // simulamos éxito
    }

    @Override
    public boolean saveSalida(SalidaHeader header, List<SalidaLine> detalle) {
        // TODO: validar y persistir en BD en la implementación real
        if (header == null || detalle == null || detalle.isEmpty()) return false;
        if (header.fecha() == null || header.ubicacionOrigenId() == null) return false;
        return true; // simulamos éxito
    }

    @Override
    public boolean transferir(TransferReq req) {
        // TODO: validar y persistir en BD en la implementación real
        if (req == null) return false;
        if (req.productoId() == null || req.desdeUbicacionId() == null || req.haciaUbicacionId() == null) return false;
        if (req.cantidad() == null || req.cantidad().signum() <= 0) return false;
        if (req.desdeUbicacionId().equals(req.haciaUbicacionId())) return false;
        return true; // simulamos éxito
    }
}
