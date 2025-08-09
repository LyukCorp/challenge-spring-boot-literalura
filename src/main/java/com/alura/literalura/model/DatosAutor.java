package com.alura.literalura.model;


import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosAutor(
        @JsonAlias("name") String nombre,
        @JsonAlias("birth_year") Integer fechaDeCumplenos,
        @JsonAlias("death_year") Integer fechaDeMuerte
    ) {
}
