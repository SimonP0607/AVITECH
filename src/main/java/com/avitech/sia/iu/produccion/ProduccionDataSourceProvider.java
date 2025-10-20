package com.avitech.sia.iu.produccion;

/** Proveedor estático para inyectar la implementación (MySQL, mock, etc.). */
public final class ProduccionDataSourceProvider {
    private static ProduccionDataSource INSTANCE = new ProduccionDataSource() {
        @Override public java.util.List<String> findGalpones() { return java.util.List.of("Galpón 1","Galpón 2","Galpón 3"); }
        @Override public ProduccionKpis getKpis() { return new ProduccionKpis("—","—","—","—"); }
        @Override public java.util.List<ProduccionDTO> listarDiario(String g){ return java.util.List.of(); }
        @Override public long saveRegistro(ProduccionDTO dto){ return 1L; }
    };

    private ProduccionDataSourceProvider(){}

    public static ProduccionDataSource get() { return INSTANCE; }
    public static void set(ProduccionDataSource ds) { INSTANCE = ds; }
}
