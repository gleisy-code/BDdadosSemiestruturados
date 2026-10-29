package com.projeto.senso_escolar.model;

public class Escola {
    private Integer id;
    private String codigoInep;
    private String nome;
    private String situacao;
    private String dependencia;
    private String aguaPotavel;
    private Integer idEndereco;

    public Escola() {}

    public Escola(String codigoInep, String nome, String situacao, String dependencia, String aguaPotavel, Integer idEndereco) {
        this.codigoInep = codigoInep;
        this.nome = nome;
        this.situacao = situacao;
        this.dependencia = dependencia;
        this.aguaPotavel = aguaPotavel;
        this.idEndereco = idEndereco;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCodigoInep() { return codigoInep; }
    public void setCodigoInep(String codigoInep) { this.codigoInep = codigoInep; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    public String getDependencia() { return dependencia; }
    public void setDependencia(String dependencia) { this.dependencia = dependencia; }
    public String getAguaPotavel() { return aguaPotavel; }
    public void setAguaPotavel(String aguaPotavel) { this.aguaPotavel = aguaPotavel; }
    public Integer getIdEndereco() { return idEndereco; }
    public void setIdEndereco(Integer idEndereco) { this.idEndereco = idEndereco; }
}