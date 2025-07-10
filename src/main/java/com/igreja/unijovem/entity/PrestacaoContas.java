package com.igreja.unijovem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prestacao_contas_tb")
public class PrestacaoContas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @Column(name = "vl_camisa_total", precision = 10, scale = 2)
    private BigDecimal valorCamisaTotal;

    @Column(name = "vl_camisa_pg", precision = 10, scale = 2)
    private BigDecimal valorCamisaPago;

    @Column(name = "perc_camisa_pago", precision = 5, scale = 2)
    private BigDecimal percentualCamisaPago;

    @Column(name = "vl_mens_total", precision = 10, scale = 2)
    private BigDecimal valorMensalidadeTotal;

    @Column(name = "vl_mens_pg", precision = 10, scale = 2)
    private BigDecimal valorMensalidadePago;

    @Column(name = "perc_mens_pago", precision = 5, scale = 2)
    private BigDecimal percentualMensalidadePago;

    @Column(name = "forma_pagamento", length = 50)
    private String formaPagamento;

    @NotNull(message = "Ano vigente é obrigatório")
    @Column(name = "ano_vigente", nullable = false)
    private Integer anoVigente;

    @Column(name = "dt_inclusao", nullable = false)
    private LocalDateTime dataInclusao;

    @Column(name = "dt_alteracao")
    private LocalDateTime dataAlteracao;

    @PrePersist
    public void prePersist() {
        dataInclusao = LocalDateTime.now();
        calcularPercentuais();
    }

    @PreUpdate
    public void preUpdate() {
        dataAlteracao = LocalDateTime.now();
        calcularPercentuais();
    }

    private void calcularPercentuais() {
        if (valorCamisaTotal != null && valorCamisaTotal.compareTo(BigDecimal.ZERO) > 0) {
            if (valorCamisaPago != null) {
                percentualCamisaPago = valorCamisaPago.multiply(BigDecimal.valueOf(100))
                        .divide(valorCamisaTotal, 2, BigDecimal.ROUND_HALF_UP);
            }
        }

        if (valorMensalidadeTotal != null && valorMensalidadeTotal.compareTo(BigDecimal.ZERO) > 0) {
            if (valorMensalidadePago != null) {
                percentualMensalidadePago = valorMensalidadePago.multiply(BigDecimal.valueOf(100))
                        .divide(valorMensalidadeTotal, 2, BigDecimal.ROUND_HALF_UP);
            }
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public BigDecimal getValorCamisaTotal() {
        return valorCamisaTotal;
    }

    public void setValorCamisaTotal(BigDecimal valorCamisaTotal) {
        this.valorCamisaTotal = valorCamisaTotal;
    }

    public BigDecimal getValorCamisaPago() {
        return valorCamisaPago;
    }

    public void setValorCamisaPago(BigDecimal valorCamisaPago) {
        this.valorCamisaPago = valorCamisaPago;
    }

    public BigDecimal getPercentualCamisaPago() {
        return percentualCamisaPago;
    }

    public void setPercentualCamisaPago(BigDecimal percentualCamisaPago) {
        this.percentualCamisaPago = percentualCamisaPago;
    }

    public BigDecimal getValorMensalidadeTotal() {
        return valorMensalidadeTotal;
    }

    public void setValorMensalidadeTotal(BigDecimal valorMensalidadeTotal) {
        this.valorMensalidadeTotal = valorMensalidadeTotal;
    }

    public BigDecimal getValorMensalidadePago() {
        return valorMensalidadePago;
    }

    public void setValorMensalidadePago(BigDecimal valorMensalidadePago) {
        this.valorMensalidadePago = valorMensalidadePago;
    }

    public BigDecimal getPercentualMensalidadePago() {
        return percentualMensalidadePago;
    }

    public void setPercentualMensalidadePago(BigDecimal percentualMensalidadePago) {
        this.percentualMensalidadePago = percentualMensalidadePago;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public Integer getAnoVigente() {
        return anoVigente;
    }

    public void setAnoVigente(Integer anoVigente) {
        this.anoVigente = anoVigente;
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