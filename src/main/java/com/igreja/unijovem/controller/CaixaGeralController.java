package com.igreja.unijovem.controller;

import com.igreja.unijovem.entity.Saldo;
import com.igreja.unijovem.service.CaixaGeralService;
import com.igreja.unijovem.service.SaldoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/caixa-geral")
@CrossOrigin(origins = "*")
public class CaixaGeralController {

    @Autowired
    private CaixaGeralService caixaGeralService;

    @Autowired
    private SaldoService saldoService;

    @GetMapping("/relatorio/{anoVigente}")
    public ResponseEntity<Map<String, Object>> buscarRelatorioCaixa(@PathVariable Integer anoVigente) {
        try {
            Map<String, Object> relatorio = caixaGeralService.buscarRelatorioCaixaGeral(anoVigente);
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/saldo-atual")
    public ResponseEntity<Saldo> buscarSaldoAtual() {
        Optional<Saldo> saldo = saldoService.buscarSaldoAtual();

        if (saldo.isPresent()) {
            return ResponseEntity.ok(saldo.get());
        }

        // Se não existe, criar saldo inicial
        Saldo saldoInicial = saldoService.criarSaldoInicial();
        return ResponseEntity.ok(saldoInicial);
    }

    @PutMapping("/atualizar-saldo")
    public ResponseEntity<Saldo> atualizarSaldo(@RequestParam BigDecimal novoSaldo) {
        try {
            if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().build();
            }

            Saldo saldoAtualizado = saldoService.atualizarSaldo(novoSaldo);
            return ResponseEntity.ok(saldoAtualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}