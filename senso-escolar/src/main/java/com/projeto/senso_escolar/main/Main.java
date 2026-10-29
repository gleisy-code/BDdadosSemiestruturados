package com.projeto.senso_escolar.main;


import com.projeto.senso_escolar.dao.CensoDAO;
import com.projeto.senso_escolar.service.ImportadorService;

public class Main {
    public static void main(String[] args) {
        String caminhoCSV = "Tabela_Escola_2025_V2.csv"; 

        ImportadorService service = new ImportadorService();
        CensoDAO dao = new CensoDAO();

        try {
            // 1. Processa e trata o CSV populando o SQLite
            service.processarArquivo(caminhoCSV);

            // 2. Consulta de Exemplo da Aplicação (Diagnóstico de Infraestrutura)
            dao.buscarEscolasSemAgua();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}