package com.avitech.sia.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class DBTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBTest.class);

    public static void main(String[] args) {
        LOGGER.info("DBTest: iniciando prueba de conexión...");
        try (InputStream in = DBTest.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (in == null) {
                LOGGER.error("No se encontró app.properties en recursos.");
                System.exit(2);
            }
            Properties p = new Properties();
            p.load(in);
            String url = p.getProperty("db.url");
            String user = p.getProperty("db.user");
            String pass = p.getProperty("db.pass");

            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(url);
            cfg.setUsername(user);
            cfg.setPassword(pass);
            cfg.setMaximumPoolSize(Integer.parseInt(p.getProperty("db.pool.max", "5")));

            try (HikariDataSource ds = new HikariDataSource(cfg);
                 Connection c = ds.getConnection()) {
                LOGGER.info("Conexión establecida OK: {} (user={})", url, user);
                String q = "SELECT COUNT(*) AS cnt FROM Usuarios";
                try (PreparedStatement ps = c.prepareStatement(q);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        LOGGER.info("Usuarios en la tabla: {}", rs.getInt("cnt"));
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error en DBTest: {}", ex.getMessage(), ex);
            System.exit(1);
        }
    }
}
