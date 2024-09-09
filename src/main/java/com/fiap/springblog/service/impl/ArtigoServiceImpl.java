package com.fiap.springblog.service.impl;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import com.fiap.springblog.repository.ArtigoRepository;
import com.fiap.springblog.repository.AutorRepository;
import com.fiap.springblog.service.ArtigoService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtigoServiceImpl implements ArtigoService {

    private final MongoTemplate mongoTemplate;

    public ArtigoServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Autowired
    private ArtigoRepository artigoRepository;
    @Autowired
    private AutorRepository autorRepository;
    @Autowired
    private MongoTransactionManager transactionManager;

    @Override
    public List<Artigo> obterTodos() {
        return this.artigoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Artigo obterPorCodigo(String codigo) {
        return this.artigoRepository.findById(codigo).orElseThrow(
                ()-> new IllegalArgumentException("Argumento não existe")
        );
    }

    @Override
    @Transactional
    public ResponseEntity<?> criarComException(Artigo artigo) {
        try {
            if (artigo.getAutor().getCodigo() != null) {
                Autor autor = this.autorRepository.findById(artigo.getAutor().getCodigo())
                        .orElseThrow(()-> new IllegalArgumentException("Autor inexistente"));
                artigo.setAutor(autor);
            }
            else {
                artigo.setAutor(null);
            }
        } catch (NullPointerException e) {
            artigo.setAutor(null);
        }

        try {
            this.artigoRepository.save(artigo);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Artigo já existe na coleção!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar artigo: "+ e.getMessage());
        }
    }

    @Override
    @Transactional
    public Artigo criar(Artigo artigo) {

        try {
            if (artigo.getAutor().getCodigo() != null) {
            Autor autor = this.autorRepository.findById(artigo.getAutor().getCodigo())
                    .orElseThrow(()-> new IllegalArgumentException("Autor inexistente"));
            artigo.setAutor(autor);
            }
            else {
                artigo.setAutor(null);
            }
        } catch (NullPointerException e) {
            artigo.setAutor(null);
        }

        try {
            return this.artigoRepository.save(artigo);
        } catch (OptimisticLockingFailureException ex) {
            Artigo atualizado = artigoRepository.findById(artigo.getCodigo()).orElse(null);
            if (atualizado != null) {
                atualizado.setTitulo(artigo.getTitulo());
                atualizado.setTexto(artigo.getTexto());
                atualizado.setStatus(artigo.getStatus());

                atualizado.setVersion(atualizado.getVersion() + 1);
                return this.artigoRepository.save(atualizado);
            }
            else {
                throw  new RuntimeException("Artigo não encontrado " + artigo.getCodigo());
            }
        }
    }

    @Override
    public List<Artigo> findByDataGreaterThan(LocalDateTime data) {
        Query query = new Query(Criteria.where("data").gt(data));
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public List<Artigo> findByDataAndStatus(LocalDateTime data, Integer status) {
        Query query = new Query(
                Criteria.where("data")
                        .is(data)
                        .and("status")
                        .is(status)
        );
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    @Transactional
    public void atualizar(Artigo artigo) {
        this.artigoRepository.save(artigo);
    }

    @Override
    @Transactional
    public void atualizarArtigo(String id, String novaURL) {
        Query query = new Query(
                Criteria.where("id")
                        .is(id)
        );
        Update update = new Update().set("url", novaURL);
        this.mongoTemplate.updateFirst(query, update, Artigo.class);
    }

    @Override
    @Transactional
    public ResponseEntity<?> atualizarArtigo(String id, Artigo artigo) {
        try {
            Artigo extisteArtigo =
                    this.artigoRepository.findById(id).orElse(null);
            if (extisteArtigo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Artigo não encontrado na Coleção");
            }

            extisteArtigo.setTitulo(artigo.getTitulo());
            extisteArtigo.setData(artigo.getData());
            extisteArtigo.setTexto(artigo.getTexto());
            this.artigoRepository.save(extisteArtigo);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar artigo: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(String id) {
        this.artigoRepository.deleteById(id);
    }

    @Override
    public void deleteArtigoById(String id) {
        Query query = new Query(
                Criteria.where("id")
                        .is(id)
        );
        this.mongoTemplate.remove(query, Artigo.class);
    }

    @Override
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime date) {
        return this.artigoRepository.findByStatusAndDataGreaterThan(status, date);
    }

    @Override
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate) {
        return this.artigoRepository.obterArtigoPorDataHora(de, ate);
    }

    @Override
    public List<Artigo> encontrarArtigosComplexos(Integer status, LocalDateTime data, String titulo) {
        Criteria criteria = new Criteria();
        criteria.and("data").lte(data);
        if (status != null) {
            criteria.and("status").is(status);
        }

        if (StringUtils.isBlank(titulo)) {
            criteria.and("titulo").is(titulo);
        }
        return this.mongoTemplate.find(new Query(criteria), Artigo.class);
    }

    @Override
    public Page<Artigo> findAll(Pageable pageable) {
        Sort sort = Sort.by("titulo").ascending();
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return this.artigoRepository.findAll(pageable);
    }

    @Override
    public List<Artigo> findByStatusOrderByTituloAsc(Integer status) {
        return this.artigoRepository.findByStatusOrderByTituloAsc(status);
    }

    @Override
    public List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status) {
        return this.artigoRepository.obterArtigoPorStatusComOrdenacao(status);
    }

    @Override
    public List<Artigo> findByTexto(String searchTerm) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(searchTerm);
        Query query = TextQuery.queryText(criteria).sortByScore();
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                        Artigo.class,
                        Aggregation.group("status").count().as("quantidade"),
                        Aggregation.project("quantidade").and("status").previousOperation()
                );
        AggregationResults<ArtigoStatusCount> result =
                mongoTemplate.aggregate(aggregation, ArtigoStatusCount.class);
        return result.getMappedResults();
    }

    @Override
    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorNoPeriodo(LocalDate inicio, LocalDate fim) {
        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                        Artigo.class,
                        Aggregation.match(
                                Criteria.where("data")
                                        .gte(inicio.atStartOfDay())
                                        .lte(fim.plusDays(1).atStartOfDay())
                        ),
                        Aggregation.group("autor").count().as("totalArtigos"),
                        Aggregation.project("totalArtigos").and("autor").previousOperation()
                );
        AggregationResults<AutorTotalArtigo> results =
                mongoTemplate.aggregate(aggregation, AutorTotalArtigo.class);
        return results.getMappedResults();
    }

    @Override
    public ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor) {

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.execute(status -> {
            try {
                this.autorRepository.save(autor);
                artigo.setData(LocalDateTime.now());
                artigo.setAutor(autor);
                artigoRepository.save(artigo);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException("Erro ao criar artigo com autor: " + e.getMessage());
            }
            return null;
        });

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public void excluirArtigoEAutor(Artigo artigo) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.execute(status -> {
            try {
                this.autorRepository.delete(artigo.getAutor());
                this.artigoRepository.delete(artigo);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException("Erro ao deletar artigo com autor: " + e.getMessage());
            }
            return null;
        });
    }

}
