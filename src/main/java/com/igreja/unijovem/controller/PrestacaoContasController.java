package com.igreja.unijovem.controller;

import com.igreja.unijovem.entity.PrestacaoContas;
import com.igreja.unijovem.service.PrestacaoContasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/prestacao-contas")
@CrossOrigin(origins = "*")
public class PrestacaoContasController {

    @Autowired
    private PrestacaoContasService prestacaoContasService;

    @GetMapping
    public ResponseEntity<List<PrestacaoContas>> listarTodas() {
        List<PrestacaoContas> prestacoes = prestacaoContasService.listarTodas();
        return ResponseEntity.ok(prestacoes);
    }

    @GetMapping("/ano/{anoVigente}")
    public ResponseEntity<List<PrestacaoContas>> listarPorAno(@PathVariable Integer anoVigente) {
        List<PrestacaoContas> prestacoes = prestacaoContasService.listarPorAno(anoVigente);
        return ResponseEntity.ok(prestacoes);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PrestacaoContas>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<PrestacaoContas> prestacoes = prestacaoContasService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(prestacoes);
    }

    @GetMapping("/usuario/{usuarioId}/ano/{anoVigente}")
    public ResponseEntity<PrestacaoContas> buscarPorUsuarioEAno(@PathVariable Long usuarioId, @PathVariable Integer anoVigente) {
        Optional<PrestacaoContas> prestacao = prestacaoContasService.buscarPorUsuarioEAno(usuarioId, anoVigente);

        if (prestacao.isPresent()) {
            return ResponseEntity.ok(prestacao.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/anos")
    public ResponseEntity<List<Integer>> listarAnosVigentes() {
        List<Integer> anos = prestacaoContasService.listarAnosVigentes();
        return ResponseEntity.ok(anos);
    }

    @GetMapping("/com-dividas")
    public ResponseEntity<List<PrestacaoContas>> listarComDividas() {
        List<PrestacaoContas> prestacoes = prestacaoContasService.listarComDividas();
        return ResponseEntity.ok(prestacoes);
    }

    @GetMapping("/com-dividas/ano/{anoVigente}")
    public ResponseEntity<List<PrestacaoContas>> listarComDividasPorAno(@PathVariable Integer anoVigente) {
        List<PrestacaoContas> prestacoes = prestacaoContasService.listarComDividasPorAno(anoVigente);
        return ResponseEntity.ok(prestacoes);
    }

    @PostMapping
    public ResponseEntity<PrestacaoContas> criar(@Valid @RequestBody PrestacaoContas prestacaoContas) {
        try {
            PrestacaoContas prestacaoSalva = prestacaoContasService.salvar(prestacaoContas);
            return ResponseEntity.status(HttpStatus.CREATED).body(prestacaoSalva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/usuario/{usuarioId}/ano/{anoVigente}")
    public ResponseEntity<PrestacaoContas> criarPorUsuarioEAno(@PathVariable Long usuarioId, @PathVariable Integer anoVigente) {
        try {
            PrestacaoContas prestacao = prestacaoContasService.criarPrestacaoContas(usuarioId, anoVigente);
            return ResponseEntity.status(HttpStatus.CREATED).body(prestacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/pagamentos")
    public ResponseEntity<PrestacaoContas> atualizarPagamentos(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal valorMensalidadePago,
            @RequestParam(required = false) BigDecimal valorCamisaPago,
            @RequestParam(required = false) String formaPagamento) {

        try {
            PrestacaoContas prestacaoAtualizada = prestacaoContasService.atualizarPagamentos(
                    id, valorMensalidadePago, valorCamisaPago, formaPagamento);
            return ResponseEntity.ok(prestacaoAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            prestacaoContasService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}