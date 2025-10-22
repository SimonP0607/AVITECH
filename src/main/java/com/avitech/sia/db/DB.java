package com.avitech.sia.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public final class DB {
    private static final Logger logger = LoggerFactory.getLogger(DB.class);
    private static final HikariDataSource DS;

    static {
        try {
            Properties p = new Properties();
            try (InputStream in = DB.class.getResourceAsStream("/app.properties")) {
                if (in == null) throw new IllegalStateException("No se encontró app.properties");
                p.load(in);
            }
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(p.getProperty("db.url"));
            cfg.setUsername(p.getProperty("db.user"));
            cfg.setPassword(p.getProperty("db.pass"));
            cfg.setMaximumPoolSize(Integer.parseInt(p.getProperty("db.pool.max", "10")));
            DS = new HikariDataSource(cfg);
            logger.info("Pool de conexiones a BD inicializado correctamente");
        } catch (Exception e) {
            logger.error("Error inicializando el pool de BD", e);
            throw new RuntimeException("Error inicializando el pool de BD", e);
        }
    }

    private DB() {}
    public static Connection get() throws Exception {
        return DS.getConnection();
    }
}
