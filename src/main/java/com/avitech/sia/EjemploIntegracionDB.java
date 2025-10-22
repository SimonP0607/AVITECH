
package com.avitech.sia;

import com.avitech.sia.db.DB;
import com.avitech.sia.db.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * ESTE ARCHIVO ES UN EJEMPLO COMPLETO PARA ILUSTRAR LA INTEGRACIÓN
 * ENTRE LA INTERFAZ DE USUARIO (FXML), EL CONTROLADOR (JAVA) Y LA BASE DE DATOS (DAO).
 *
 * Muestra cómo el clic de un botón "Guardar" puede tomar datos de campos de texto,
 * procesarlos y enviarlos a la base de datos.
 *
 * Puedes borrar este archivo una vez que hayas implementado la lógica en tus clases reales.
 */
public class EjemploIntegracionDB {

    // =================================================================================
    // 1. SIMULACIÓN DE LA INTERFAZ DE USUARIO (CAMPOS DE ENTRADA)
    // =================================================================================
    // Estos campos serían definidos en tu archivo FXML (ej: `modal_nuevo_usuario.fxml`)
    // y enlazados aquí con la anotación @FXML.

    @FXML
    private TextField campoUsuario; // Correspondería a un <TextField fx:id="campoUsuario" />

    @FXML
    private PasswordField campoPassword; // Correspondería a un <PasswordField fx:id="campoPassword" />

    @FXML
    private ComboBox<String> comboRol; // Correspondería a un <ComboBox fx:id="comboRol" />

    @FXML
    private Button botonGuardar; // Correspondería a un <Button fx:id="botonGuardar" onAction="#handleGuardarUsuario" />


    /**
     * Este método sería el `onAction` del botón "Guardar".
     * Orquesta todo el proceso.
     */
    @FXML
    private void handleGuardarUsuario() {
        // --- PASO A: LEER DATOS DE LA INTERFAZ ---
        String nombreUsuario = campoUsuario.getText();
        String passwordPlano = campoPassword.getText();
        String rol = comboRol.getValue();

        // Validaciones básicas (en una app real, serían más robustas)
        if (nombreUsuario.isEmpty() || passwordPlano.isEmpty() || rol == null) {
            System.out.println("Error: Todos los campos son obligatorios.");
            // Aquí mostrarías una alerta al usuario.
            return;
        }

        try {
            // --- PASO B: LLAMAR A LA LÓGICA DE NEGOCIO/DATOS ---
            // Se delega la tarea de crear el usuario a la clase DAO correspondiente.
            UsuarioDAO_Ejemplo.crearUsuario(nombreUsuario, passwordPlano, rol);

            System.out.println("¡Usuario '" + nombreUsuario + "' guardado exitosamente!");
            // Aquí cerrarías la ventana modal y refrescarías la tabla de usuarios.

        } catch (Exception e) {
            System.err.println("Error al guardar el usuario: " + e.getMessage());
            // Aquí mostrarías una alerta de error al usuario.
            e.printStackTrace();
        }
    }
}


// =================================================================================
// 2. SIMULACIÓN DE LA CLASE DAO (DATA ACCESS OBJECT)
// =================================================================================
// Esta clase se encarga EXCLUSIVAMENTE de la comunicación con la base de datos.
// El siguiente método `crearUsuario` es el que le falta a tu `UsuarioDAO` actual.

class UsuarioDAO_Ejemplo {

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param usuario El nombre de usuario.
     * @param passwordPlano La contraseña en texto plano (sin hashear).
     * @param rol El rol del usuario (ADMIN, SUPERVISOR, OPERADOR).
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public static void crearUsuario(String usuario, String passwordPlano, String rol) throws Exception {

        // --- PASO C: LÓGICA DE PREPARACIÓN DE DATOS ---
        // Se hashea la contraseña antes de guardarla. NUNCA guardes contraseñas en texto plano.
        // Esta lógica usa tu clase `PasswordUtil`.
        String passwordHasheado = PasswordUtil.hash(passwordPlano);

        // --- PASO D: SCRIPT SQL ---
        // La consulta SQL para insertar un nuevo registro en la tabla `Usuarios`.
        // Se usan `?` como placeholders para prevenir inyección SQL.
        String sql = "INSERT INTO Usuarios (usuario, password, rol) VALUES (?, ?, ?)";

        // --- PASO E: EJECUCIÓN DE LA CONSULTA ---
        // Se obtiene una conexión a la BD usando tu clase `DB`.
        try (Connection conexion = DB.get();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            // Se asignan los valores a los placeholders `?` en orden.
            ps.setString(1, usuario);
        //    ps.setString(2, passwordHasheado); // Descomentar cuando se integre
            ps.setString(3, rol);

            // Se ejecuta la sentencia. `executeUpdate` se usa para INSERT, UPDATE, DELETE.
            ps.executeUpdate();
        }
        // La conexión se cierra automáticamente gracias al `try-with-resources`.
    }
}

/*
NOTAS FINALES:

1.  `PasswordUtil.java`: Tu clase para hashear contraseñas es correcta. El método `PasswordUtil.hash()`
    es el que se debe usar antes de pasar la contraseña al DAO, como se muestra en el ejemplo.

2.  `DB.java`: Tu clase `DB` para gestionar la conexión es el camino correcto. El `try-with-resources`
    asegura que la conexión se cierre siempre, evitando fugas de recursos.

3.  Modal: En `UsuariosController`, el método `onNuevoUsuario` debería abrir una nueva ventana (modal)
    que contenga el formulario de creación. El controlador de esa nueva ventana sería similar a la
    clase `EjemploIntegracionDB` que he creado aquí.

*/
