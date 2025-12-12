package br.com.alura.screenmatch.model;

public enum Categoria {
    ACAO("Action", "Acão"),
    AVENTURA("Adventure", "Aventura"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    FICCAO_CIENTIFICA("Science Fiction", "Ficção Científica"),
    ROMANCE("Romance", "Romance"),
    TERROR("Horror", "Terror"),
    ANIMACAO("Animation", "Animação"),
    CRIME("Crime", "Crime"),;

    private String categoriaOmdb;

    private String categoriaPortugues;


    Categoria(String categoriaOmdb, String categoriaPortugues) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
    public static Categoria fromPortugues(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortugues.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
