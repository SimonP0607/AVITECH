package com.avitech.sia.iu.suministros;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Puente de la capa de UI (JavaFX) con la fuente real de datos (BD/MySQL).
 * No contiene lógica de UI: solo contratos de lectura/escritura.
 */
public interface SuministrosDataSource {

    /* ======== DTOs / Records ======== */

    /** KPIs mostrados en la vista principal de Suministros */
    record Kpis(
            Integer movimientosHoy,
            Integer productosActivos,
            Integer stockBajo,
            BigDecimal valorTotalStock
    ) {}

    /** Movimiento de stock (fila de la tabla principal) */
    record Movimiento(
            Long id,
            LocalDateTime fechaHora,
            String item,
            BigDecimal cantidad,
            String unidad,
            String tipo,           // "Entrada" | "Salida" | "Transferencia"
            String responsable,
            String detalles,       // libre (lote, motivo, ubicación, etc.)
            String stockPosterior  // ej: "Anterior: 200 / Actual: 700"
    ) {}

    /** Filtro para buscar movimientos */
    record MovFilter(
            String query,          // texto libre (item, responsable, proveedor…)
            String tipo,           // "Todos" | "Entrada" | "Salida" | "Transferencia"
            Long responsableId,    // null => cualquiera
            LocalDate desde,       // null => sin desde
            LocalDate hasta        // null => sin hasta
    ) {}

    /** Item de inventario (para "Ver Stock") */
    record StockItem(
            String codigo,
            String nombre,
            String categoria,
            String lote,           // puede ser null
            LocalDate vence,       // puede ser null
            String ubicacion,
            BigDecimal disponible,
            BigDecimal minimo
    ) {}

    /** Filtro para stock */
    record StockFilter(
            String query,          // por código/nombre
            String categoria,      // "Todas" o categoría exacta
            String ubicacion       // "Todas" o ubicación exacta
    ) {}

    /** Catálogos básicos */
    record Producto(Long id, String nombre, String unidadDefecto) {}
    record Ubicacion(Long id, String nombre) {}
    record Responsable(Long id, String nombre) {}

    /* ======== Entradas ======== */
    record EntradaHeader(LocalDate fecha, String proveedor, Long ubicacionId, String observacion) {}
    record EntradaLine(Long productoId, BigDecimal cantidad, String unidad,
                       BigDecimal costoUnitario, String lote, LocalDate vence) {}

    /* ======== Salidas ======== */
    record SalidaHeader(LocalDate fecha, String tipo, Long ubicacionOrigenId, String destino, String observacion) {}
    record SalidaLine(Long productoId, BigDecimal cantidad, String unidad) {}

    /* ======== Transferencias ======== */
    record TransferReq(Long productoId, BigDecimal cantidad, Long desdeUbicacionId,
                       Long haciaUbicacionId, String motivo) {}

    /* ======== Operaciones de lectura ======== */

    Kpis getKpis();

    List<Movimiento> getMovimientos(MovFilter filter);

    List<StockItem> getStock(StockFilter filter);

    List<Producto>   getProductos();
    List<Ubicacion>  getUbicaciones();
    List<Responsable> getResponsables();
    List<String>     getUnidades();   // ej: ["kg","L","unid"]

    List<String>     getCategorias(); // ej: ["Alimento","Sanidad","Vitaminas","Insumos"]

    /* ======== Operaciones de escritura ======== */

    /** Persiste una entrada (cabecera + detalle). Debe ser transaccional. */
    boolean saveEntrada(EntradaHeader header, List<EntradaLine> detalle);

    /** Persiste una salida (cabecera + detalle). Debe validar stock >= 0. */
    boolean saveSalida(SalidaHeader header, List<SalidaLine> detalle);

    /** Transfiere stock entre ubicaciones sin cambiar el stock total del producto. */
    boolean transferir(TransferReq req);
}
