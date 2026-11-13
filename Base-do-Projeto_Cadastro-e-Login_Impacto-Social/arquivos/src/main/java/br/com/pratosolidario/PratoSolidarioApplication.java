package br.com.pratosolidario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação Prato Solidário.
 * Ao iniciar, o Spring conecta ao PostgreSQL, executa database/01_schema.sql e
 * database/02_seed.sql e publica a API REST em http://localhost:8080/api
 * (a interface de demonstração fica em http://localhost:8080).
 */
@SpringBootApplication
public class PratoSolidarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(PratoSolidarioApplication.class, args);
    }
}
