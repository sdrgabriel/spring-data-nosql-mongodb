package com.fiap.springblog.service;

import com.fiap.springblog.model.Autor;
import org.springframework.stereotype.Service;

public interface AutorService {

    Autor criar(Autor autor);

    Autor obterPorCodigo(String codigo);

}
