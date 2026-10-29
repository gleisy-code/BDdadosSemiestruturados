package com.projeto.senso_escolar.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Esta classe é responsável por conectar ao SQLite e criar o arquivo censo_escolar.db caso ele ainda não exista.

public class Conexao {
    private static final String URL = "jdbc:sqlite:censo_escolar.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}