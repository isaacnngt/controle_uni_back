package com.igreja.unijovem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_login_tb")
public class UsuarioLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @JsonIgnore
    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private TipoUsuario tipoUsuario = TipoUsuario.USUARIO;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "tentativas_login")
    private Integer tentativasLogin = 0;

    @Column(name = "bloqueado_ate")
    private LocalDateTime bloqueadoAte;

    @Column(name = "dt_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "dt_alteracao")
    private LocalDateTime dataAlteracao;

    // Enum para tipos de usuário
    public enum TipoUsuario {
        ADMIN, USUARIO
    }

    @PrePersist
    public void prePersist() {
        dataCriacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        dataAlteracao = LocalDateTime.now();
    }

    // Métodos auxiliares
    public boolean isBloqueado() {
        return bloqueadoAte != null && bloqueadoAte.isAfter(LocalDateTime.now());
    }

    public void resetarTentativas() {
        this.tentativasLogin = 0;
        this.bloqueadoAte = null;
    }

    public void incrementarTentativas() {
        this.tentativasLogin++;

        // Bloquear após 5 tentativas por 15 minutos
        if (this.tentativasLogin >= 5) {
            this.bloqueadoAte = LocalDateTime.now().plusMinutes(15);
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public Integer getTentativasLogin() {
        return tentativasLogin;
    }

    public void setTentativasLogin(Integer tentativasLogin) {
        this.tentativasLogin = tentativasLogin;
    }

    public LocalDateTime getBloqueadoAte() {
        return bloqueadoAte;
    }

    public void setBloqueadoAte(LocalDateTime bloqueadoAte) {
        this.bloqueadoAte = bloqueadoAte;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }
}