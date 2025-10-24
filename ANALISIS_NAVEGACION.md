# ANÁLISIS DE ESTRUCTURA Y NAVEGACIÓN DEL PROYECTO AVITECH SIA

## 📋 RESUMEN EJECUTIVO

Este documento analiza la estructura completa del proyecto AVITECH SIA, desde el login hasta todos los módulos y sus interconexiones, identificando inconsistencias y errores en las rutas de navegación.

---

## 🔑 1. PUNTO DE ENTRADA: LOGIN

**Archivo**: `LoginController.java`  
**FXML**: `/fxml/login.fxml`

### Credenciales Demo:
- **Admin**: `admin / admin123` → Redirige a `/fxml/dashboard_admin.fxml`
- **Supervisor**: `supervisor / super123` → Redirige a `/fxml/dashboard_supervisor.fxml`
- **Operador**: `operador / oper123` → Redirige a `/fxml/dashboard_oper.fxml`

### ✅ Estado: CORRECTO
- Utiliza `SessionManager` para gestionar roles
- Redirige correctamente según el rol del usuario
- Las rutas de dashboard están correctamente configuradas

---

## 🏠 2. DASHBOARDS POR ROL

### 2.1 Dashboard Admin
**Controlador**: `DashboardAdminController.java`  
**FXML**: `/fxml/dashboard_admin.fxml`  
**Rol**: ADMIN (acceso completo a todos los módulos)

### 2.2 Dashboard Supervisor
**Controlador**: `DashboardSupervisorController.java`  
**FXML**: `/fxml/dashboard_supervisor.fxml`  
**Rol**: SUPERVISOR (acceso limitado según permisos)

### 2.3 Dashboard Operador
**Controlador**: `DashboardOperadorController.java`  
**FXML**: `/fxml/dashboard_oper.fxml`  
**Rol**: OPERADOR (acceso básico)

### ✅ Estado: CORRECTO
- Todos los dashboards extienden de `DashboardController`
- Implementan control de permisos mediante `BaseController`

---

## 📦 3. MÓDULOS PRINCIPALES Y SUS RUTAS

### 3.1 SUMINISTROS
**Controlador**: `SuministrosController.java`  
**FXML Principal**: `/fxml/suministros/suministros.fxml`

#### ⚠️ INCONSISTENCIA DETECTADA:
- **En AlertasController**: `App.goTo("/fxml/suministros.fxml", ...)`  
  ❌ **RUTA INCORRECTA** - Debería ser `/fxml/suministros/suministros.fxml`
  
- **Rutas correctas en otros controladores**:
  - SanidadController: `/fxml/suministros/suministros.fxml` ✅
  - ProduccionController: `/fxml/suministros/suministros.fxml` ✅

#### Subpantallas (Modales):
- `/fxml/suministros/entrada.fxml` → `EntradaController`
- `/fxml/suministros/salida.fxml` → `SalidaController`
- `/fxml/suministros/stock.fxml` → `StockController`
- `/fxml/suministros/moverstock.fxml` → `MoverStockController`

**Métodos de navegación en SuministrosController**:
```java
@FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
@FXML private void goSupplies()   { /* ya estás aquí */ }
@FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
@FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
@FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
@FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
@FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
@FXML private void goParams()     { /* pendiente */ }  // ⚠️ NO IMPLEMENTADO
@FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
@FXML private void goBackup()     { /* pendiente */ }  // ⚠️ NO IMPLEMENTADO
@FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión"); }
```

---

### 3.2 SANIDAD
**Controlador**: `SanidadController.java`  
**FXML Principal**: `/fxml/sanidad/sanidad.fxml`

#### ⚠️ INCONSISTENCIA DETECTADA:
- **En AlertasController**: `App.goTo("/fxml/sanidad/sanidad.fxml", ...)` ✅ CORRECTO

#### Subpantallas (Modales):
- `/fxml/sanidad/sanidad_aplicacion.fxml` → `AplicacionController`
- `/fxml/sanidad/sanidad_evento.fxml` → `EventoController`

**Métodos de navegación en SanidadController**:
```java
@FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
@FXML private void goSupplies()   { App.goTo("/fxml/suministros/suministros.fxml", "SIA Avitech — Suministros"); } ✅
@FXML private void goHealth()     { /* ya aquí */ }
@FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
@FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
@FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
@FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
@FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
@FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
@FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos"); }
@FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — LOGIN"); }
```

---

### 3.3 PRODUCCIÓN
**Controlador**: `ProduccionController.java`  
**FXML Principal**: `/fxml/produccion/produccion.fxml`

**Métodos de navegación en ProduccionController**:
```java
@FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
@FXML private void goSupplies()   { App.goTo("/fxml/suministros/suministros.fxml", "SIA Avitech — Suministros"); } ✅
@FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
@FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
@FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
@FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); }
@FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
@FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
@FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
@FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos"); }
@FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión"); }
```

---

### 3.4 ALERTAS
**Controlador**: `AlertasController.java`  
**FXML Principal**: `/fxml/alertas/alertas.fxml`

#### ❌ INCONSISTENCIA CRÍTICA:
**AlertasController tiene la ruta INCORRECTA para alertas en sus archivos FXML:**
- La estructura real es: `/fxml/alertas/alertas.fxml`
- Pero el controlador referencia: `/fxml/alertas.fxml` (sin carpeta)

**Métodos de navegación en AlertasController**:
```java
@FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
@FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); } ❌ INCORRECTO
@FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
@FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
@FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
@FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); } ❌ POSIBLE ERROR
@FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
@FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
@FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
@FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos"); }
@FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión");}
```

---

### 3.5 REPORTES
**Controlador**: `ReportesController.java`  
**FXML Principal**: `/fxml/reportes.fxml` ✅

**Métodos de navegación en ReportesController**:
```java
@FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
@FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); } ❌ INCORRECTO
@FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
@FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
@FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
@FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); } ❌ POSIBLE ERROR
@FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); }
@FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
@FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
@FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos"); }
@FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión"); }
```

---

### 3.6 AUDITORÍA
**Controlador**: `AuditoriaController.java`  
**FXML Principal**: `/fxml/auditoria/auditoria.fxml`

#### ❌ INCONSISTENCIA DETECTADA:
- Estructura real: `/fxml/auditoria/auditoria.fxml`
- Referencias en controladores: `/fxml/auditoria.fxml` (sin carpeta)

---

### 3.7 PARÁMETROS
**Controlador**: `ParametrosController.java`  
**FXML Principal**: `/fxml/parametros.fxml` ✅

#### Subpantallas dinámicas (dentro de StackPane):
- `/fxml/Parametros/parametros_unidades.fxml` → `UnidadesController`
- `/fxml/Parametros/parametros_categorias.fxml` → `CategoriasController`
- `/fxml/Parametros/parametros_medicamentos.fxml` → `MedicamentosController`
- `/fxml/Parametros/parametros_lotes.fxml` → `LotesController`
- `/fxml/Parametros/parametros_ubicaciones.fxml` → `UbicacionesController`

**Métodos de navegación en ParametrosController**:
```java
@FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
@FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); } ❌ INCORRECTO
@FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
@FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
@FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
@FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); } ❌ POSIBLE ERROR
@FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); } ❌ POSIBLE ERROR
@FXML private void goParams()     { /* ya estás aquí */ }
@FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
@FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos"); }
@FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión"); }
```

---

### 3.8 USUARIOS
**Controlador**: `UsuariosController.java`  
**FXML Principal**: `/fxml/usuarios/usuarios.fxml` ✅

#### Subpantallas (Modales):
- `/fxml/usuarios/usuario_nuevo.fxml` → `NuevoUsuarioController`

**Métodos de navegación en UsuariosController**:
```java
@FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
@FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); } ❌ INCORRECTO
@FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
@FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
@FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
@FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); } ❌ POSIBLE ERROR
@FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); } ❌ POSIBLE ERROR
@FXML private void goParams()     { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
@FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
@FXML private void goBackup()     { App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos"); }
@FXML private void onExit()       { App.goTo("/fxml/login.fxml", "SIA Avitech — Inicio de sesión"); }
```

---

### 3.9 RESPALDOS
**Controlador**: `RespaldosController.java`  
**FXML Principal**: `/fxml/respaldos/respaldos.fxml` ✅

**Métodos de navegación en RespaldosController**:
```java
@FXML private void goDashboard()  { App.goTo("/fxml/dashboard_admin.fxml", "SIA Avitech — ADMIN"); }
@FXML private void goSupplies()   { App.goTo("/fxml/suministros.fxml", "SIA Avitech — Suministros"); } ❌ INCORRECTO
@FXML private void goHealth()     { App.goTo("/fxml/sanidad/sanidad.fxml", "SIA Avitech — Sanidad"); }
@FXML private void goProduction() { App.goTo("/fxml/produccion/produccion.fxml", "SIA Avitech — Producción"); }
@FXML private void goReports()    { App.goTo("/fxml/reportes.fxml", "SIA Avitech — Reportes"); }
@FXML private void goAlerts()     { App.goTo("/fxml/alertas.fxml", "SIA Avitech — Alertas"); } ❌ POSIBLE ERROR
@FXML private void goAudit()      { App.goTo("/fxml/auditoria.fxml", "SIA Avitech — Auditoría"); } ❌ POSIBLE ERROR
@FXML private void goParams()     { App.goTo("/fxml/parametros_unidades.fxml", "SIA Avitech — Parámetros"); } ❌ INCORRECTO
@FXML private void goUsers()      { App.goTo("/fxml/usuarios/usuarios.fxml", "SIA Avitech — Usuarios"); }
```

---

## 🚨 PROBLEMAS DETECTADOS - RESUMEN

### 1. ❌ RUTA INCORRECTA DE SUMINISTROS
**Afecta a los siguientes controladores:**
- `AlertasController.java` (línea ~148)
- `ReportesController.java` (línea ~52)
- `ParametrosController.java` (línea ~60)
- `UsuariosController.java` (línea ~239)
- `RespaldosController.java` (línea ~97)

**Ruta incorrecta**: `/fxml/suministros.fxml`  
**Ruta correcta**: `/fxml/suministros/suministros.fxml`

### 2. ⚠️ POSIBLE ERROR EN RUTA DE ALERTAS
**Estructura de archivos real:**
```
fxml/
  alertas/
    alertas.fxml
```

**Referencias en controladores**: `/fxml/alertas.fxml`  
**Verificar**: La mayoría de controladores referencian alertas sin carpeta, pero la estructura sugiere que debería estar en una carpeta.

### 3. ⚠️ POSIBLE ERROR EN RUTA DE AUDITORÍA
**Estructura de archivos real:**
```
fxml/
  auditoria/
    auditoria.fxml
```

**Referencias en controladores**: `/fxml/auditoria.fxml`  
**Verificar**: Similar al caso de alertas.

### 4. ❌ RUTA INCORRECTA EN RESPALDOS → PARÁMETROS
**En RespaldosController** (línea ~102):
```java
@FXML private void goParams() { App.goTo("/fxml/parametros_unidades.fxml", ...) }
```
**Debería ser**: `/fxml/parametros.fxml`

### 5. ⚠️ MÉTODOS NO IMPLEMENTADOS
**En SuministrosController:**
- `goParams()` → comentado como "/* pendiente */"
- `goBackup()` → comentado como "/* pendiente */"

---

## 🔧 SOLUCIONES RECOMENDADAS

### Acción 1: Corregir ruta de Suministros en 5 controladores
Cambiar en:
- AlertasController
- ReportesController  
- ParametrosController
- UsuariosController
- RespaldosController

**De**: `App.goTo("/fxml/suministros.fxml", ...)`  
**A**: `App.goTo("/fxml/suministros/suministros.fxml", ...)`

### Acción 2: Verificar estructura real de Alertas y Auditoría
Confirmar si los archivos FXML están en:
- `/fxml/alertas.fxml` o `/fxml/alertas/alertas.fxml`
- `/fxml/auditoria.fxml` o `/fxml/auditoria/auditoria.fxml`

### Acción 3: Corregir goParams() en RespaldosController
**De**: `App.goTo("/fxml/parametros_unidades.fxml", ...)`  
**A**: `App.goTo("/fxml/parametros.fxml", ...)`

### Acción 4: Implementar métodos pendientes en SuministrosController
```java
@FXML private void goParams() { App.goTo("/fxml/parametros.fxml", "SIA Avitech — Parámetros"); }
@FXML private void goBackup() { App.goTo("/fxml/respaldos/respaldos.fxml", "SIA Avitech — Respaldos"); }
```

---

## 📊 MATRIZ DE NAVEGACIÓN

| DESDE ↓ / HACIA → | Dashboard | Suministros | Sanidad | Producción | Reportes | Alertas | Auditoría | Parámetros | Usuarios | Respaldos |
|-------------------|-----------|-------------|---------|------------|----------|---------|-----------|------------|----------|-----------|
| **Suministros**   | ✅ | - | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ | ✅ | ⚠️ |
| **Sanidad**       | ✅ | ✅ | - | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Producción**    | ✅ | ✅ | ✅ | - | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Alertas**       | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Reportes**      | ✅ | ❌ | ✅ | ✅ | - | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Auditoría**     | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ | - | ✅ | ✅ | ✅ |
| **Parámetros**    | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | - | ✅ | ✅ |
| **Usuarios**      | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | - | ✅ |
| **Respaldos**     | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ | - |

**Leyenda:**
- ✅ = Ruta correcta implementada
- ❌ = Ruta incorrecta o error confirmado
- ⚠️ = No implementado o pendiente
- `-` = No aplica (mismo módulo)

---

## 🎯 PRIORIDAD DE CORRECCIONES

### 🔴 CRÍTICO (Bloquea navegación)
1. Corregir ruta de Suministros en 5 controladores
2. Corregir ruta de Parámetros en RespaldosController

### 🟡 IMPORTANTE (Puede causar problemas)
3. Verificar y corregir rutas de Alertas y Auditoría
4. Implementar métodos pendientes en SuministrosController

### 🟢 MEJORAS (Consistencia)
5. Estandarizar formato de comentarios en métodos de navegación
6. Documentar convención de rutas en un archivo README

---

## ✅ CONCLUSIONES

1. **El sistema de navegación está funcional** pero tiene **5 controladores con la ruta incorrecta** para Suministros.

2. **La estructura del proyecto es consistente** con el patrón:
   - Login → Dashboard (por rol) → Módulos principales → Subpantallas/Modales

3. **El control de permisos funciona correctamente** mediante:
   - `BaseController` + `SessionManager`
   - Módulos definidos en `UserRole.Module`
   - Verificación automática en `initialize()`

4. **Se requieren correcciones inmediatas** en las rutas para evitar errores de navegación.

---

**Fecha de análisis**: 2025-10-24  
**Versión del proyecto**: Actual  
**Analizado por**: GitHub Copilot

