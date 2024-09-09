/**
 * Autor: Gabriel Ricardo
 * Data: 01/08/2023
 * Hora: 15h
 * Objetivo: Criar um controlador REST para o projeto
 */
package com.fiap.springblog.controller;

import com.fiap.springblog.model.*;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/artigos")
public class ArtigoController {

    @Autowired
    private ArtigoService artigoService;

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handlerOptimisticLockingFailureException(OptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Erro de concorrência: O Artigo foi atualizado por outro usúario, Favor tente novamente!");
    }

    @GetMapping
    public List<Artigo> obterTodos() {
        return this.artigoService.obterTodos();
    }

    @GetMapping("/{codigo}")
    public Artigo obterPorCodigo(@PathVariable String codigo) {
        return this.artigoService.obterPorCodigo(codigo);
    }

    @PostMapping
    public Artigo criar(@RequestBody Artigo artigo) {
        return this.artigoService.criar(artigo);
    }

    @PostMapping("/criar")
    public ResponseEntity<?> criarComException  (
            @RequestBody Artigo artigo) {
        return this.artigoService.criarComException(artigo);
    }

    @GetMapping("/data")
    public List<Artigo> findByDataGreaterThan(@RequestParam("data") LocalDateTime data) {
        return this.artigoService.findByDataGreaterThan(data);
    }

    @GetMapping("/data-status")
    public List<Artigo> findByDataAndStatus(
            @RequestParam("data") LocalDateTime data,
            @RequestParam("status") Integer status) {
        return this.artigoService.findByDataAndStatus(data, status);
    }

    @PutMapping
    public void atualizar(@RequestBody Artigo artigo) {
        this.artigoService.atualizar(artigo);
    }

    @PutMapping("/atualizar-url/{id}")
    public void atualizarArtigo(
            @PathVariable String id,
            @RequestBody String novaURL) {
        this.artigoService.atualizarArtigo(id, novaURL);
    }

    @PutMapping("atualizar-artigo/{id}")
    public ResponseEntity<?> atualizarArtigo(
            @PathVariable String id,
            @RequestBody Artigo artigo) {
        return this.artigoService.atualizarArtigo(id, artigo);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        this.artigoService.deleteById(id);
    }

    @DeleteMapping("/delete")
    public void deleteArtigoById(@RequestParam("Id") String id) {
        this.artigoService.deleteArtigoById(id);
    }

    @GetMapping("/status-maiordata")
    public List<Artigo> findByStatusAndDataGreaterThan(
            @RequestParam("status") Integer status,
            @RequestParam("data") LocalDateTime date) {
        return this.artigoService.findByStatusAndDataGreaterThan(status, date);
    }

    @GetMapping("/periodo")
    public List<Artigo> obterArtigoPorDataHora(
            @RequestParam("de") LocalDateTime de,
            @RequestParam("ate") LocalDateTime ate) {
        return this.artigoService.obterArtigoPorDataHora(de, ate);
    }

    @GetMapping("/complexos")
    public List<Artigo> encontrarArtigosComplexos(
            @RequestParam("status") Integer status,
            @RequestParam("data") LocalDateTime data,
            @RequestParam("titulo") String titulo) {
        return this.artigoService.encontrarArtigosComplexos(status, data, titulo);
    }

    @GetMapping("/paginacao")
    public ResponseEntity<Page<Artigo>> findAll(
            @PageableDefault(size = 5, page = 0, sort = {"title"}) Pageable pageable) {
        Page<Artigo> artigos = this.artigoService.findAll(pageable);
        return ResponseEntity.ok(artigos);
    }

    @GetMapping("/status-ordenado")
    public List<Artigo> findByStatusOrderByTituloAsc(
            @RequestParam("status") Integer status) {
        return this.artigoService.findByStatusOrderByTituloAsc(status);
    }

    @GetMapping("/status-query-ordenacao")
    public List<Artigo> obterArtigoPorStatusComOrdenacao(
            @RequestParam("status") Integer status) {
        return this.artigoService.obterArtigoPorStatusComOrdenacao(status);
    }

    @GetMapping("/buscatexto")
    public List<Artigo> findByTexto(
            @RequestParam("searchTerm") String termo) {
        return this.artigoService.findByTexto(termo);
    }

    @GetMapping("/contar-artigo")
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        return this.artigoService.contarArtigosPorStatus();
    }

    @GetMapping("/total-artigos-autor-periodo")
    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(
            @RequestParam("inicio") LocalDate inicio,
            @RequestParam("fim") LocalDate fim) {
        return this.artigoService.calcularTotalArtigosPorAutorNoPeriodo(inicio, fim);
    }

    @PutMapping("/comAutor")
    public ResponseEntity<?> criarArtigoComAutor(
            @RequestBody ArtigoComAutorRequest artigoComAutorRequest) {
        return this.artigoService.criarArtigoComAutor(artigoComAutorRequest.getArtigo(), artigoComAutorRequest.getAutor());
    }

    @DeleteMapping("/completo")
    public void excluirArtigoEAutor(@RequestBody Artigo artigo) {
        this.artigoService.excluirArtigoEAutor(artigo);
    }

}
