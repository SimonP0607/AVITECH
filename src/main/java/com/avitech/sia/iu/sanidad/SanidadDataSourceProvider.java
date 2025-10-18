package com.avitech.sia.iu.sanidad;

/**
 * Proveedor estático para la fuente de datos de Sanidad.
 * Por defecto usa EmptySanidadDataSource; cuando tengas MySQL,
 * haces: SanidadDataSourceProvider.set(new JdbcSanidadDataSource(conn));
 */
public final class SanidadDataSourceProvider {

    private static volatile SanidadDataSource INSTANCE = new EmptySanidadDataSource();

    private SanidadDataSourceProvider() { }

    public static SanidadDataSource get() {
        return INSTANCE;
    }

    public static void set(SanidadDataSource impl) {
        if (impl == null) throw new IllegalArgumentException("impl == null");
        INSTANCE = impl;
    }
}

