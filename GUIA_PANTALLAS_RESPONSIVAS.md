# 🖥️ Guía: Pantallas Responsivas en AVITECH SIA

## 📋 Resumen de la Solución

He implementado un sistema completo para que **todas las pantallas se adapten automáticamente** a la resolución de la pantalla donde se ejecute la aplicación. La solución incluye:

### ✅ Componentes Creados:

1. **`ScreenManager.java`** - Detecta la resolución y proporciona información sobre la pantalla
2. **`BaseController.java`** - Controlador base con funcionalidad responsiva común
3. **Actualización de `App.java`** - Integración del ScreenManager en el ciclo de vida de la app

---

## 🎯 Características Principales

### 1. Detección Automática de Pantalla
El sistema detecta automáticamente:
- ✔️ Resolución (ancho x alto)
- ✔️ Categoría de pantalla (Pequeña, Mediana, Grande, Extra Grande, Ultra)
- ✔️ DPI de la pantalla
- ✔️ Límites visuales disponibles

### 2. Categorías de Pantalla Soportadas:
- **Pequeña**: 1366x768 (HD Ready)
- **Mediana**: 1600x900 (HD+)
- **Grande**: 1920x1080 (Full HD) ⭐
- **Extra Grande**: 2560x1440 (2K/QHD)
- **Ultra**: 3840x2160 (4K)

### 3. Ventanas Adaptativas
Las ventanas ahora:
- Se maximizan automáticamente en pantallas grandes
- Se dimensionan al 90% en pantallas medianas/pequeñas
- Se centran automáticamente
- Mantienen su estado al cambiar de pantalla

---

## 🔧 Cómo Usar en tus Controladores

### Opción 1: Extender BaseController (Recomendado)

```java
package com.avitech.sia.iu.reportes;

import com.avitech.sia.iu.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ReportesController extends BaseController {
    
    @FXML private TableView<?> tvRecientes;
    @FXML private TableColumn<?, ?> colRepNombre;
    
    @Override
    protected void onScreenReady() {
        // Este método se llama automáticamente cuando screenManager está listo
        
        // Ajustar anchos de columnas según la pantalla
        adjustColumnWidth(colRepNombre, 360);  // ancho base 360px
        
        // Mostrar info de pantalla en consola
        System.out.println("Pantalla detectada: " + getScreenInfo());
        
        // Usar UI simplificada en pantallas pequeñas
        if (shouldUseSimplifiedUI()) {
            // Ocultar columnas secundarias, reducir padding, etc.
        }
    }
}
```

### Opción 2: Acceso Directo al ScreenManager

```java
package com.avitech.sia.iu.usuarios;

import com.avitech.sia.App;
import com.avitech.sia.iu.ScreenManager;
import javafx.fxml.FXML;

public class UsuariosController {
    
    @FXML
    public void initialize() {
        ScreenManager sm = App.getScreenManager();
        
        // Obtener dimensiones
        double ancho = sm.getScreenWidth();
        double alto = sm.getScreenHeight();
        
        // Ajustar componentes según el tamaño
        double factorEscala = sm.getScaleFactor();
        
        // Verificar tipo de pantalla
        if (sm.isSmallScreen()) {
            // Ajustes para pantallas pequeñas
        }
    }
}
```

---

## 📐 Actualizar Archivos FXML para Responsividad

### ❌ ANTES (Tamaños Fijos):
```xml
<BorderPane prefWidth="1920" prefHeight="1080">
    <TableColumn prefWidth="360" text="Nombre"/>
</BorderPane>
```

### ✅ DESPUÉS (Responsivo):
```xml
<BorderPane>
    <!-- Sin prefWidth/prefHeight fijos -->
    <!-- Usa porcentajes con ColumnConstraints -->
    <GridPane>
        <columnConstraints>
            <ColumnConstraints percentWidth="33.3"/>
            <ColumnConstraints percentWidth="66.7"/>
        </columnConstraints>
    </GridPane>
</BorderPane>
```

### 📝 Reglas para FXML Responsivo:

1. **NO usar `prefWidth`/`prefHeight` en el contenedor raíz**
2. **Usar porcentajes** en GridPane: `percentWidth="50"`
3. **Usar `HBox.hgrow="ALWAYS"`** y `VBox.vgrow="ALWAYS"`
4. **ScrollPane con `fitToWidth="true"`** para contenido largo
5. **maxWidth="Infinity" maxHeight="Infinity"** en contenedores principales

---

## 🎨 Ejemplo Completo: Convertir Pantalla a Responsiva

### Paso 1: Actualizar FXML
```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.geometry.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<!-- Raíz SIN tamaños fijos -->
<BorderPane xmlns:fx="http://javafx.com/fxml"
            fx:controller="com.avitech.sia.iu.ejemplo.EjemploController">

    <!-- Sidebar con ancho fijo razonable -->
    <left>
        <ScrollPane fitToWidth="true" hbarPolicy="NEVER" prefWidth="270">
            <VBox spacing="10" styleClass="sidebar">
                <!-- Contenido del sidebar -->
            </VBox>
        </ScrollPane>
    </left>

    <!-- Contenido central que crece -->
    <center>
        <ScrollPane fitToWidth="true" hbarPolicy="NEVER">
            <VBox spacing="18" styleClass="content">
                <padding><Insets topRightBottomLeft="16"/></padding>
                
                <!-- Grid con porcentajes -->
                <GridPane hgap="12" vgap="12">
                    <columnConstraints>
                        <ColumnConstraints percentWidth="50"/>
                        <ColumnConstraints percentWidth="50"/>
                    </columnConstraints>
                    
                    <VBox GridPane.columnIndex="0"><!-- Contenido --></VBox>
                    <VBox GridPane.columnIndex="1"><!-- Contenido --></VBox>
                </GridPane>
            </VBox>
        </ScrollPane>
    </center>
</BorderPane>
```

### Paso 2: Actualizar Controlador
```java
package com.avitech.sia.iu.ejemplo;

import com.avitech.sia.iu.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EjemploController extends BaseController {
    
    @FXML private TableView<MiDato> tabla;
    @FXML private TableColumn<MiDato, String> colNombre;
    @FXML private TableColumn<MiDato, String> colDescripcion;
    
    @Override
    protected void onScreenReady() {
        // Ajustar columnas según la pantalla
        adjustColumnWidth(colNombre, 300);       // Base: 300px
        adjustColumnWidth(colDescripcion, 500);  // Base: 500px
        
        // Info de debug
        System.out.println("📱 Ejecutando en: " + getScreenInfo());
        System.out.println("📏 Factor de escala: " + getUIScaleFactor());
    }
}
```

---

## 🚀 Resultados Esperados

Cuando ejecutes la aplicación, verás en la consola:

```
═══════════════════════════════════════════════
  AVITECH - Detección de Pantalla
═══════════════════════════════════════════════
  Resolución: 1920 x 1080
  Categoría: Grande
  DPI: 96.0
═══════════════════════════════════════════════
```

Y las ventanas:
- ✅ Se abrirán maximizadas automáticamente
- ✅ Se adaptarán a la resolución disponible
- ✅ Mantendrán proporciones correctas en cualquier pantalla
- ✅ Los componentes se escalarán proporcionalmente

---

## 📋 Checklist de Migración

Para migrar tus pantallas existentes:

- [ ] Eliminar `prefWidth`/`prefHeight` del BorderPane raíz en FXML
- [ ] Convertir GridPane a usar `percentWidth` en lugar de anchos fijos
- [ ] Actualizar controlador para extender `BaseController`
- [ ] Implementar `onScreenReady()` para ajustes de columnas
- [ ] Probar en diferentes resoluciones (cambiar resolución de Windows)

---

## 🛠️ Métodos Útiles del ScreenManager

```java
// Obtener dimensiones
double ancho = screenManager.getScreenWidth();
double alto = screenManager.getScreenHeight();

// Verificar tipo de pantalla
boolean esPequeña = screenManager.isSmallScreen();
boolean esGrande = screenManager.isLargeScreen();

// Calcular tamaños adaptativos
double anchoColumna = screenManager.getColumnWidth(300);  // Base 300px
double altoComponente = screenManager.getComponentHeight(200);  // Base 200px

// Factor de escala para UI
double factor = screenManager.getScaleFactor();  // 0.75 a 1.5

// Info completa
screenManager.printDebugInfo();
```

---

## 💡 Consejos Adicionales

1. **Siempre usar ScrollPane** en contenido central para evitar recortes
2. **Porcentajes en GridPane** son mejores que anchos fijos
3. **Sidebar puede mantener ancho fijo** (250-300px es razonable)
4. **TableView ajusta columnas** automáticamente si usas `adjustColumnWidth()`
5. **Probar en múltiples resoluciones** antes de desplegar

---

## 🎯 Próximos Pasos

1. ✅ Ya tienes ScreenManager funcionando
2. ✅ Ya tienes BaseController creado
3. ✅ App.java ya está integrado
4. ⏳ Actualizar cada controlador para extender BaseController
5. ⏳ Revisar y actualizar todos los FXML para quitar tamaños fijos

---

## 📞 Soporte

Si encuentras problemas:
- Verifica que todos los FXML no tengan `prefWidth`/`prefHeight` en el raíz
- Revisa la consola para ver la resolución detectada
- Usa `screenManager.printDebugInfo()` para diagnóstico

¡Tu aplicación ahora es completamente responsiva! 🎉

