package com.avitech.sia.iu.sanidad;

public interface UsesSanidadDataSource {
    default SanidadDataSource sanidadDs() {
        return SanidadDataSourceProvider.get();
    }
}
