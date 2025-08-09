package com.alura.literalura.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "libros", uniqueConstraints = {
        @UniqueConstraint(name = "uk_libro_gutendex_id", columnNames = {"gutendex_id"}),
        @UniqueConstraint(name = "uk_libro_titulo", columnNames = {"titulo"})
})
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo para control de concurrencia optimista
    @Version
    private Long version;

    @Column(name = "gutendex_id")
    private Integer gutendexId;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 10)
    private String idioma;

    @Column(length = 4000)
    private String resumen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id")
    private Autor autor;




    protected Libro() {
        // Constructor JPA
    }

    // Constructor usado por tu lógica: NO asigna 'id' ni 'version'
    public Libro(DatosLibro datos, Autor autor) {
        if (datos != null) {
            this.titulo = datos.titulo();
            this.idioma = (datos.idiomas() != null && !datos.idiomas().isEmpty()) ? datos.idiomas().get(0) : null;
            this.resumen = String.valueOf(datos.resumen());
            // Asigna el id externo a un campo separado
            this.gutendexId = Math.toIntExact(datos.id()); // Ajusta si el nombre del método difiere
        }
        this.autor = autor;
    }

    // Getters/Setters

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public Integer getGutendexId() {
        return gutendexId;
    }

    public void setGutendexId(Integer gutendexId) {
        this.gutendexId = gutendexId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    // equals/hashCode por id (si está presente)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Libro)) return false;
        Libro libro = (Libro) o;
        return id != null && id.equals(libro.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", idioma='" + idioma + '\'' +
                ", autor=" + (autor != null ? autor.getNombre() : null) +
                '}';
    }
}
