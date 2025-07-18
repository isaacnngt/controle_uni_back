package com.igreja.unijovem.controller;

import com.igreja.unijovem.dto.CadastroUsuarioLoginDTO;
import com.igreja.unijovem.dto.LoginDTO;
import com.igreja.unijovem.dto.LoginResponseDTO;
import com.igreja.unijovem.entity.UsuarioLogin;
import com.igreja.unijovem.service.UsuarioLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e gerenciamento completo de usuários login")
public class AuthController {

    @Autowired
    private UsuarioLoginService usuarioLoginService;

    // ===== AUTENTICAÇÃO =====

    @Operation(summary = "Realizar login", description = "Autentica um usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Parameter(description = "Dados de login do usuário", required = true)
            @Valid @RequestBody LoginDTO loginDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            LoginResponseDTO loginResponse = usuarioLoginService.autenticar(loginDTO);

            response.put("sucesso", true);
            response.put("mensagem", "Login realizado com sucesso!");
            response.put("usuario", loginResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Operation(summary = "Realizar logout", description = "Realiza logout do usuário")
    @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();

        response.put("sucesso", true);
        response.put("mensagem", "Logout realizado com sucesso!");

        return ResponseEntity.ok(response);
    }

    // ===== CADASTRO =====

    @Operation(summary = "Cadastrar usuário login", description = "Cria um novo usuário login no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já existe")
    })
    @PostMapping("/cadastrar")
    public ResponseEntity<Map<String, Object>> cadastrarUsuarioLogin(
            @Parameter(description = "Dados do usuário login", required = true)
            @Valid @RequestBody CadastroUsuarioLoginDTO cadastroDTO) {

        Map<String, Object> response = new HashMap<>();

        try {
            UsuarioLogin usuarioLogin = usuarioLoginService.cadastrarUsuarioLogin(cadastroDTO);

            response.put("sucesso", true);
            response.put("mensagem", "Usuário cadastrado com sucesso!");
            response.put("usuarioLoginId", usuarioLogin.getId());
            response.put("email", usuarioLogin.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    // ===== CRUD USUÁRIO LOGIN =====

    @Operation(summary = "Criar usuário login", description = "Cria um novo usuário login via parâmetros")
    @PostMapping("/criar-login")
    public ResponseEntity<Map<String, Object>> criarLogin(
            @Parameter(description = "Email do usuário", required = true)
            @RequestParam String email,
            @Parameter(description = "Senha do usuário", required = true)
            @RequestParam String senha,
            @Parameter(description = "Tipo de usuário (USUARIO ou ADMIN)")
            @RequestParam(defaultValue = "USUARIO") String tipoUsuario) {

        Map<String, Object> response = new HashMap<>();

        try {
            UsuarioLogin.TipoUsuario tipo = UsuarioLogin.TipoUsuario.valueOf(tipoUsuario.toUpperCase());
            UsuarioLogin usuarioLogin = usuarioLoginService.criarUsuarioLogin(email, senha, tipo);

            response.put("sucesso", true);
            response.put("mensagem", "Login criado com sucesso!");
            response.put("usuarioLoginId", usuarioLogin.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Buscar usuário login por ID", description = "Retorna dados de um usuário login específico")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(
            @Parameter(description = "ID do usuário login", required = true)
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<UsuarioLogin> usuarioLogin = usuarioLoginService.buscarPorId(id);

            if (usuarioLogin.isPresent()) {
                response.put("sucesso", true);
                response.put("usuario", usuarioLogin.get());
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Usuário não encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Listar todos os usuários login", description = "Lista todos os usuários login do sistema")
    @GetMapping("/todos")
    public ResponseEntity<Map<String, Object>> listarTodos() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<UsuarioLogin> usuarios = usuarioLoginService.listarTodos();

            response.put("sucesso", true);
            response.put("usuarios", usuarios);
            response.put("total", usuarios.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Listar usuários ativos", description = "Lista todos os usuários ativos no sistema")
    @GetMapping("/usuarios-ativos")
    public ResponseEntity<Map<String, Object>> listarUsuariosAtivos() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<UsuarioLogin> usuarios = usuarioLoginService.listarUsuariosAtivos();

            response.put("sucesso", true);
            response.put("usuarios", usuarios);
            response.put("total", usuarios.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Alterar senha", description = "Altera a senha de um usuário")
    @PutMapping("/alterar-senha")
    public ResponseEntity<Map<String, Object>> alterarSenha(
            @Parameter(description = "ID do usuário de login", required = true)
            @RequestParam Long usuarioLoginId,
            @Parameter(description = "Senha atual", required = true)
            @RequestParam String senhaAtual,
            @Parameter(description = "Nova senha", required = true)
            @RequestParam String novaSenha) {

        Map<String, Object> response = new HashMap<>();

        try {
            usuarioLoginService.alterarSenha(usuarioLoginId, senhaAtual, novaSenha);

            response.put("sucesso", true);
            response.put("mensagem", "Senha alterada com sucesso!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Resetar senha", description = "Reseta a senha de um usuário pelo email")
    @PutMapping("/resetar-senha")
    public ResponseEntity<Map<String, Object>> resetarSenha(
            @Parameter(description = "Email do usuário", required = true)
            @RequestParam String email,
            @Parameter(description = "Nova senha", required = true)
            @RequestParam String novaSenha) {

        Map<String, Object> response = new HashMap<>();

        try {
            usuarioLoginService.resetarSenha(email, novaSenha);

            response.put("sucesso", true);
            response.put("mensagem", "Senha resetada com sucesso!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Ativar/Desativar usuário", description = "Ativa ou desativa um usuário no sistema")
    @PutMapping("/ativar-desativar/{usuarioLoginId}")
    public ResponseEntity<Map<String, Object>> ativarDesativarUsuario(
            @Parameter(description = "ID do usuário de login", required = true)
            @PathVariable Long usuarioLoginId,
            @Parameter(description = "Status ativo (true) ou inativo (false)", required = true)
            @RequestParam boolean ativo) {

        Map<String, Object> response = new HashMap<>();

        try {
            usuarioLoginService.ativarDesativarUsuario(usuarioLoginId, ativo);

            response.put("sucesso", true);
            response.put("mensagem", ativo ? "Usuário ativado!" : "Usuário desativado!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Alterar tipo de usuário", description = "Altera o tipo de um usuário (ADMIN/USUARIO)")
    @PutMapping("/alterar-tipo/{usuarioLoginId}")
    public ResponseEntity<Map<String, Object>> alterarTipoUsuario(
            @Parameter(description = "ID do usuário de login", required = true)
            @PathVariable Long usuarioLoginId,
            @Parameter(description = "Novo tipo (ADMIN ou USUARIO)", required = true)
            @RequestParam String tipoUsuario) {

        Map<String, Object> response = new HashMap<>();

        try {
            UsuarioLogin.TipoUsuario tipo = UsuarioLogin.TipoUsuario.valueOf(tipoUsuario.toUpperCase());
            usuarioLoginService.alterarTipoUsuario(usuarioLoginId, tipo);

            response.put("sucesso", true);
            response.put("mensagem", "Tipo de usuário alterado para " + tipo);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Alterar email", description = "Altera o email de um usuário")
    @PutMapping("/alterar-email/{usuarioLoginId}")
    public ResponseEntity<Map<String, Object>> alterarEmail(
            @Parameter(description = "ID do usuário de login", required = true)
            @PathVariable Long usuarioLoginId,
            @Parameter(description = "Novo email", required = true)
            @RequestParam String novoEmail) {

        Map<String, Object> response = new HashMap<>();

        try {
            usuarioLoginService.alterarEmail(usuarioLoginId, novoEmail);

            response.put("sucesso", true);
            response.put("mensagem", "Email alterado com sucesso!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Excluir usuário login", description = "Remove um usuário login do sistema")
    @DeleteMapping("/{usuarioLoginId}")
    public ResponseEntity<Map<String, Object>> excluirUsuarioLogin(
            @Parameter(description = "ID do usuário de login", required = true)
            @PathVariable Long usuarioLoginId) {

        Map<String, Object> response = new HashMap<>();

        try {
            usuarioLoginService.excluirUsuarioLogin(usuarioLoginId);

            response.put("sucesso", true);
            response.put("mensagem", "Usuário login excluído com sucesso!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    // ===== ENDPOINTS AUXILIARES =====

    @Operation(summary = "Listar administradores", description = "Lista todos os usuários administradores")
    @GetMapping("/administradores")
    public ResponseEntity<Map<String, Object>> listarAdministradores() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<UsuarioLogin> admins = usuarioLoginService.listarAdministradores();

            response.put("sucesso", true);
            response.put("administradores", admins);
            response.put("total", admins.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Listar usuários bloqueados", description = "Lista usuários que estão bloqueados")
    @GetMapping("/bloqueados")
    public ResponseEntity<Map<String, Object>> listarUsuariosBloqueados() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<UsuarioLogin> bloqueados = usuarioLoginService.buscarUsuariosBloqueados();

            response.put("sucesso", true);
            response.put("bloqueados", bloqueados);
            response.put("total", bloqueados.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Limpar bloqueios", description = "Remove bloqueios expirados do sistema")
    @PostMapping("/limpar-bloqueios")
    public ResponseEntity<Map<String, Object>> limparBloqueios() {
        Map<String, Object> response = new HashMap<>();

        try {
            usuarioLoginService.limparBloqueiosExpirados();

            response.put("sucesso", true);
            response.put("mensagem", "Bloqueios expirados limpos com sucesso!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Verificar email", description = "Verifica se um email já existe no sistema")
    @GetMapping("/verificar-email/{email}")
    public ResponseEntity<Map<String, Object>> verificarEmail(
            @Parameter(description = "Email a ser verificado", required = true)
            @PathVariable String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean existe = usuarioLoginService.existeEmail(email);

            response.put("sucesso", true);
            response.put("existe", existe);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Buscar logins recentes", description = "Lista logins realizados nos últimos X dias")
    @GetMapping("/logins-recentes")
    public ResponseEntity<Map<String, Object>> buscarLoginsRecentes(
            @Parameter(description = "Número de dias para buscar", required = false)
            @RequestParam(defaultValue = "7") int dias) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<UsuarioLogin> logins = usuarioLoginService.buscarLoginsRecentes(dias);

            response.put("sucesso", true);
            response.put("logins", logins);
            response.put("total", logins.size());
            response.put("periodo", dias + " dias");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Estatísticas de usuários", description = "Retorna estatísticas dos tipos de usuários")
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Object[]> estatisticas = usuarioLoginService.contarUsuariosPorTipo();

            response.put("sucesso", true);
            response.put("estatisticas", estatisticas);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}