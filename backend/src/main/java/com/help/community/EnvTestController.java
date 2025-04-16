package com.help.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/env")
public class EnvTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkEnv() throws SQLException {
        Map<String, Object> response = new HashMap<>();

        // 1. Verifica variables de entorno
        response.put("MYSQL_USER_env", System.getenv("MYSQL_USER"));
        response.put("MYSQL_PASSWORD_env", System.getenv("MYSQL_PASSWORD"));

        // 2. Verifica conexión a BD
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            response.put("database", Map.of(
                    "name", meta.getDatabaseProductName(),
                    "version", meta.getDatabaseProductVersion(),
                    "url", meta.getURL(),
                    "isValid", conn.isValid(2)
            ));
        }

        return ResponseEntity.ok(response);
    }
}
