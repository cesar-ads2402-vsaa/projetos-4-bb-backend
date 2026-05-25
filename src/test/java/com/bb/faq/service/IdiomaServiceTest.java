package com.bb.faq.service;

import com.bb.faq.DTOs.IdiomaRequestDTO;
import com.bb.faq.DTOs.IdiomaResponseDTO;
import com.bb.faq.model.Idioma;
import com.bb.faq.repository.IdiomaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdiomaServiceTest {

    @Mock
    private IdiomaRepository repository;

    @InjectMocks
    private IdiomaService service;

    @Test
    @DisplayName("Deve retornar lista de DTOs ao listar todos os idiomas")
    void deveListarTodosOsIdiomas() {
        Idioma pt = new Idioma();
        pt.setId(1L);
        pt.setNome("Português");
        pt.setCodigo("pt-BR");

        when(repository.findAll()).thenReturn(List.of(pt));

        List<IdiomaResponseDTO> resultado = service.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Português", resultado.get(0).nome());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve salvar um novo idioma e retornar o DTO correspondente")
    void deveCriarIdiomaComSucesso() {
        IdiomaRequestDTO request = new IdiomaRequestDTO("Espanhol", "es");
        Idioma idiomaSalvo = new Idioma();
        idiomaSalvo.setId(2L);
        idiomaSalvo.setNome("Espanhol");
        idiomaSalvo.setCodigo("es");

        when(repository.save(any(Idioma.class))).thenReturn(idiomaSalvo);

        IdiomaResponseDTO resultado = service.criar(request);

        assertNotNull(resultado);
        assertEquals("Espanhol", resultado.nome());
        assertEquals("es", resultado.codigo());
        verify(repository, times(1)).save(any(Idioma.class));
    }

    @Test
    @DisplayName("Deve chamar o repository para deletar um idioma pelo ID")
    void deveDeletarIdioma() {
        Long idParaDeletar = 10L;
        doNothing().when(repository).deleteById(idParaDeletar);

        service.deletar(idParaDeletar);

        verify(repository, times(1)).deleteById(idParaDeletar);
    }
}