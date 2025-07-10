package com.igreja.unijovem.service;

import com.igreja.unijovem.entity.Usuario;
import com.igreja.unijovem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Usuario> buscarPorSexo(String sexo) {
        return usuarioRepository.findBySexo(sexo);
    }

    public List<Usuario> buscarComCargoMinisterial() {
        return usuarioRepository.findByCargoMinisterialTrue();
    }

    public List<Usuario> buscarComDividas() {
        return usuarioRepository.findByDividasTrue();
    }

    public List<Usuario> buscarComCargoUnijovem() {
        return usuarioRepository.findByCargoUnijovemNotEmpty();
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);

        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();

            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setSexo(usuarioAtualizado.getSexo());
            usuario.setTamanhoCamisa(usuarioAtualizado.getTamanhoCamisa());
            usuario.setResponsavelDireto(usuarioAtualizado.getResponsavelDireto());
            usuario.setDataNascimento(usuarioAtualizado.getDataNascimento());
            usuario.setCargoMinisterial(usuarioAtualizado.getCargoMinisterial());
            usuario.setCargoUnijovem(usuarioAtualizado.getCargoUnijovem());
            usuario.setDividas(usuarioAtualizado.getDividas());

            return usuarioRepository.save(usuario);
        }

        throw new RuntimeException("Usuário não encontrado com ID: " + id);
    }

    public void deletar(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
    }

    public boolean existePorId(Long id) {
        return usuarioRepository.existsById(id);
    }

    public long contarTodos() {
        return usuarioRepository.count();
    }

    public Map<String, List<Usuario>> listarAniversariantesPorMes() {
        List<Usuario> todosUsuarios = usuarioRepository.findAllWithDataNascimentoOrdered();

        // Nomes dos meses em português
        String[] meses = {
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };

        // Criar mapa ordenado com todos os meses
        Map<String, List<Usuario>> aniversariantesPorMes = new LinkedHashMap<>();

        // Inicializar todos os meses
        for (int i = 1; i <= 12; i++) {
            aniversariantesPorMes.put(meses[i-1], new ArrayList<>());
        }

        // Agrupar usuários por mês
        for (Usuario usuario : todosUsuarios) {
            if (usuario.getDataNascimento() != null) {
                int mes = usuario.getDataNascimento().getMonthValue();
                aniversariantesPorMes.get(meses[mes-1]).add(usuario);
            }
        }

        return aniversariantesPorMes;
    }

    public List<Usuario> listarAniversariantesDoMes(Integer mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("Mês deve estar entre 1 e 12");
        }
        return usuarioRepository.findByMesAniversario(mes);
    }

    public List<Usuario> listarAniversariantesHoje() {
        return usuarioRepository.findAniversariantesHoje();
    }

    public List<Usuario> listarProximosAniversariantes(Integer dias) {
        if (dias == null || dias <= 0) {
            dias = 30; // Default 30 dias
        }
        return usuarioRepository.findProximosAniversariantes(dias);
    }
}