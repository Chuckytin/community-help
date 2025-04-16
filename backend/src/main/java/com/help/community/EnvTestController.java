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
    public Map<String, Object> checkEnv() throws SQLException {
        Map<String, Object> response = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            response.put("database", Map.of(
                    "url", conn.getMetaData().getURL(),
                    "user", conn.getMetaData().getUserName(),
                    "isValid", conn.isValid(2),
                    "version", conn.getMetaData().getDatabaseProductVersion()
            ));
        }
        response.put("MYSQL_USER_env", System.getenv("MYSQL_USER"));
        response.put("MYSQL_PASSWORD_env", System.getenv("MYSQL_PASSWORD"));
        return response;
    }
}
