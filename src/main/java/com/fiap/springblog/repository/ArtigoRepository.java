package com.fiap.springblog.repository;

import com.fiap.springblog.model.Artigo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoRepository extends MongoRepository<Artigo, String> {

    void deleteById(String id);

    List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime date);

    @Query("{ $and: [ { 'data': { $gte: ?0 } }. { 'data': { $lte: ?1 } } ] }")
    List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate);

    List<Artigo> findByStatusOrderByTituloAsc(Integer status);

    @Query(value = "{ 'status':{$eq: ?0}}", sort = "{'titulo': 1}")
    List<Artigo> obterArtigoPorStatusComOrdenacao(Integer status);
}
