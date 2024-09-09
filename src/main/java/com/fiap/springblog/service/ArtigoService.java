package com.fiap.springblog.service;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoService {

    List<Artigo> obterTodos();

    Artigo obterPorCodigo(String codigo);

    Artigo criar(Artigo artigo);

    ResponseEntity<?> criarComException(Artigo artigo);

    List<Artigo> findByDataGreaterThan(LocalDateTime data);

    List<Artigo> findByDataAndStatus(LocalDateTime data, Integer status);

    void atualizar(Artigo artigo);

    void atualizarArtigo(String id, String novaURL);

    ResponseEntity<?> atualizarArtigo(String id, Artigo artigo);

    void deleteById(String id);

    void deleteArtigoById(String id);

    List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime date);

    List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate);

    List<Artigo> encontrarArtigosComplexos(Integer status, LocalDateTime data, String titulo);

    Page<Artigo> findAll(Pageable pageable);

    List<Artigo> findByStatusOrderByTituloAsc(Integer status);

    List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status);

    List<Artigo> findByTexto(String searchTerm);

    List<ArtigoStatusCount> contarArtigosPorStatus();

    List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(LocalDate inicio, LocalDate fim);

    ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor);

    void excluirArtigoEAutor(Artigo artigo);

}
