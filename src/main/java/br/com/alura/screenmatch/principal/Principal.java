package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private List<DadosSerie> dadosSeries = new  ArrayList<>();

    private SerieRepository repositorio;

    private List<Serie> series = new  ArrayList<>();

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    private Optional<Serie> serie;

    public void exibeMenu() {
    var opcao = -1;
        while(opcao != 0){
            var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries buscadas
                4 - Buscar série por titulo
                5 - Buscar séries por ator
                6 - Top 5 séries por avaliação
                7 - Buscar séries por gênero
                8 - Filtrar séries
                9 - Buscar episódio por trecho
                10 - Top episodios por Série
                
                0 - Sair                                 
                """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarPorGenero();
                    break;
                case 8:
                    buscarPorMaximoDeTemporadas();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarTop5Episodios();
                    break;
                case 11:
                    buscarEpisodiosApartirDeData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }


    }



    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY + "&plot=full");
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Escolha uma série para ver os episódios:");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if(serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }
        else{
            System.out.println("Série não encontrada.");
        }
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
        dadosSeries.forEach(System.out::println);
    }


    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série para ver os episódios:");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBuscada.isPresent()){
            System.out.println("Dados da Série: " + serieBuscada.get());
            serie = serieBuscada;
        }
        else {
            System.out.println("Série não encontrada.");
        }
    }



    private void buscarSeriePorAtor() {
        System.out.println("Qual o nome para busca?");
        var nomeAtor = leitura.nextLine();
        System.out.println("Qual a avaliação mínima?");
        var avaliacaoMinima = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByElencoContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacaoMinima).stream().toList();
        if(!seriesEncontradas.isEmpty()) {
            System.out.println("Séries em que " +  nomeAtor + " atuou:");
            seriesEncontradas.forEach(s ->
                    System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
        }
        else{
            System.out.println("Nenhuma série encontrada com o ator " + nomeAtor + " com avaliação mínima de " + avaliacaoMinima);
        }
    }


    private void buscarTop5Series() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc().stream().toList();
        System.out.println("Top 5 séries por avaliação:");
        serieTop.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarPorGenero() {
        System.out.println("Qual categoria deseja buscar?");
        var categoria = leitura.nextLine();
        try{
            Categoria genero = Categoria.fromPortugues(categoria);
            List<Serie> seriesEncontradas = repositorio.findByGenero(genero);
            System.out.println("Séries encontradas na categoria " + categoria + ":");
            seriesEncontradas.forEach(s ->
                    System.out.println(s.getTitulo() + " - " + s.getGenero().toString()));
        }
        catch(IllegalArgumentException e){
            System.out.println("Não foi possível encontrar a categoria: " + categoria);
        }
    }

    private void buscarPorMaximoDeTemporadas() {
        System.out.println("Qual o número máximo de temporadas?");
        var maxTemporadas = leitura.nextInt();
        System.out.println("Qual a avaliação mínima?");
        var avaliacaoMinima = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.seriesPorTemporadaEAvaliacao(maxTemporadas, avaliacaoMinima);
        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo() + " - " + s.getGenero().toString()));
    }

    private void buscarEpisodioPorTrecho(){
        System.out.println("Digite um trecho do episódio para busca:");
        var trecho = leitura.nextLine();
        try{
            List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trecho);
            if(episodiosEncontrados.isEmpty()){
                System.out.println("Nenhum episódio encontrado com o trecho: " + trecho);
                return;
            }
            episodiosEncontrados.forEach(e ->
                    System.out.println("Episódio: " + e.getNumeroEpisodio() + " " +  e.getTitulo() + " Temporada: " + e.getTemporada() + " - Série: " + e.getSerie().getTitulo()));
        }
        catch(Exception e){
            System.out.println("Erro ao buscar episódios por trecho: " + e.getMessage());
        }
    }

    private void buscarTop5Episodios(){
        buscarSeriePorTitulo();
        if(serie.isPresent()){
            Serie serieBusca = serie.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serieBusca);
            topEpisodios.forEach( e ->
                    System.out.println( " Temporada: " + e.getTemporada()  + " "+  "Episodio: " + e.getNumeroEpisodio() + " " +  e.getTitulo() +" Avaliação: " + e.getAvaliacao()));
        }
    }

    private void buscarEpisodiosApartirDeData(){
        buscarSeriePorTitulo();
        if(serie.isPresent()){
            Serie serieBusca = serie.get();
            System.out.println("Digite o ano limite para a busca ");
            var dataInput = leitura.nextLine();
            try{
                List<Episodio> episodiosAno = repositorio.episodiosPorAno(serieBusca, dataInput);
                episodiosAno.forEach( e ->
                        System.out.println(
                                "Episodio: " + e.getNumeroEpisodio() +
                                        " Temporada: " + e.getTemporada() +
                                        " - " + e.getTitulo() +
                                        " Data de Lançamento: " + e.getDataLancamento()
                        ));
            }
            catch (IllegalArgumentException e){
                System.out.println("Erro ao buscar episódios a partir do ano: " + e.getMessage());
            }
        }
    }


}