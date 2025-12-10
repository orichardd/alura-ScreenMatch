package br.com.alura.screenmatch.model;


import br.com.alura.screenmatch.service.ConsultaGemini;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    String titulo;
    String ano;
    Integer totalTemporadas;
    double avaliacao;
    @Enumerated(EnumType.STRING)
    Categoria genero;
    String elenco;
    @Column(columnDefinition = "TEXT")
    String sinopse;
    String linkPoster;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();

    public Serie(DadosSerie dadosSerie){
        this.titulo = dadosSerie.titulo();
        this.ano = dadosSerie.ano();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0.0);
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.elenco = dadosSerie.elenco();
        this.sinopse = ConsultaGemini.traduzir(dadosSerie.sinopse());
        this.linkPoster = dadosSerie.linkPoster();
    }

    public Serie() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getElenco() {
        return elenco;
    }

    public void setElenco(String elenco) {
        this.elenco = elenco;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getLinkPoster() {
        return linkPoster;
    }

    public void setLinkPoster(String linkPoster) {
        this.linkPoster = linkPoster;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e -> e.setSerie(this));
        this.episodios = episodios;
    }

    @Override
    public String toString() {
        getEpisodios().forEach(e -> e.setSerie(this));

        return """
                Título: %s
                Ano: %s
                Total de Temporadas: %d
                Avaliação: %.1f
                Gênero: %s
                Elenco: %s
                Sinopse: %s
                Link do Poster: %s
                
                Episódios: %d
                """.formatted(titulo, ano, totalTemporadas, avaliacao, genero, elenco, sinopse, linkPoster, episodios.size());
    }
}
