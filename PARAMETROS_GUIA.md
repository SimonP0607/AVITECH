# Sección de Parámetros - Guía Técnica

## 📋 Descripción General

La sección de **Parámetros** es el centro de configuración del sistema AVITECH SIA, donde se gestionan todos los datos maestros necesarios para el funcionamiento de la avícola.

## 🎯 Módulos Configurados

### 1️⃣ **Unidades de Medida**
- **Ubicación**: `parametros_unidades.fxml` + `UnidadesController.java`
- **Propósito**: Configurar unidades utilizadas en el sistema (kg, g, L, mL, dosis, etc.)
- **Campos**:
  - Nombre
  - Símbolo
  - Tipo (Peso, Volumen, Cantidad)
  - Descripción
  - Estado (Activo/Inactivo)

### 2️⃣ **Categorías de Productos**
- **Ubicación**: `parametros_categorias.fxml` + `CategoriasController.java`
- **Propósito**: Organizar productos por categorías (Alimentos, Medicamentos, Insumos, etc.)
- **Campos**:
  - Nombre
  - Tipo (Insumo, Producto, Alimento, Medicamento)
  - Color (identificador visual)
  - Descripción
  - Estado

### 3️⃣ **Medicamentos y Vacunas**
- **Ubicación**: `parametros_medicamentos.fxml` + `MedicamentosController.java`
- **Propósito**: Catálogo de productos veterinarios
- **Campos**:
  - Nombre
  - Principio Activo
  - Tipo (Antibiótico, Vitamina, Vacuna, etc.)
  - Dosis Recomendada
  - Período de Retiro (días)
  - Estado

### 4️⃣ **Lotes de Aves**
- **Ubicación**: `parametros_lotes.fxml` + `LotesController.java`
- **Propósito**: Gestión de lotes de producción
- **Campos**:
  - Nombre del Lote
  - Número de Aves
  - Edad (semanas)
  - Raza
  - Galpón Asignado
  - Fecha de Ingreso
  - Estado (Activo/Terminado/En preparación)

### 5️⃣ **Ubicaciones y Galpones**
- **Ubicación**: `parametros_ubicaciones.fxml` + `UbicacionesController.java`
- **Propósito**: Configurar ubicaciones físicas de la avícola
- **Campos**:
  - Nombre
  - Tipo (Galpón, Almacén, Bodega, Área de Proceso)
  - Capacidad
  - Responsable
  - Descripción
  - Estado

## 🔧 Funcionalidades Implementadas

### ✅ Búsqueda y Filtrado
Cada módulo incluye:
- **Campo de búsqueda** en tiempo real
- **Filtros por tipo** (según el módulo)
- **Filtros por estado** (Activo/Inactivo)
- **Botón de refrescar** para recargar datos

### ✅ Acciones CRUD
Cada tabla incluye botones de acción:
- **✎ Editar**: Modifica registros existentes
- **🗑 Eliminar**: Elimina registros (con confirmación)
- **👁 Ver**: Ver detalles (solo en Lotes)

### ✅ Diseño Responsivo
- Tablas con anchos optimizados
- Cards con estilo profesional
- Coherente con el tema raíz del sistema
- Colores y tipografía del diseño AVITECH

## 🗄️ Preparación para Base de Datos

### Métodos TODO Marcados

Todos los controladores tienen métodos marcados con `// TODO:` que indican dónde conectar la base de datos:

```java
// TODO: Cargar desde la base de datos
private void loadData() {
    // Actualmente usa datos de ejemplo
    // Reemplazar con consulta SQL
}

// TODO: Insertar en BD y recargar
private void onAgregar() {
    // Abrir diálogo de captura
    // Validar campos
    // INSERT INTO tabla ...
    // Recargar datos
}

// TODO: Actualizar en BD
private void onEditar(Entidad entidad) {
    // Cargar datos en diálogo
    // UPDATE tabla SET ... WHERE id = ?
    // Recargar datos
}

// TODO: Eliminar (lógico) de BD
private void onEliminar(Entidad entidad) {
    // Confirmar acción
    // DELETE o UPDATE estado = 'Inactivo'
    // Recargar datos
}
```

### Estructura de Clases de Datos

Cada controlador tiene una clase interna que representa el modelo:

```java
public static class Unidad {
    public final String nombre, simbolo, tipo, desc, estado;
    // Constructor
}
```

**Para migrar a BD:**
1. Crear entidades JPA correspondientes
2. Crear repositorios/DAOs
3. Reemplazar datos de ejemplo con consultas
4. Implementar validaciones
5. Agregar manejo de errores

## 🔗 Integración con Otros Módulos

Los datos de Parámetros se utilizan en:

- **Suministros**: 
  - Unidades de medida para entradas/salidas
  - Categorías para clasificar productos
  - Ubicaciones para almacenamiento

- **Sanidad**:
  - Medicamentos para aplicaciones
  - Lotes para asignación de tratamientos
  - Unidades para dosificación

- **Producción**:
  - Lotes de aves
  - Ubicaciones (galpones)
  - Categorías de productos

- **Reportes**:
  - Todos los parámetros para filtros y clasificaciones

## 📊 Datos de Ejemplo Incluidos

Cada módulo incluye datos de ejemplo realistas para facilitar el desarrollo y pruebas:

- **7 Unidades** de medida estándar
- **7 Categorías** de productos
- **6 Medicamentos** comunes en avicultura
- **5 Lotes** de ejemplo (activos y terminados)
- **8 Ubicaciones** (galpones, almacenes, áreas)

## 🎨 Estilo Visual

- **Cards blancos** con sombra suave
- **Botones primarios** en verde AVITECH (#176A52)
- **Botones secundarios** con borde verde
- **Tablas** con estilo `movement-table`
- **Iconos** de emojis para acciones
- **Badges** de color para estados

## ⚠️ Notas sobre Errores en ReportesController

Los "errores" que ves en `ReportesController` son **solo ADVERTENCIAS**, no errores críticos. Ocurren porque:

1. El FXML tiene elementos con `fx:id` declarados
2. El controlador tiene campos `@FXML` correspondientes
3. Algunos campos no se usan todavía en el código Java

**Esto es completamente normal** cuando preparas la estructura para futuras funcionalidades de base de datos. Los mismos tipos de advertencias pueden aparecer en otros controladores preparados para BD.

## 🚀 Próximos Pasos

1. **Crear entidades JPA** para cada modelo
2. **Implementar repositorios** con Spring Data JPA o JDBC
3. **Crear diálogos modales** para agregar/editar registros
4. **Agregar validaciones** de campos
5. **Implementar auditoría** de cambios
6. **Agregar paginación** si hay muchos registros
7. **Sincronizar** con otros módulos del sistema

## ✨ Características Listas

✅ Navegación completa entre módulos  
✅ Búsqueda en tiempo real  
✅ Filtrado por múltiples criterios  
✅ Confirmación de eliminaciones  
✅ Diseño profesional y consistente  
✅ Código comentado y documentado  
✅ Preparado para integración con BD  
✅ Gestión de permisos por rol  

---

**Sistema desarrollado para AVITECH - Sistema Integral Avícola**

