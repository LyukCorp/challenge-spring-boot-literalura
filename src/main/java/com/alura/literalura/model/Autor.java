package com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(
        name = "autor",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_autor_nombre_nacimiento", columnNames = {"nombre", "nacimiento"})
        }
)
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = true)
    private String nombre;
    @Column(nullable = true)
    private Integer fechaDeCumplenos;
    @Column(nullable = true)
    private Integer fechaDeMuerte;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Libro> libros;

    public Autor() {}

    public Autor(DatosAutor datos) {
        this.nombre = datos.nombre();
        this.fechaDeCumplenos = datos.fechaDeCumplenos();
        this.fechaDeMuerte = datos.fechaDeMuerte();
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFechaDeCumplenos() {
        return fechaDeCumplenos;
    }

    public void setFechaDeCumplenos(Integer fechaDeCumplenos) {
        this.fechaDeCumplenos = fechaDeCumplenos;
    }

    public Integer getFechaDeMuerte() {
        return fechaDeMuerte;
    }

    public void setFechaDeMuerte(Integer fechaDeMuerte) {
        this.fechaDeMuerte = fechaDeMuerte;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }
}
