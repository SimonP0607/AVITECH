package com.avitech.sia.iu.dashboard;

import java.util.*;

/**
 * Implementación vacía/segura de DashboardDataSource.
 * Muestra valores nulos o cero en todos los campos.
 * Útil para ejecutar la app sin base de datos.
 */
public class EmptyDashboardDataSource implements DashboardDataSource {

    @Override
    public Kpis getKpis() {
        return new Kpis(null, null, null, null, null, null, null, null);
    }

    @Override
    public List<Point> getWeeklyProduction() {
        return Collections.emptyList();
    }

    @Override
    public List<Coop> getCoopsProduction() {
        return Arrays.asList(
                new Coop(1, 0, 0),
                new Coop(2, 0, 0),
                new Coop(3, 0, 0),
                new Coop(4, 0, 0),
                new Coop(5, 0, 0),
                new Coop(6, 0, 0)
        );
    }

    @Override
    public List<Alert> getRecentAlerts() {
        return Collections.emptyList();
    }
}