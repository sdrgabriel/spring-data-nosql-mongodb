package com.fiap.springblog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class Artigo {

    @Id
    private String codigo;

    @NotBlank(message = "O titulo do artigo não pode estar em branco.")
    private String titulo;

    @NotNull(message = "A data do artigo não pode ser nula.")
    private LocalDateTime data;

    @TextIndexed
    @NotBlank(message = "O texto do artigo não pode estar em branco.")
    private String texto;

    private String url;

    @NotBlank(message = "O status do artigo não pode estar em branco.")
    private Integer status;

    @DBRef
    private Autor autor;

    @Version
    private Long version;

}
