package com.projeto.senso_escolar.service;

import com.projeto.senso_escolar.dao.CensoDAO;
import com.projeto.senso_escolar.model.Endereco;
import com.projeto.senso_escolar.model.Escola;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class ImportadorService {

    private final CensoDAO dao = new CensoDAO();

    public void processarArquivo(String caminhoCSV) {
        try {
            dao.criarTabelas();

            // 1. Ajustado para ISO_8859_1 para corrigir os acentos
            Reader reader = new FileReader(caminhoCSV, StandardCharsets.ISO_8859_1);
            CSVFormat config = CSVFormat.DEFAULT.builder()
                    .setDelimiter(';')
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreSurroundingSpaces(true)
                    .build();

            CSVParser parser = new CSVParser(reader, config);

            int contador = 0;
            System.out.println("Iniciando importação e tratamento de dados...");

            //aqui acontece todo o tratamento
            for (CSVRecord record : parser) {
                
                // --- FILTRO POR REGIONAL / ESTADO ---
                // Pegamos a sigla da UF. Se não for do estado desejado (ex: "PB" ou "RO"), ignora a linha.
                String uf = getValor(record, "SG_UF").trim();
                if (!uf.equalsIgnoreCase("PB")) { // Altere "PB" para o estado que preferir
                    continue;
                }

                // --- TRATAMENTO 1: Dados Não-Conformes / CEP ---
                String rawCep = getValor(record, "CO_CEP");
                String cepLimpo = rawCep.replaceAll("\\D", "");
                if (cepLimpo.length() < 8) {
                    cepLimpo = String.format("%08d", cepLimpo.isEmpty() ? 0 : Integer.parseInt(cepLimpo));
                }

                // --- TRATAMENTO 2: Padronização de Número de Endereço ---
                String numero = getValor(record, "NU_ENDERECO").trim();
                if (numero.isEmpty() || numero.equalsIgnoreCase("SN") || numero.equalsIgnoreCase("S/N")) {
                    numero = "S/N";
                }

                // --- TRATAMENTO 3: Sinônimos / Normalização da Situação de Funcionamento ---
                String rawSituacao = getValor(record, "TP_SITUACAO_FUNCIONAMENTO");
                String situacaoPadronizada = switch (rawSituacao) {
                    case "1", "ATIVA", "EM ATIVIDADE" -> "Em Atividade";
                    case "2", "PARALISADA" -> "Paralisada";
                    case "3", "EXTINTA" -> "Extinta";
                    default -> "Não Informado";
                };

                // --- TRATAMENTO 4: Normalização de Infraestrutura (Água Potável) ---
                String rawAgua = getValor(record, "IN_AGUA_POTAVEL");
                String aguaPadronizada = switch (rawAgua) {
                    case "1", "SIM", "TRUE" -> "Possui";
                    case "0", "NAO", "NÃO", "FALSE" -> "Não Possui";
                    default -> "Sem Informação";
                };

                // Instancia e salva o Endereço
                Endereco end = new Endereco(
                        getValor(record, "DS_ENDERECO"),
                        numero,
                        getValor(record, "NO_BAIRRO"),
                        getValor(record, "NO_MUNICIPIO"),
                        getValor(record, "SG_UF"),
                        cepLimpo
                );

                int idEndereco = dao.salvarEndereco(end);

                // Instancia e salva a Escola vinculada
                Escola escola = new Escola(
                        getValor(record, "CO_ENTIDADE"),
                        getValor(record, "NO_ENTIDADE"),
                        situacaoPadronizada,
                        getValor(record, "TP_DEPENDENCIA"),
                        aguaPadronizada,
                        idEndereco
                );

                dao.salvarEscola(escola);

                contador++;
                if (contador % 1000 == 0) {
                    System.out.println(contador + " registros processados...");
                }
            }

            parser.close();
            System.out.println("Importação concluída! Total de escolas processadas: " + contador);

        } catch (Exception e) {
            System.err.println("Erro durante o processamento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getValor(CSVRecord record, String coluna) {
        return record.isMapped(coluna) && record.get(coluna) != null ? record.get(coluna) : "";
    }
}