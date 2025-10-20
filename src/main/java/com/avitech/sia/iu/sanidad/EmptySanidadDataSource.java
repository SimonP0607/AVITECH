package com.avitech.sia.iu.sanidad;

import com.avitech.sia.iu.sanidad.dto.AplicacionDTO;
import com.avitech.sia.iu.sanidad.dto.EventoDTO;

import java.util.List;

public class EmptySanidadDataSource implements SanidadDataSource {

    @Override public List<String> findLotes() { return List.of("Galpón 1", "Galpón 2", "Galpón 3"); }
    @Override public List<String> findMedicamentos() { return List.of("Vacuna Newcastle", "Antibiótico Respiratorio", "Vitamina E+Selenio"); }
    @Override public List<String> findViasAplicacion() { return List.of("Oral (agua)", "Inyección", "Aerosol"); }
    @Override public List<String> findResponsables() { return List.of("Dr. María González", "Dr. Carlos Ruiz", "Téc. Ana López"); }
    @Override public List<String> findTiposEvento() { return List.of("Enfermedad", "Mortalidad", "Otro"); }

    @Override public long saveAplicacion(AplicacionDTO dto) { return 1L; }
    @Override public long saveEvento(EventoDTO dto) { return 1L; }
}
