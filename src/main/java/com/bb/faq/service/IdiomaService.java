package com.bb.faq.service;

import com.bb.faq.DTOs.IdiomaRequestDTO;
import com.bb.faq.DTOs.IdiomaResponseDTO;
import com.bb.faq.model.Idioma;
import com.bb.faq.repository.IdiomaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Ensina o Spring que esta classe contém a lógica de negócio
public class IdiomaService {

    @Autowired
    private IdiomaRepository repository;

    public List<IdiomaResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(IdiomaResponseDTO::new)
                .toList();
    }

    public IdiomaResponseDTO criar(IdiomaRequestDTO dto) {
        Idioma idioma = new Idioma();
        idioma.setNome(dto.nome());
        idioma.setCodigo(dto.codigo());

        Idioma idiomaSalvo = repository.save(idioma);
        return new IdiomaResponseDTO(idiomaSalvo);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}