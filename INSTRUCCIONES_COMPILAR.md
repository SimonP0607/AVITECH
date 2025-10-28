# 🔧 INSTRUCCIONES PARA COMPILAR EL PROYECTO

## ⚠️ IMPORTANTE: Descargar Dependencias

Los errores que ves son porque las librerías de **iText7** (PDF) y **Apache POI** (Excel) aún no se han descargado.

## 📥 Pasos para Descargar las Dependencias

### Opción 1: Desde la terminal de IntelliJ IDEA

1. Abre la terminal en IntelliJ (Alt+F12)
2. Ejecuta:
```cmd
gradlew.bat build
```

### Opción 2: Desde el Explorador de Windows

1. Abre el explorador de archivos
2. Navega a: `C:\Users\SIMON PEREZ\Cursos\JAVA\AVITECH`
3. Haz doble clic en `gradlew.bat`
4. Espera a que termine (puede tardar unos minutos la primera vez)

### Opción 3: Desde IntelliJ IDEA con Gradle

1. Abre el panel de Gradle (ícono del elefante en el lado derecho)
2. Haz clic en el botón de "Refresh" (ícono de recarga)
3. O ejecuta: AVITECH > Tasks > build > build

## ✅ Después de la Compilación

Una vez que las dependencias se descarguen:
- ✅ Los errores de "Cannot resolve symbol" desaparecerán
- ✅ El IDE reconocerá las clases de iText y Apache POI
- ✅ Podrás generar reportes en PDF y Excel

## 📦 Dependencias que se Descargarán

```groovy
// PDF (iText7)
implementation 'com.itextpdf:itext7-core:7.2.5'

// Excel (Apache POI)
implementation 'org.apache.poi:poi:5.2.5'
implementation 'org.apache.poi:poi-ooxml:5.2.5'
```

## 🎯 Tamaño Aproximado

- iText7: ~5 MB
- Apache POI: ~15 MB
- Total: ~20 MB de descarga

## ⏱️ Tiempo Estimado

- Primera vez: 2-5 minutos (descarga de internet)
- Compilaciones posteriores: 10-30 segundos

## 🔍 Verificar que Todo Está Correcto

Después de compilar, verifica que:
1. La carpeta `build/` tiene contenido
2. No hay errores rojos en el IDE
3. Los archivos `.java` en `reportes/` no tienen errores

## 🚀 Listo para Usar

Una vez compilado, el sistema de reportes estará **100% funcional** y podrás:
- Generar reportes en PDF profesionales
- Generar reportes en Excel editables
- Aplicar filtros y fechas
- Ver historial de reportes generados

---

**Nota**: Si tienes problemas con el comando, asegúrate de tener conexión a internet, ya que Gradle descargará las dependencias desde Maven Central.

