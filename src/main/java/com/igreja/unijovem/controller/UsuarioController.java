package com.igreja.unijovem.controller;

import com.igreja.unijovem.entity.Usuario;
import com.igreja.unijovem.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);

        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscarPorNome(@RequestParam String nome) {
        List<Usuario> usuarios = usuarioService.buscarPorNome(nome);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/sexo/{sexo}")
    public ResponseEntity<List<Usuario>> buscarPorSexo(@PathVariable String sexo) {
        List<Usuario> usuarios = usuarioService.buscarPorSexo(sexo);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/cargo-ministerial")
    public ResponseEntity<List<Usuario>> buscarComCargoMinisterial() {
        List<Usuario> usuarios = usuarioService.buscarComCargoMinisterial();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/com-dividas")
    public ResponseEntity<List<Usuario>> buscarComDividas() {
        List<Usuario> usuarios = usuarioService.buscarComDividas();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/cargo-unijovem")
    public ResponseEntity<List<Usuario>> buscarComCargoUnijovem() {
        List<Usuario> usuarios = usuarioService.buscarComCargoUnijovem();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario usuarioSalvo = usuarioService.salvar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        try {
            Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/existe/{id}")
    public ResponseEntity<Boolean> verificarExistencia(@PathVariable Long id) {
        boolean existe = usuarioService.existePorId(id);
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contarUsuarios() {
        long total = usuarioService.contarTodos();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/aniversariantes")
    public ResponseEntity<Map<String, List<Usuario>>> listarAniversariantesPorMes() {
        Map<String, List<Usuario>> aniversariantes = usuarioService.listarAniversariantesPorMes();
        return ResponseEntity.ok(aniversariantes);
    }

    @GetMapping("/aniversariantes/mes/{mes}")
    public ResponseEntity<List<Usuario>> listarAniversariantesDoMes(@PathVariable Integer mes) {
        List<Usuario> aniversariantes = usuarioService.listarAniversariantesDoMes(mes);
        return ResponseEntity.ok(aniversariantes);
    }

    @GetMapping("/aniversariantes/hoje")
    public ResponseEntity<List<Usuario>> listarAniversariantesHoje() {
        List<Usuario> aniversariantes = usuarioService.listarAniversariantesHoje();
        return ResponseEntity.ok(aniversariantes);
    }

    @GetMapping("/aniversariantes/proximos/{dias}")
    public ResponseEntity<List<Usuario>> listarProximosAniversariantes(@PathVariable Integer dias) {
        List<Usuario> aniversariantes = usuarioService.listarProximosAniversariantes(dias);
        return ResponseEntity.ok(aniversariantes);
    }
}