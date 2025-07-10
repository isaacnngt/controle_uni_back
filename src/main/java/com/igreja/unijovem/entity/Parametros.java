package com.igreja.unijovem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parametros_tb")
public class Parametros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Valor da mensalidade é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor da mensalidade deve ser positivo")
    @Column(name = "vl_mensalidade", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMensalidade;

    @NotNull(message = "Valor da camisa é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor da camisa deve ser positivo")
    @Column(name = "vl_camisa", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCamisa;

    @Column(name = "dt_inclusao", nullable = false)
    private LocalDateTime dataInclusao;

    @Column(name = "dt_alteracao")
    private LocalDateTime dataAlteracao;

    @PrePersist
    public void prePersist() {
        dataInclusao = LocalDateTime.now();
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

    public BigDecimal getValorMensalidade() {
        return valorMensalidade;
    }

    public void setValorMensalidade(BigDecimal valorMensalidade) {
        this.valorMensalidade = valorMensalidade;
    }

    public BigDecimal getValorCamisa() {
        return valorCamisa;
    }

    public void setValorCamisa(BigDecimal valorCamisa) {
        this.valorCamisa = valorCamisa;
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