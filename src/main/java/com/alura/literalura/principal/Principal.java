package com.alura.literalura.principal;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.DatosAutor;
import com.alura.literalura.model.DatosLibro;
import com.alura.literalura.model.DatosResultados;
import com.alura.literalura.model.Libro;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumoApi = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private static final String URL_BASE = "https://gutendex.com/books/?";


    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    // Asegura sesión abierta durante la lectura y el println(toString())
    @Transactional(readOnly = true)
    public void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAllWithAutor();
        libros.forEach(System.out::println);
    }


    public void visualizarMenu() {
        String menu = """
                ----------------------------
                Elija la opción a través de su número:
                1- Buscar libro por título
                2- Listar libros registrados
                3- Listar autores registrados
                4- Listar autores vivos en un determinado año
                5- Listar libros por idiomas

                0- Salir
                """;
        byte opcion = -1;

        while (opcion != 0) {
            System.out.println(menu);
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                System.out.println("Ingrese una opción válida.");
                continue;
            }
            try {
                opcion = Byte.parseByte(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida.");
                continue;
            }

            switch (opcion) {
                case 1 -> buscarLibroPorTitulo();
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivosEnAnio();
                case 5 -> listarLibrosPorIdioma();
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    // 1 - Buscar libro por título (con confirmación para guardar)
    private void buscarLibroPorTitulo() {
        System.out.print("Título a buscar: ");
        String titulo = scanner.nextLine().trim();
        if (titulo.isEmpty()) {
            System.out.println("Debe ingresar un título.");
            return;
        }

        Optional<Libro> existente = libroRepository.findByTitulo(titulo);
        if (existente.isPresent()) {
            System.out.println("Ya está registrado:");
            System.out.println(existente.get());
            return;
        }

        String url = URL_BASE + "search=" + URLEncoder.encode(titulo, StandardCharsets.UTF_8);
        String json = consumoApi.obtenerDatos(url);
        if (json == null || json.isBlank()) {
            System.out.println("No hubo respuesta del servidor.");
            return;
        }

        DatosResultados resultados = conversor.obtenerDatos(json, DatosResultados.class);
        if (resultados == null || resultados.resultados() == null || resultados.resultados().isEmpty()) {
            System.out.println("No se encontraron resultados");
            return;
        }

        DatosLibro datos = resultados.resultados().stream()
                .filter(dl -> dl.titulo() != null && dl.titulo().toLowerCase().contains(titulo.toLowerCase()))
                .findFirst()
                .orElse(resultados.resultados().get(0));

        System.out.println("\nResultado encontrado:");
        System.out.println("Título: " + datos.titulo());
        String idioma = (datos.idiomas() != null && !datos.idiomas().isEmpty()) ? datos.idiomas().get(0) : "desconocido";
        System.out.println("Idioma: " + idioma);
        System.out.println("Resumen: " + datos.resumen());
        String autorNombre = (datos.autor() != null && !datos.autor().isEmpty()) ? datos.autor().get(0).nombre() : "Autor desconocido";
        System.out.println("Autor: " + autorNombre);

        System.out.print("¿Desea guardarlo? SI(s)/NO(n): ");
        String resp = scanner.nextLine().trim();
        if (!resp.equalsIgnoreCase("S")) {
            System.out.println("No se guardó.");
            return;
        }

        Autor autor = null;
        if (datos.autor() != null && !datos.autor().isEmpty()) {
            DatosAutor da = datos.autor().get(0);
            autor = autorRepository.findFirstByNombreIgnoreCase(da.nombre())
                    .orElseGet(() -> autorRepository.save(new Autor(da)));
        }

        Libro libro = new Libro(datos, autor);
        libroRepository.save(libro);
        System.out.println("Guardado correctamente.");
    }


    // 3 - Listar autores registrados
    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }
        autores.forEach(System.out::println);
    }

    // 4 - Listar autores vivos en un año
    private void listarAutoresVivosEnAnio() {
        System.out.print("Ingrese el año: ");
        String entrada = scanner.nextLine().trim();
        try {
            int anio = Integer.parseInt(entrada);
            List<Autor> vivos = autorRepository.findAutoresVivosEnAnio(anio);
            if (vivos.isEmpty()) {
                System.out.println("No hay autores vivos en ese año.");
            } else {
                vivos.forEach(System.out::println);
            }
        } catch (NumberFormatException e) {
            System.out.println("Año inválido.");
        }
    }

    // 5 - Listar libros por idioma (y cantidad)
    private void listarLibrosPorIdioma() {
        System.out.print("Ingrese código de idioma (es, en, fr, pt, ...): ");
        String idioma = scanner.nextLine().trim().toLowerCase();
        if (idioma.isEmpty()) {
            System.out.println("Debe ingresar un idioma.");
            return;
        }
        List<Libro> libros = libroRepository.findByIdioma(idioma);
        long cantidad = libroRepository.countByIdioma(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
            return;
        }
        System.out.println("Cantidad de libros en '" + idioma + "': " + cantidad);
        libros.forEach(System.out::println);
    }
}
