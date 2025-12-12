package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String titulo);

    Optional<Serie> findByElencoContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, double avaliacaoMinima);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findTop5ByTotalTemporadasIsLessThanEqualAndAvaliacaoIsGreaterThanEqual(int totalTemporadas, double avaliacao);

    @Query(value = "select s from Serie s where s.totalTemporadas >= :totalTemporadas and s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(int totalTemporadas, double avaliacao);

    @Query(value = "select e, s from Serie s join s.episodios e where e.titulo ilike %:trecho%")
    List<Episodio> episodiosPorTrecho(String trecho);

    @Query(value = "select e from Serie s join s.episodios e where s = :serieBusca order by e.avaliacao desc limit 5")
    List<Episodio> topEpisodiosPorSerie(Serie serieBusca);

    @Query(value = "select e from Serie s join s.episodios e where s = :serieBusca and year(e.dataLancamento) >= :dataInput")
    List<Episodio> episodiosPorAno(Serie serieBusca, String dataInput);
}
