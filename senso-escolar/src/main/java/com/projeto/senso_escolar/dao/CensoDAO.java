package com.projeto.senso_escolar.dao;


import com.projeto.senso_escolar.config.Conexao;
import com.projeto.senso_escolar.model.Endereco;
import com.projeto.senso_escolar.model.Escola;

import java.sql.*;

public class CensoDAO {

    public void criarTabelas() throws SQLException {
        String sqlEndereco = """
            CREATE TABLE IF NOT EXISTS endereco (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                logradouro TEXT,
                numero TEXT,
                bairro TEXT,
                municipio TEXT,
                uf TEXT,
                cep TEXT
            );
        """;

        String sqlEscola = """
            CREATE TABLE IF NOT EXISTS escola (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo_inep TEXT UNIQUE,
                nome TEXT NOT NULL,
                situacao TEXT,
                dependencia TEXT,
                agua_potavel TEXT,
                id_endereco INTEGER,
                FOREIGN KEY (id_endereco) REFERENCES endereco(id)
            );
        """;

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlEndereco);
            stmt.execute(sqlEscola);
        }
    }

    public int salvarEndereco(Endereco end) throws SQLException {
        String sql = "INSERT INTO endereco (logradouro, numero, bairro, municipio, uf, cep) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, end.getLogradouro());
            pstmt.setString(2, end.getNumero());
            pstmt.setString(3, end.getBairro());
            pstmt.setString(4, end.getMunicipio());
            pstmt.setString(5, end.getUf());
            pstmt.setString(6, end.getCep());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void salvarEscola(Escola escola) throws SQLException {
    	String sql = "INSERT OR REPLACE INTO escola (codigo_inep, nome, situacao, dependencia, agua_potavel, id_endereco) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, escola.getCodigoInep());
            pstmt.setString(2, escola.getNome());
            pstmt.setString(3, escola.getSituacao());
            pstmt.setString(4, escola.getDependencia());
            pstmt.setString(5, escola.getAguaPotavel());
            pstmt.setInt(6, escola.getIdEndereco());
            pstmt.executeUpdate();
        }
    }

    public void buscarEscolasSemAgua() throws SQLException {
        String sql = """
            SELECT e.nome, e.situacao, end.municipio, end.uf 
            FROM escola e 
            JOIN endereco end ON e.id_endereco = end.id 
            WHERE e.agua_potavel = 'Não Possui'
            LIMIT 10;
        """;

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Amostra: Escolas sem Água Potável ---");
            while (rs.next()) {
                System.out.printf("Escola: %s | Município: %s-%s | Situação: %s\n",
                        rs.getString("nome"),
                        rs.getString("municipio"),
                        rs.getString("uf"),
                        rs.getString("situacao"));
            }
        }
    }
}