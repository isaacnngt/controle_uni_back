package com.igreja.unijovem.service;

import com.igreja.unijovem.entity.Parametros;
import com.igreja.unijovem.entity.PrestacaoContas;
import com.igreja.unijovem.entity.Usuario;
import com.igreja.unijovem.repository.ParametrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ParametrosService {

    @Autowired
    private ParametrosRepository parametrosRepository;

    public Optional<Parametros> buscarParametrosAtuais() {
        return parametrosRepository.findLatest();
    }

    public Parametros salvarParametros(Parametros parametros) {
        return parametrosRepository.save(parametros);
    }

    public boolean existemParametros() {
        return parametrosRepository.countParametros() > 0;
    }

    public Parametros criarParametrosIniciais() {
        if (!existemParametros()) {
            Parametros parametros = new Parametros();
            parametros.setValorMensalidade(java.math.BigDecimal.valueOf(50.0));
            parametros.setValorCamisa(java.math.BigDecimal.valueOf(35.0));
            return parametrosRepository.save(parametros);
        }
        return buscarParametrosAtuais().orElse(null);
    }

}