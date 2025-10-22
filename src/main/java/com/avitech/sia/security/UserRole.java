package com.avitech.sia.security;

/**
 * Roles de usuario del sistema con sus permisos de acceso a módulos.
 */
public enum UserRole {
    /** Administrador: acceso completo a todos los módulos */
    ADMIN,

    /** Operador: acceso limitado (Dashboard, Suministros, Sanidad, Producción) */
    OPERADOR,

    /** Supervisor: acceso intermedio (Dashboard, Suministros, Sanidad, Producción, Reportes, Alertas, Auditoría) */
    SUPERVISOR;

    /**
     * Verifica si este rol tiene acceso a un módulo específico.
     */
    public boolean hasAccessTo(Module module) {
        return switch (this) {
            case ADMIN -> true; // Acceso total
            case OPERADOR -> switch (module) {
                case DASHBOARD, SUMINISTROS, SANIDAD, PRODUCCION -> true;
                default -> false;
            };
            case SUPERVISOR -> switch (module) {
                case DASHBOARD, SUMINISTROS, SANIDAD, PRODUCCION,
                     REPORTES, ALERTAS, AUDITORIA -> true;
                default -> false;
            };
        };
    }

    /**
     * Módulos del sistema.
     */
    public enum Module {
        DASHBOARD,
        SUMINISTROS,
        SANIDAD,
        PRODUCCION,
        REPORTES,
        ALERTAS,
        AUDITORIA,
        PARAMETROS,
        USUARIOS,
        RESPALDOS
    }
}

