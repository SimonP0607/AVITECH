package com.avitech.sia.iu.usuarios;

import com.avitech.sia.iu.usuarios.dto.UsuarioDTO;
import com.avitech.sia.iu.usuarios.UsuariosController.UserRow;

import java.util.List;
import java.util.Map;

public interface UsersDataSource {

    // ===== Catálogos =====
    List<String> listRoles();
    List<String> listEstados();

    /** Descripciones por rol (opcional). Puedes devolver Map.of() si no lo usas aún. */
    default Map<String, String> roleDescriptions() { return Map.of(); }

    // ===== Consultas =====
    List<UserRow> searchUsers(String query, String rol, String estado);

    // ===== Escrituras =====
    long createUser(UsuarioDTO dto) throws Exception;
    void resetPassword(long userId) throws Exception;
    void deleteUser(long userId) throws Exception;
}
