# 📊 GUÍA DEL SISTEMA DE REPORTES - AVITECH

## 🎯 Funcionalidades Implementadas

### ✅ Generación de Reportes en PDF y Excel

El sistema ahora cuenta con un módulo completo de generación de reportes que permite:

1. **Seleccionar tipo de reporte** (7 tipos disponibles)
2. **Elegir formato** (PDF o Excel)
3. **Aplicar filtros** (fechas, lotes, categorías, etc.)
4. **Generar reportes reales** con datos exportables
5. **Vista previa** (opcional)
6. **Historial** de reportes generados

---

## 📋 Tipos de Reportes Disponibles

### 1. **Stock Actual**
- Inventario actual de insumos y productos
- Columnas: Artículo, Categoría, Stock Actual, Stock Mínimo, Ubicación, Estado

### 2. **Registro por Artículo**
- Movimientos detallados de artículos específicos
- Columnas: Fecha, Tipo, Cantidad, Responsable, Observaciones

### 3. **Recibos de Insumos**
- Registro de entradas y compras
- Columnas: Fecha, Proveedor, Artículo, Cantidad, Costo Total

### 4. **Consumo de Alimento**
- Análisis de consumo de alimento por lote
- Columnas: Lote, Período, Consumo Total, Promedio Diario, Aves

### 5. **Aplicaciones Sanitarias**
- Registro de vacunas y tratamientos aplicados
- Columnas: Fecha, Tratamiento, Lote, Dosis, Responsable

### 6. **Producción por Lote/Tamaño**
- Análisis detallado de producción clasificada
- Columnas: Lote, Fecha, Tamaño, Cantidad, Calidad

### 7. **Mortalidad**
- Registro y análisis de casos de mortalidad
- Columnas: Fecha, Lote, Cantidad, Causa, % Mortalidad

---

## 🔧 Arquitectura del Sistema

### Componentes Creados:

```
reportes/
├── ReportesController.java        → Controlador principal de la UI
├── ReporteConfig.java             → Configuración y tipos de reportes
├── ReporteGenerator.java          → Interfaz para generadores
├── PDFReporteGenerator.java       → Generador de PDF (iText7)
├── ExcelReporteGenerator.java     → Generador de Excel (Apache POI)
└── ReporteService.java            → Servicio de gestión de reportes
```

### Dependencias Agregadas (build.gradle):

```groovy
// Generación de reportes PDF
implementation 'com.itextpdf:itext7-core:7.2.5'

// Generación de reportes Excel
implementation 'org.apache.poi:poi:5.2.5'
implementation 'org.apache.poi:poi-ooxml:5.2.5'
```

---

## 🎨 Interfaz de Usuario

### Flujo de Generación de Reportes:

1. **Seleccionar Tipo de Reporte**
   - Hacer clic en uno de los botones de tipo de reporte (Stock Actual, Registro por Artículo, etc.)
   - El sistema muestra un mensaje confirmando la selección

2. **Configurar Filtros** (Opcional)
   - **Fecha Desde/Hasta**: Rango de fechas para el reporte
   - **Lote**: Filtrar por lote específico
   - **Artículo**: Filtrar por artículo específico
   - **Categoría**: Filtrar por categoría
   - **Responsable**: Filtrar por responsable

3. **Elegir Formato**
   - **PDF**: Reporte en formato PDF (profesional)
   - **Excel**: Reporte en formato Excel (editable)

4. **Opciones Adicionales**
   - ☑️ **Vista Previa**: Muestra el reporte antes de guardarlo

5. **Generar Reporte**
   - Clic en "🧾 Generar Reporte"
   - El sistema muestra un diálogo de progreso
   - Al finalizar, ofrece abrir el archivo automáticamente

---

## 📁 Ubicación de Reportes Generados

Los reportes se guardan en:
```
AVITECH/
└── reportes/
    ├── Reporte_STOCK_ACTUAL_20241027_143022.pdf
    ├── Reporte_CONSUMO_ALIMENTO_20241027_143045.xlsx
    └── ...
```

**Formato de nombre:**
```
Reporte_{TIPO}_{FECHA}_{HORA}.{extension}
```

---

## 🔌 Integración con Base de Datos (Futura)

### DataSource Interface

El sistema está preparado para conectarse a la base de datos:

```java
public interface ReportesDataSource {
    List<Map<String, Object>> obtenerStockActual(Map<String, String> filtros);
    List<Map<String, Object>> obtenerMovimientos(String articulo, LocalDate desde, LocalDate hasta);
    List<Map<String, Object>> obtenerRecibos(LocalDate desde, LocalDate hasta);
    List<Map<String, Object>> obtenerConsumoAlimento(Map<String, String> filtros);
    List<Map<String, Object>> obtenerAplicacionesSanitarias(LocalDate desde, LocalDate hasta);
    List<Map<String, Object>> obtenerProduccion(Map<String, String> filtros);
    List<Map<String, Object>> obtenerMortalidad(LocalDate desde, LocalDate hasta);
}
```

### Cómo Conectar la Base de Datos:

1. **Crear implementación del DataSource**:
```java
public class MySQLReportesDataSource implements ReportesDataSource {
    private final DataSource dataSource;
    
    @Override
    public List<Map<String, Object>> obtenerStockActual(Map<String, String> filtros) {
        // Ejecutar consulta SQL
        String sql = "SELECT * FROM inventario WHERE ...";
        // Retornar resultados
    }
    // ... más métodos
}
```

2. **Inyectar en el servicio**:
```java
ReporteService reporteService = new ReporteService();
reporteService.setDataSource(new MySQLReportesDataSource(dataSource));
```

---

## 📊 Estructura de Datos Esperada

### Ejemplo de Map para Stock Actual:
```java
Map<String, Object> item = Map.of(
    "articulo", "Alimento Balanceado Premium",
    "categoria", "Alimentos",
    "stock_actual", "500 kg",
    "stock_minimo", "200 kg",
    "ubicacion", "Almacén Principal",
    "estado", "Normal"
);
```

### Ejemplo de Map para Movimientos:
```java
Map<String, Object> movimiento = Map.of(
    "fecha", "15/10/2024",
    "tipo", "Entrada",
    "cantidad", "+500 kg",
    "responsable", "Juan Pérez",
    "observaciones", "Compra mensual"
);
```

---

## 🎯 Características Técnicas

### PDF (iText7):
- ✅ Tablas con formato profesional
- ✅ Encabezados con colores
- ✅ Filtros aplicados en el reporte
- ✅ Pie de página automático
- ✅ Fecha de generación
- ✅ Logo y marca AVITECH

### Excel (Apache POI):
- ✅ Formato XLSX (Excel 2007+)
- ✅ Encabezados con estilo
- ✅ Bordes y colores
- ✅ Autoajuste de columnas
- ✅ Múltiples hojas (futuro)
- ✅ Fórmulas (futuro)

### Generación Asíncrona:
- ✅ No bloquea la interfaz
- ✅ Barra de progreso
- ✅ Manejo de errores
- ✅ Feedback al usuario

---

## 🚀 Modo Actual (Sin BD)

Actualmente, el sistema funciona con **datos de ejemplo** para demostración:

- ✅ Genera reportes reales en PDF y Excel
- ✅ Aplica todos los filtros configurados
- ✅ Muestra estructura completa del reporte
- ✅ Los datos de ejemplo son representativos del sistema

**Cuando se conecte la BD:**
- Los datos de ejemplo serán reemplazados automáticamente
- No se requieren cambios en la UI
- Solo se implementa el DataSource

---

## 📝 Tabla de Reportes Recientes

La tabla muestra:
- **Nombre**: Tipo de reporte y formato
- **Fecha**: Fecha y hora de generación
- **Tamaño**: Tamaño del archivo
- **Acciones**: Botón para abrir el reporte

---

## 🎨 KPIs del Módulo

- **Reportes Este Mes**: Contador de reportes generados
- **Más Solicitado**: Tipo de reporte más generado
- **Formatos Populares**: Distribución PDF vs Excel
- **Tiempo Promedio**: Tiempo de generación promedio

---

## ⚡ Próximas Mejoras

### Corto Plazo:
- [ ] Conectar con base de datos real
- [ ] Agregar más filtros dinámicos
- [ ] Implementar gráficos en reportes
- [ ] Exportar a CSV

### Mediano Plazo:
- [ ] Reportes programados (automáticos)
- [ ] Envío por correo electrónico
- [ ] Reportes personalizados por usuario
- [ ] Dashboard de analíticas

### Largo Plazo:
- [ ] Reportes con BI (Business Intelligence)
- [ ] Machine Learning para predicciones
- [ ] Reportes en la nube
- [ ] API REST para reportes

---

## 🐛 Solución de Problemas

### El reporte no se abre automáticamente
**Solución**: Verificar que el sistema tenga un programa asociado para PDF/Excel

### Error al generar reporte
**Posibles causas**:
1. Permisos de escritura en carpeta `reportes/`
2. Dependencias no descargadas (ejecutar `gradlew build`)
3. Filtros inválidos

### Datos vacíos en el reporte
**Explicación**: Sin conexión a BD, se usan datos de ejemplo. Esto es normal en modo demo.

---

## 📞 Soporte

Para más información o problemas, contactar al equipo de desarrollo.

**Sistema desarrollado por AVITECH © 2024**

