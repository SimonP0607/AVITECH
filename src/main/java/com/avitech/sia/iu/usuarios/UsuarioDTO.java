package com.avitech.sia.iu.usuarios.dto;

public record UsuarioDTO(
        String nombreCompleto,
        String usuario,
        String estado,
        String rol,
        String descripcionRol,
        String passwordPlain
) {}
