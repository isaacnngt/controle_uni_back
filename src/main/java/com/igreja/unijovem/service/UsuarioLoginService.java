package com.igreja.unijovem.service;

import com.igreja.unijovem.dto.CadastroUsuarioLoginDTO;
import com.igreja.unijovem.dto.LoginDTO;
import com.igreja.unijovem.dto.LoginResponseDTO;
import com.igreja.unijovem.entity.UsuarioLogin;
import com.igreja.unijovem.repository.UsuarioLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioLoginService {

    @Autowired
    private UsuarioLoginRepository usuarioLoginRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ===== AUTENTICAÇÃO =====
    public LoginResponseDTO autenticar(LoginDTO loginDTO) {
        // Buscar usuário por email
        Optional<UsuarioLogin> usuarioLoginOpt = usuarioLoginRepository.findByEmailAndAtivoTrue(loginDTO.getEmail());

        if (!usuarioLoginOpt.isPresent()) {
            throw new RuntimeException("Email não encontrado ou usuário inativo");
        }

        UsuarioLogin usuarioLogin = usuarioLoginOpt.get();

        // Verificar se está bloqueado
        if (usuarioLogin.isBloqueado()) {
            throw new RuntimeException("Usuário temporariamente bloqueado devido a muitas tentativas de login");
        }

        // Verificar senha
        if (!passwordEncoder.matches(loginDTO.getSenha(), usuarioLogin.getSenha())) {
            // Incrementar tentativas e possivelmente bloquear
            usuarioLogin.incrementarTentativas();
            usuarioLoginRepository.save(usuarioLogin);

            throw new RuntimeException("Senha incorreta");
        }

        // Login bem-sucedido
        usuarioLogin.resetarTentativas();
        usuarioLogin.setUltimoLogin(LocalDateTime.now());
        usuarioLoginRepository.save(usuarioLogin);

        // Gerar token
        String token = gerarTokenSimples(usuarioLogin);

        return new LoginResponseDTO(usuarioLogin, token);
    }

    // ===== CADASTRO =====
    public UsuarioLogin cadastrarUsuarioLogin(CadastroUsuarioLoginDTO cadastroDTO) {
        // Verificar se email já existe
        if (usuarioLoginRepository.existsByEmail(cadastroDTO.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        try {
            // Criar usuário de login
            UsuarioLogin usuarioLogin = new UsuarioLogin();
            usuarioLogin.setEmail(cadastroDTO.getEmail());
            usuarioLogin.setSenha(passwordEncoder.encode(cadastroDTO.getSenha()));
            usuarioLogin.setTipoUsuario(UsuarioLogin.TipoUsuario.valueOf(cadastroDTO.getTipoUsuario().toUpperCase()));
            usuarioLogin.setAtivo(true);
            usuarioLogin.setTentativasLogin(0);

            return usuarioLoginRepository.save(usuarioLogin);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    // ===== CRUD USUÁRIO LOGIN =====

    // CREATE - Criar usuário login
    public UsuarioLogin criarUsuarioLogin(String email, String senha, UsuarioLogin.TipoUsuario tipo) {
        // Verificar se email já existe
        if (usuarioLoginRepository.existsByEmail(email)) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Criar login
        UsuarioLogin usuarioLogin = new UsuarioLogin();
        usuarioLogin.setEmail(email);
        usuarioLogin.setSenha(passwordEncoder.encode(senha));
        usuarioLogin.setTipoUsuario(tipo);
        usuarioLogin.setAtivo(true);
        usuarioLogin.setTentativasLogin(0);

        return usuarioLoginRepository.save(usuarioLogin);
    }

    // READ - Buscar por ID
    public Optional<UsuarioLogin> buscarPorId(Long id) {
        return usuarioLoginRepository.findById(id);
    }

    // READ - Buscar por email
    public Optional<UsuarioLogin> buscarPorEmail(String email) {
        return usuarioLoginRepository.findByEmail(email);
    }

    // READ - Listar todos
    public List<UsuarioLogin> listarTodos() {
        return usuarioLoginRepository.findAll();
    }

    // READ - Listar ativos
    public List<UsuarioLogin> listarUsuariosAtivos() {
        return usuarioLoginRepository.findAllAtivos();
    }

    // READ - Listar administradores
    public List<UsuarioLogin> listarAdministradores() {
        return usuarioLoginRepository.findAdministradores();
    }

    // UPDATE - Alterar senha
    public void alterarSenha(Long usuarioLoginId, String senhaAtual, String novaSenha) {
        Optional<UsuarioLogin> usuarioLoginOpt = usuarioLoginRepository.findById(usuarioLoginId);

        if (!usuarioLoginOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        UsuarioLogin usuarioLogin = usuarioLoginOpt.get();

        // Verificar senha atual
        if (!passwordEncoder.matches(senhaAtual, usuarioLogin.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        // Atualizar senha
        usuarioLogin.setSenha(passwordEncoder.encode(novaSenha));
        usuarioLoginRepository.save(usuarioLogin);
    }

    // UPDATE - Resetar senha (sem verificar atual)
    public void resetarSenha(String email, String novaSenha) {
        Optional<UsuarioLogin> usuarioLoginOpt = usuarioLoginRepository.findByEmail(email);

        if (!usuarioLoginOpt.isPresent()) {
            throw new RuntimeException("Email não encontrado");
        }

        UsuarioLogin usuarioLogin = usuarioLoginOpt.get();
        usuarioLogin.setSenha(passwordEncoder.encode(novaSenha));
        usuarioLogin.resetarTentativas();
        usuarioLoginRepository.save(usuarioLogin);
    }

    // UPDATE - Ativar/Desativar usuário
    public void ativarDesativarUsuario(Long usuarioLoginId, boolean ativo) {
        Optional<UsuarioLogin> usuarioLoginOpt = usuarioLoginRepository.findById(usuarioLoginId);

        if (!usuarioLoginOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        UsuarioLogin usuarioLogin = usuarioLoginOpt.get();
        usuarioLogin.setAtivo(ativo);
        usuarioLoginRepository.save(usuarioLogin);
    }

    // UPDATE - Alterar tipo de usuário
    public void alterarTipoUsuario(Long usuarioLoginId, UsuarioLogin.TipoUsuario novoTipo) {
        Optional<UsuarioLogin> usuarioLoginOpt = usuarioLoginRepository.findById(usuarioLoginId);

        if (!usuarioLoginOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        UsuarioLogin usuarioLogin = usuarioLoginOpt.get();
        usuarioLogin.setTipoUsuario(novoTipo);
        usuarioLoginRepository.save(usuarioLogin);
    }

    // UPDATE - Alterar email
    public void alterarEmail(Long usuarioLoginId, String novoEmail) {
        // Verificar se o novo email já existe
        if (usuarioLoginRepository.existsByEmail(novoEmail)) {
            throw new RuntimeException("Email já está em uso");
        }

        Optional<UsuarioLogin> usuarioLoginOpt = usuarioLoginRepository.findById(usuarioLoginId);

        if (!usuarioLoginOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        UsuarioLogin usuarioLogin = usuarioLoginOpt.get();
        usuarioLogin.setEmail(novoEmail);
        usuarioLoginRepository.save(usuarioLogin);
    }

    // DELETE - Excluir usuário login
    public void excluirUsuarioLogin(Long usuarioLoginId) {
        if (!usuarioLoginRepository.existsById(usuarioLoginId)) {
            throw new RuntimeException("Usuário não encontrado");
        }

        usuarioLoginRepository.deleteById(usuarioLoginId);
    }

    // ===== MÉTODOS AUXILIARES =====

    @Modifying
    public void limparBloqueiosExpirados() {
        usuarioLoginRepository.limparBloqueiosExpirados(LocalDateTime.now());
    }

    public boolean existeEmail(String email) {
        return usuarioLoginRepository.existsByEmail(email);
    }

    public List<UsuarioLogin> buscarLoginsRecentes(int dias) {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(dias);
        return usuarioLoginRepository.findLoginsRecentes(dataLimite);
    }

    public List<Object[]> contarUsuariosPorTipo() {
        return usuarioLoginRepository.countByTipoUsuario();
    }

    public List<UsuarioLogin> buscarUsuariosBloqueados() {
        return usuarioLoginRepository.findBloqueados(LocalDateTime.now());
    }

    // ===== TOKEN (SIMPLES) =====

    private String gerarTokenSimples(UsuarioLogin usuarioLogin) {
        return "TOKEN_" + usuarioLogin.getId() + "_" + System.currentTimeMillis();
    }

    public boolean validarToken(String token) {
        return token != null && token.startsWith("TOKEN_");
    }

    public Optional<UsuarioLogin> buscarPorToken(String token) {
        if (token == null || !token.startsWith("TOKEN_")) {
            return Optional.empty();
        }

        try {
            String[] parts = token.split("_");
            Long usuarioId = Long.parseLong(parts[1]);
            return usuarioLoginRepository.findById(usuarioId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}