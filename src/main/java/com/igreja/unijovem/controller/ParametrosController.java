package com.igreja.unijovem.controller;

import com.igreja.unijovem.entity.Parametros;
import com.igreja.unijovem.service.ParametrosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/parametros")
@CrossOrigin(origins = "*")
public class ParametrosController {

    @Autowired
    private ParametrosService parametrosService;  //tt

    @GetMapping
    public ResponseEntity<Parametros> buscarParametrosAtuais() {
        Optional<Parametros> parametros = parametrosService.buscarParametrosAtuais();

        if (parametros.isPresent()) {
            return ResponseEntity.ok(parametros.get());
        }

        // Se não existem parâmetros, criar os iniciais
        Parametros parametrosIniciais = parametrosService.criarParametrosIniciais();
        return ResponseEntity.ok(parametrosIniciais);
    }

    @PostMapping
    public ResponseEntity<Parametros> salvarParametros(@Valid @RequestBody Parametros parametros) {
        try {
            Parametros parametrosSalvos = parametrosService.salvarParametros(parametros);
            return ResponseEntity.status(HttpStatus.CREATED).body(parametrosSalvos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<Parametros> atualizarParametros(@Valid @RequestBody Parametros parametros) {
        try {
            Parametros parametrosAtualizados = parametrosService.salvarParametros(parametros);
            return ResponseEntity.ok(parametrosAtualizados);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/existem")
    public ResponseEntity<Boolean> verificarExistenciaParametros() {
        boolean existem = parametrosService.existemParametros();
        return ResponseEntity.ok(existem);
    }
}