package com.igreja.unijovem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_tb")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "sexo", length = 1)
    private String sexo;

    @Column(name = "tamanho_camisa", length = 5)
    private String tamanhoCamisa;

    @Column(name = "responsavel_direto", length = 200)
    private String responsavelDireto;

    @Column(name = "dt_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "cargo_min")
    private Boolean cargoMinisterial;

    @Column(name = "cargo_unijovem", length = 60)
    private String cargoUnijovem;

    @Column(name = "dividas")
    private Boolean dividas;

    @Column(name = "dt_inclusao", nullable = false)
    private LocalDateTime dataInclusao;

    @Column(name = "dt_alteracao")
    private LocalDateTime dataAlteracao;

    @PrePersist
    public void prePersist() {
        dataInclusao = LocalDateTime.now();
        if (cargoMinisterial == null) {
            cargoMinisterial = false;
        }
        if (dividas == null) {
            dividas = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        dataAlteracao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTamanhoCamisa() {
        return tamanhoCamisa;
    }

    public void setTamanhoCamisa(String tamanhoCamisa) {
        this.tamanhoCamisa = tamanhoCamisa;
    }

    public String getResponsavelDireto() {
        return responsavelDireto;
    }

    public void setResponsavelDireto(String responsavelDireto) {
        this.responsavelDireto = responsavelDireto;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Boolean getCargoMinisterial() {
        return cargoMinisterial;
    }

    public void setCargoMinisterial(Boolean cargoMinisterial) {
        this.cargoMinisterial = cargoMinisterial;
    }

    public String getCargoUnijovem() {
        return cargoUnijovem;
    }

    public void setCargoUnijovem(String cargoUnijovem) {
        this.cargoUnijovem = cargoUnijovem;
    }

    public Boolean getDividas() {
        return dividas;
    }

    public void setDividas(Boolean dividas) {
        this.dividas = dividas;
    }

    public LocalDateTime getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(LocalDateTime dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }
}