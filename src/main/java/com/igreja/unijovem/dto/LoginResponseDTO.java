package com.igreja.unijovem.dto;

import com.igreja.unijovem.entity.UsuarioLogin;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Resposta do login contendo dados do usuário autenticado")
public class LoginResponseDTO {

    @Schema(description = "ID do usuário login")
    private Long id;

    @Schema(description = "Email do usuário")
    private String email;

    @Schema(description = "Tipo de usuário (ADMIN ou USUARIO)")
    private String tipoUsuario;

    @Schema(description = "Token de autenticação")
    private String token;

    @Schema(description = "Data do último login")
    private LocalDateTime ultimoLogin;

    @Schema(description = "Se o usuário está ativo")
    private Boolean ativo;

    @Schema(description = "Data de criação")
    private LocalDateTime dataCriacao;

    // Construtores
    public LoginResponseDTO() {}

    // Construtor principal
    public LoginResponseDTO(UsuarioLogin usuarioLogin, String token) {
        this.id = usuarioLogin.getId();
        this.email = usuarioLogin.getEmail();
        this.tipoUsuario = usuarioLogin.getTipoUsuario().name();
        this.token = token;
        this.ultimoLogin = usuarioLogin.getUltimoLogin();
        this.ativo = usuarioLogin.getAtivo();
        this.dataCriacao = usuarioLogin.getDataCriacao();
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

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}