package com.alura.literalura.repository;

import com.alura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTitulo(String titulo);
    List<Libro> findByIdioma(String idioma);
    Long countByIdioma(String idioma);
    // Carga cada libro con su autor en la misma consulta
    @Query("select l from Libro l join fetch l.autor")
    List<Libro> findAllWithAutor();
}