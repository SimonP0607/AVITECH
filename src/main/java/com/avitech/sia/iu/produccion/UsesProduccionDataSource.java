package com.avitech.sia.iu.produccion;

public interface UsesProduccionDataSource {
    default ProduccionDataSource produccionDs() {
        return ProduccionDataSourceProvider.get();
    }
}
