package com.avitech.sia.iu.dashboard;

import java.util.List;

/**
 * Interfaz genérica para obtener datos del Tablero Principal (Dashboard).
 * Se implementará más adelante con una fuente real (p. ej. MySQL).
 */
public interface DashboardDataSource {

    /** KPIs principales del sistema */
    record Kpis(
            Integer avesActivas, Integer deltaAves,
            Integer huevosDia, Integer deltaHuevosDia,
            Integer suministros, Integer deltaSuministros,
            Integer alertas, Integer deltaAlertas
    ) {}

    /** Punto del gráfico de producción semanal */
    record Point(String label, int value) {}

    /** Producción de un galpón */
    record Coop(int num, int current, int target) {}

    /** Alerta reciente */
    record Alert(String when, String message) {}

    // ===== Métodos que deben implementarse =====

    /** Devuelve los KPIs actuales del sistema */
    Kpis getKpis();

    /** Devuelve la producción semanal (por día) */
    List<Point> getWeeklyProduction();

    /** Devuelve la producción actual por galpón */
    List<Coop> getCoopsProduction();

    /** Devuelve las alertas recientes (máximo 10) */
    List<Alert> getRecentAlerts();
}
