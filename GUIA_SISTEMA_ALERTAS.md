# Sistema de Alertas - Guía de Implementación de Base de Datos

## Resumen

El módulo de Alertas ha sido completamente reconstruido con la arquitectura correcta siguiendo el patrón de diseño del proyecto. Está listo para conectarse a la base de datos MySQL.

## Estructura de Archivos Creados

```
src/main/java/com/avitech/sia/iu/alertas/
├── AlertasController.java          (Controlador JavaFX completo)
├── AlertasDataSource.java          (Interface para acceso a datos)
├── EmptyAlertasDataSource.java     (Implementación vacía por defecto)
└── UsesAlertasDataSource.java      (Interface de inyección)

src/main/resources/fxml/alertas/
└── alertas.fxml                    (UI corregida y completa)
```

## Esquema de Base de Datos Recomendado

### Tabla: alertas

```sql
CREATE TABLE alertas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo VARCHAR(50) NOT NULL,              -- 'STOCK_BAJO', 'VENCIMIENTO', 'SANITARIO', 'PRODUCCION', 'SISTEMA'
    prioridad VARCHAR(20) NOT NULL,         -- 'CRITICA', 'ALTA', 'MEDIA', 'BAJA'
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    fecha_generacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',  -- 'ACTIVA', 'ATENDIDA', 'DESCARTADA'
    modulo VARCHAR(50) NOT NULL,            -- 'Suministros', 'Sanidad', 'Producción', 'Sistema'
    detalles TEXT,                          -- JSON o información adicional
    atendida_por BIGINT,                    -- FK a usuarios
    fecha_atencion DATETIME,
    comentario_atencion TEXT,
    creada_por BIGINT,                      -- FK a usuarios
    INDEX idx_estado (estado),
    INDEX idx_prioridad (prioridad),
    INDEX idx_fecha (fecha_generacion),
    INDEX idx_modulo (modulo),
    FOREIGN KEY (atendida_por) REFERENCES usuarios(id),
    FOREIGN KEY (creada_por) REFERENCES usuarios(id)
);
```

### Tabla: reglas_alerta

```sql
CREATE TABLE reglas_alerta (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,              -- Tipo de alerta que genera
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    condicion TEXT NOT NULL,                -- Descripción de la condición
    dias_anticipacion INT,                  -- Para vencimientos
    umbral INT,                             -- Para stock bajo (porcentaje o cantidad)
    destinatarios TEXT,                     -- JSON con usuarios/roles que reciben la alerta
    ultima_ejecucion DATETIME,
    proxima_ejecucion DATETIME,
    creada_por BIGINT,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creada_por) REFERENCES usuarios(id)
);
```

### Tabla: historial_alertas (opcional, para auditoría)

```sql
CREATE TABLE historial_alertas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    alerta_id BIGINT NOT NULL,
    accion VARCHAR(50) NOT NULL,            -- 'CREADA', 'ATENDIDA', 'DESCARTADA', 'MODIFICADA'
    usuario_id BIGINT,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion TEXT,
    FOREIGN KEY (alerta_id) REFERENCES alertas(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
```

## Implementación del DataSource con MySQL

Crear la clase `MySQLAlertasDataSource.java` en el mismo paquete:

```java
package com.avitech.sia.iu.alertas;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySQLAlertasDataSource implements AlertasDataSource {
    
    private final Connection connection;
    
    public MySQLAlertasDataSource(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Kpis getKpis() {
        String sql = """
            SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN prioridad = 'CRITICA' THEN 1 ELSE 0 END) as criticas,
                SUM(CASE WHEN prioridad IN ('ALTA', 'MEDIA') THEN 1 ELSE 0 END) as advertencias,
                SUM(CASE WHEN prioridad = 'BAJA' THEN 1 ELSE 0 END) as informativas
            FROM alertas 
            WHERE estado = 'ACTIVA'
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new Kpis(
                    rs.getInt("total"),
                    rs.getInt("criticas"),
                    rs.getInt("advertencias"),
                    rs.getInt("informativas")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Kpis(0, 0, 0, 0);
    }
    
    @Override
    public List<Alerta> getAlertas(AlertaFilter filter) {
        List<Alerta> alertas = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT id, tipo, prioridad, titulo, descripcion, 
                   fecha_generacion, fecha_vencimiento, estado, modulo, detalles
            FROM alertas 
            WHERE 1=1
            """);
        
        List<Object> params = new ArrayList<>();
        
        if (filter.query() != null && !filter.query().isBlank()) {
            sql.append(" AND (titulo LIKE ? OR descripcion LIKE ?)");
            String q = "%" + filter.query() + "%";
            params.add(q);
            params.add(q);
        }
        
        if (filter.tipo() != null && !"Todas".equals(filter.tipo())) {
            sql.append(" AND tipo = ?");
            params.add(filter.tipo());
        }
        
        if (filter.prioridad() != null && !"Todas".equals(filter.prioridad())) {
            sql.append(" AND prioridad = ?");
            params.add(filter.prioridad());
        }
        
        if (filter.estado() != null && !"Todas".equals(filter.estado())) {
            sql.append(" AND estado = ?");
            params.add(filter.estado());
        }
        
        if (filter.modulo() != null && !"Todos".equals(filter.modulo())) {
            sql.append(" AND modulo = ?");
            params.add(filter.modulo());
        }
        
        if (filter.desde() != null) {
            sql.append(" AND DATE(fecha_generacion) >= ?");
            params.add(filter.desde());
        }
        
        if (filter.hasta() != null) {
            sql.append(" AND DATE(fecha_generacion) <= ?");
            params.add(filter.hasta());
        }
        
        sql.append(" ORDER BY fecha_generacion DESC LIMIT 500");
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertas.add(new Alerta(
                        rs.getLong("id"),
                        rs.getString("tipo"),
                        rs.getString("prioridad"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getTimestamp("fecha_generacion").toLocalDateTime(),
                        rs.getDate("fecha_vencimiento") != null ? 
                            rs.getDate("fecha_vencimiento").toLocalDate() : null,
                        rs.getString("estado"),
                        rs.getString("modulo"),
                        rs.getString("detalles")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return alertas;
    }
    
    @Override
    public void marcarAtendida(Long alertaId, String comentario) {
        String sql = """
            UPDATE alertas 
            SET estado = 'ATENDIDA', 
                fecha_atencion = NOW(),
                comentario_atencion = ?
            WHERE id = ?
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, comentario);
            stmt.setLong(2, alertaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void descartarAlerta(Long alertaId, String motivo) {
        String sql = """
            UPDATE alertas 
            SET estado = 'DESCARTADA',
                comentario_atencion = ?
            WHERE id = ?
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, motivo);
            stmt.setLong(2, alertaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Long crearAlertaManual(String tipo, String prioridad, String titulo, 
                                  String descripcion, String modulo) {
        String sql = """
            INSERT INTO alertas (tipo, prioridad, titulo, descripcion, modulo, estado)
            VALUES (?, ?, ?, ?, ?, 'ACTIVA')
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tipo);
            stmt.setString(2, prioridad);
            stmt.setString(3, titulo);
            stmt.setString(4, descripcion);
            stmt.setString(5, modulo);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1L;
    }
    
    @Override
    public List<ReglaAlerta> getReglasAlerta() {
        List<ReglaAlerta> reglas = new ArrayList<>();
        String sql = """
            SELECT id, nombre, tipo, activa, condicion, 
                   dias_anticipacion, umbral, destinatarios
            FROM reglas_alerta
            ORDER BY nombre
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reglas.add(new ReglaAlerta(
                    rs.getLong("id"),
                    rs.getString("nombre"),
                    rs.getString("tipo"),
                    rs.getBoolean("activa"),
                    rs.getString("condicion"),
                    rs.getObject("dias_anticipacion", Integer.class),
                    rs.getObject("umbral", Integer.class),
                    rs.getString("destinatarios")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reglas;
    }
    
    @Override
    public void toggleRegla(Long reglaId, boolean activa) {
        String sql = "UPDATE reglas_alerta SET activa = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, activa);
            stmt.setLong(2, reglaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<EstadisticaModulo> getEstadisticasPorModulo() {
        List<EstadisticaModulo> stats = new ArrayList<>();
        String sql = """
            SELECT 
                modulo,
                COUNT(*) as total,
                SUM(CASE WHEN prioridad = 'CRITICA' THEN 1 ELSE 0 END) as criticas,
                SUM(CASE WHEN estado = 'ATENDIDA' THEN 1 ELSE 0 END) as atendidas
            FROM alertas
            GROUP BY modulo
            ORDER BY modulo
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stats.add(new EstadisticaModulo(
                    rs.getString("modulo"),
                    rs.getInt("total"),
                    rs.getInt("criticas"),
                    rs.getInt("atendidas")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    // Implementar métodos restantes de la interface...
    @Override
    public List<Alerta> getAlertasCriticas() {
        return getAlertas(new AlertaFilter("", "Todas", "CRITICA", "ACTIVA", "Todos", null, null));
    }
    
    @Override
    public List<String> getTiposAlerta() {
        return List.of("Todas", "STOCK_BAJO", "VENCIMIENTO", "SANITARIO", "PRODUCCION", "SISTEMA");
    }
    
    @Override
    public List<String> getModulos() {
        return List.of("Todos", "Suministros", "Sanidad", "Producción", "Sistema");
    }
    
    @Override
    public Long guardarRegla(ReglaAlerta regla) {
        // TODO: Implementar
        return -1L;
    }
    
    @Override
    public void eliminarRegla(Long reglaId) {
        // TODO: Implementar
    }
    
    @Override
    public int procesarAlertasAutomaticas() {
        // TODO: Implementar lógica de procesamiento automático
        return 0;
    }
}
```

## Inyección del DataSource

En tu clase principal o donde inicialices el controlador:

```java
// Cargar el FXML
FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/alertas/alertas.fxml"));
Parent root = loader.load();

// Obtener el controlador
AlertasController controller = loader.getController();

// Inyectar el DataSource con conexión MySQL
Connection connection = // ... tu conexión MySQL
MySQLAlertasDataSource dataSource = new MySQLAlertasDataSource(connection);
controller.setDataSource(dataSource);

// Mostrar la escena
Scene scene = new Scene(root);
stage.setScene(scene);
stage.show();
```

## Funcionalidades Implementadas

### UI Completa
- ✅ KPIs: Alertas activas, críticas, advertencias, informativas
- ✅ Filtros avanzados: búsqueda, tipo, prioridad, estado, módulo, fechas
- ✅ Tabla de alertas con acciones: Ver, Atender, Descartar
- ✅ Tabla de reglas de alerta con activación/desactivación
- ✅ Estadísticas por módulo
- ✅ Botones de acción: Nueva alerta, Procesar automáticas, Exportar

### Controlador
- ✅ Manejo completo de eventos
- ✅ Navegación entre módulos
- ✅ Diálogos de confirmación
- ✅ Actualización dinámica de datos
- ✅ Filtrado y búsqueda en tiempo real

### DataSource
- ✅ Interface completa con todos los métodos necesarios
- ✅ DTOs/Records para transferencia de datos
- ✅ Implementación vacía para desarrollo sin BD
- ✅ Lista para implementación MySQL

## Próximos Pasos

1. **Crear las tablas en MySQL** usando los scripts SQL proporcionados
2. **Implementar MySQLAlertasDataSource** completo
3. **Configurar la inyección** del DataSource en la aplicación
4. **Implementar procesamiento automático** de alertas:
   - Stock bajo (comparar con mínimos)
   - Vencimientos próximos (7, 15, 30 días)
   - Eventos sanitarios pendientes
5. **Agregar notificaciones** en tiempo real
6. **Implementar exportación** a Excel/PDF

## Notas Importantes

- El módulo sigue el mismo patrón de diseño que Suministros y Sanidad
- Está completamente desacoplado de la base de datos
- Fácil de testear y mantener
- Preparado para agregar funcionalidades futuras

