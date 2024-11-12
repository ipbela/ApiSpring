package com.example.springboot.services;

import com.example.springboot.models.*;
import com.example.springboot.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatbotServices {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private CardapioRepository cardapioRepository;

    @Autowired
    private FilaRepository filaRepository;

    @Autowired
    private MonitoramentoRepository monitoramentoRepository;

    @Autowired
    private InfoRestauranteRepository infoRestauranteRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private AvaliacaoChatRepository avaliacaoChatRepository;


    // Variável para armazenar o estado atual do fluxo
    private String currentFlow;

    // Método que inicia a conversa
    public MessageModel startConversation() {
        // Resetando o fluxo ao iniciar a conversa
        currentFlow = null;
        // Envia a mensagem de boas vindas
        return new MessageModel(
                "Olá, eu sou a assistente virtual do HAPPDINE. Estou aqui para tornar sua experiência mais prática e agradável. Para facilitar seu atendimento, selecione o serviço desejado: ",
                "bot",
                getMainOptions()
        );
    }

    // Método que identifica a escolha do usuário
    public MessageModel handleUserInput(String userInput) {
        switch (userInput) {
            case "Cardápio":
                return showRestaurantsForMenu();
            case "Monitoramento":
                //aumenta as opções de escolha, redirecionando o usuário para uma opção mais específica
                return new MessageModel("Toque na opção desejada:", "bot", Arrays.asList("Selecionar por praça", "Maior Fila", "Menor Fila", "Movimentação por Horário", "Voltar para o início", "Encerrar o chat"));
            case "Selecionar por praça":
                return showRestaurantsForLocations();
            case "Queridinho do dia":
                return showVotes();
            case "Maior Fila":
                return showBigLine();
            case "Menor Fila":
                return showSmallLine();
            case "Movimentação por Horário":
                return showMoveforHours();
            case "Informações":
                //aumenta as opções de escolha, redirecionando o usuário para uma opção mais específica
                return new MessageModel("Toque na opção desejada: ", "bot", Arrays.asList("Horário de Funcionamento", "Refeições Oferecidas", "Localização", "Voltar para o início", "Encerrar o chat"));
            case "Horário de Funcionamento":
                return showOperatingHours();
            case "Refeições Oferecidas":
                return showMealsProvided();
            case "Localização":
                return showLocal();
            case "Voltar para o início":
                return startConversation();
            case "Chatbot":
                return messageAboutChatBot();
            case "Encerrar o chat":
                return finishChat();
            case "Ótimo":
                return avaliationGreat();
            case "Ruim":
                return avaliationBad();
            default:
                //armazena o restaurante selecionado de acordo com a escolha do usuário
                RestauranteModel selectedRestaurant = restauranteRepository.findByNome(userInput);
                //se a variavel for diferente de vazia, entra nessa condição
                if (selectedRestaurant != null) {
                    //se o metodo para verificar se esta no fluxo do menu, retornar true, entra nessa condição
                    if (isInMenuFlow()) {
                        //encaminha para o metodo
                        return showMenuForRestaurant(selectedRestaurant);
                    //se o metodo para verificar se esta no fluxo de monitoramento, retornar true, entra nessa condição
                    } else if (isInMonitoringFlow()) {
                        //encaminha para o metodo
                        return showLocationsForRestaurant(selectedRestaurant);
                    //se nao, retorna uma mensagem alternativa
                    } else {
                        return new MessageModel("Algo não funcionou como esperado. Que tal selecionar uma das opções disponíveis nos botões?", "bot", getMainOptions());
                    }
                } else {
                    //verifica se o input é uma localização de fila
                    FilaModel selectedLocation = filaRepository.findByLocalizacao(userInput);
                    //se a variavel for diferente de vazia, entra nessa condição
                    if (selectedLocation != null) {
                        //encaminha para o metodo
                        return showMoveForLine(selectedLocation);
                    //se não, encaminha uma mensagem alternativa
                    } else {
                        return new MessageModel("Algo não funcionou como esperado. Que tal selecionar uma das opções disponíveis nos botões?", "bot", getMainOptions());
                    }
                }
        }
    }

    //Método que mostra os restaurantes para selecionar o cardápio
    private MessageModel showRestaurantsForMenu() {
        //acha os restaurantes pelo nome e guarda em uma variável
        List<String> restaurantNames = restauranteRepository.findAll().stream()
                .map(RestauranteModel::getNome)
                .collect(Collectors.toList());

        // Define que o usuário está no fluxo de "Cardápio"
        setMenuFlow();

        //retorna uma mensagem que mostra as opções de restaurante
        return new MessageModel("Hora de decidir o almoço! Toque em um restaurante para ver o cardápio do dia: ", "bot", restaurantNames);
    }

    //Método que mostra o cardápio baseado no restaurante escolhido
    private MessageModel showMenuForRestaurant(RestauranteModel selectedRestaurant) {
        //acha o cardapio através do resturante escolhido
        List<CardapioModel> menuDay = cardapioRepository.findByRestauranteModel(selectedRestaurant);
        //se a variavel que armazena o cardapio do dia for diferente de nulo, entra nessa condição
        if (!menuDay.isEmpty()) {
            //guarda a data atual do sistema
            LocalDate today = LocalDate.now();

            //variável criada para verificar se bate com o dia que está cadastrado
            boolean foundTodayMenu = false;

            //variável criada para armazenar o cardápio do dia (concatena strings)
            StringBuilder menuString = new StringBuilder();

            //laço de repetição para percorrer as informações do cardápio
            for (CardapioModel item : menuDay) {
                //variável criada para guardar a data que consta no banco referente ao cardápio encontrado
                LocalDate itemDate = LocalDate.parse(item.getData().toString());

                //se a data do banco, for igual a data do sistema, entra nessa condição
                if (itemDate.equals(today)) {
                    //achou o menu do dia
                    foundTodayMenu = true;
                    //adiciona na variável as informações de cada coluna do banco referente ao cardápio
                    menuString.append("Prato Principal: ").append(item.getPrato_principal())
                            .append(", Saguão: ").append(item.getGuarnicao())
                            .append(", Sobremesa: ").append(item.getSobremesa())
                            .append(", Salada: ").append(item.getSalada()).append("\n");
                }
            }

            //se a variável do menu do dia for verdadeira, entra nessa condição e mostra o menu do dia
            if (foundTodayMenu) {
                return new MessageModel("Sabores do dia - Confira nosso cardápio: " + menuString, "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
            } else {
                //se não, manda uma mensagem alternativa
                return new MessageModel("Estamos finalizando o cardápio de hoje. Volte em alguns minutos para as novidades!", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
            }
        } else {
            //se a variável que armazena o cardápio for fazia, mostra essa mensagem alternativa
            return new MessageModel("Estamos finalizando o cardápio de hoje. Volte em alguns minutos para as novidades!", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        }
    }

    //Método que mostra os restaurantes para selecionar a localização de cada restaurante
    private MessageModel showRestaurantsForLocations() {
        //acha os restaurantes pelo nome e armazena numa variável
        List<String> restaurantNames = restauranteRepository.findAll().stream()
                .map(RestauranteModel::getNome)
                .collect(Collectors.toList());

        // Define que o usuário está no fluxo de "Monitoramento"
        setMonitoringFlow();

        //retorna uma mensagem que mostra as opções de restaurante
        return new MessageModel("Escolha um restaurante para ver as localizações das filas: ", "bot", restaurantNames);
    }

    //Método que mostra as localizações de cada restaurante (filtra pelo id do restaurante, e mostra cada localização que existe para aquele id)
    private MessageModel showLocationsForRestaurant(RestauranteModel selectedRestaurant) {
        //adiciona na variável as localizações encontradas, baseadas no id do restaurante
        List<String> linesLocal = filaRepository.findByRestauranteIdModel(selectedRestaurant).stream()
                .map(FilaModel::getLocalizacao)
                .collect(Collectors.toList());

        //se a variável que armazena as localizações estiver vazia, manda uma mensagem alternativa
        if (linesLocal.isEmpty()) {
            return new MessageModel("Não temos dados sobre filas para este restaurante no momento. Posso te ajudar com mais alguma coisa?", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        } else {
            //se não, envia para o usuário as localizações encontradas
            return new MessageModel("Vamos lá! Qual fila você quer visualizar?", "bot", linesLocal);
        }
    }

    private MessageModel showMoveForLine(FilaModel filaModel) {
        // Cria a variável para alocar as informações do banco
        List<MonitoramentoModel> monitoringLine = monitoramentoRepository.findByFilaModel(filaModel);

        // Se a lista não estiver vazia entra nessa condição
        if (!monitoringLine.isEmpty()) {
            //guarda a data atual do sistema
            LocalDate today = LocalDate.now();

            //variável criada para verificar se bate com o dia que está cadastrado
            boolean foundTodayLine = false;

            //variável criada para armazenar a situação da fila
            String moveLine = "";

            //pega o último registro do banco
            MonitoramentoModel latestEntry = monitoringLine.get(0);

            //pega a data registrada no banco do último registro
            LocalDate itemDate = LocalDate.parse(latestEntry.getDataRegistro().toString());

            //se a data do banco for igual a data do sistema, entra nessa condição
            if(itemDate.equals(today)){
                //muda o valor da variável
                foundTodayLine = true;
                //adiciona na variável as informações de cada coluna do banco referente a fila
                if(latestEntry.getSituacao().equals("VERMELHO")){
                    moveLine = "Atenção! A situação da fila se encontra em: " + latestEntry.getSituacao() + ". Alta movimentação de pessoas no restaurante!";
                }else if(latestEntry.getSituacao().equals("AMARELO")){
                    moveLine = "Aviso: A situação da fila se encontra em: " + latestEntry.getSituacao() + ". Há um movimento moderado no restaurante.";
                }else {
                    moveLine = "Boa notícia! A situação da fila se encontra em:  " + latestEntry.getSituacao() + ". Aproveite, o movimento está tranquilo!";
                }
            }

            if(foundTodayLine){
                //retorna a mensagem com as informações da fila
                return new MessageModel(moveLine, "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
            }else{
                //se a variável estiver vazia, manda uma mensagem alternativa
                return new MessageModel("Ainda não há registros de movimentação para esta fila hoje.", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
            }
        } else {
            //se a variável estiver vazia, manda uma mensagem alternativa
            return new MessageModel("Ainda não há registros de movimentação para esta fila.", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        }
    }

    // Função para determinar a fila com a maior situação vermelha
    private MessageModel showBigLine() {
        //pega a data do sistema
        LocalDate today = LocalDate.now();

        //filtra os monitoramentos pela data do sistema
        List<MonitoramentoModel> monitoramentosHoje = monitoramentoRepository.findAll().stream()
                .filter(m -> m.getDataRegistro().equals(today))
                .toList();

        //pega os monitoramentos do dia e filtra com base na opção "vermelha"
        MonitoramentoModel maiorFila = monitoramentosHoje.stream()
                .filter(m -> "VERMELHO".equalsIgnoreCase(m.getSituacao().getLabel()))
                .findFirst()
                .orElse(null);

        //se a variável for diferente de nula, entra nessa condição
        if (maiorFila != null) {
            //coloca a mensagem dentro de uma variável junto com os dados do banco
            String maiorFilaMsg = "A maior fila é: "
                    + maiorFila.getFilaModel().getLocalizacao() + ".";

            //retorna uma mensagem com a maior fila
            return new MessageModel(maiorFilaMsg, "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        } else {
            //se a variável for nula, entra nessa condição
            return new MessageModel("Ainda não há registros de filas cheias para esta fila hoje.", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        }
    }

    // Função para determinar a fila com a menor situação verde
    private MessageModel showSmallLine() {
        //pega a data do sistema
        LocalDate today = LocalDate.now();

        //filtra os monitoramentos pela data do sistema
        List<MonitoramentoModel> monitoramentosHoje = monitoramentoRepository.findAll().stream()
                .filter(m -> m.getDataRegistro().equals(today))
                .toList();

        //pega os monitoramentos do dia e filtra com base na opção "verde"
        MonitoramentoModel menorFila = monitoramentosHoje.stream()
                .filter(m -> "VERDE".equalsIgnoreCase(m.getSituacao().getLabel()))
                .findFirst()
                .orElse(null);

        //se a variável for diferente de nula, entra nessa condição
        if (menorFila != null) {
            //coloca a mensagem dentro de uma variável junto com os dados do banco
            String menorFilaMsg = "A menor fila é: "
                    + menorFila.getFilaModel().getLocalizacao() + ".";

            //retorna uma mensagem com o menor da fila
            return new MessageModel(menorFilaMsg, "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        } else {
            //se não, manda uma mensagem alternativa
            return new MessageModel("Ainda não há registros de filas vazias para esta fila hoje.", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        }
    }

    // Método que manda uma mensagem e redireciona para a página do gráfico de movimentação
    private MessageModel showMoveforHours(){
        return new MessageModel("Toque no botão abaixo para conferir o gráfico de movimentação por horário:", "bot", Arrays.asList("Gráfico de Movimentação", "Voltar para o início", "Encerrar o chat"));
    }

    // Método que busca o horário de funcionamento do restaurante
    private MessageModel showOperatingHours() { 
        //cria uma variavel para armazenar os dados encontrados no banco
        InfoRestauranteModel infos = infoRestauranteRepository.findInfos();

        //se a variavel nao estiver vazia, entra nessa condição
        if (infos != null) {
            //adiciona as informações a uma variavel
            String infoMsg = "Horário de Funcionamento: " + infos.getHorarioFuncionamento();

            //retorna uma mensagem com as informações
            return new MessageModel(infoMsg, "bot", List.of("Voltar para o início"));
        } else {
            //se a variavel estiver vazia entra nessa condição, mandando uma mensagem alternativa
            return new MessageModel("Informações sobre o restaurante não disponíveis.", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        }
    }

    // Método que busca as refeições oferecidas do restaurante
    private MessageModel showMealsProvided() {
        //cria uma variavel para armazenar os dados encontrados no banco
        InfoRestauranteModel infos = infoRestauranteRepository.findInfos();

        //se a variavel nao estiver vazia, entra nessa condição
        if (infos != null) {
            //adiciona as informações a uma variavel
            String infoMsg = "Refeições Oferecidas: " + infos.getRefeicoesOferecidas();

            //retorna uma mensagem com as informações
            return new MessageModel(infoMsg, "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        } else {
            //se a variavel estiver vazia entra nessa condição, mandando uma mensagem alternativa
            return new MessageModel("Informações sobre o restaurante não disponíveis.", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        }
    }

    // Método que busca a localização do restaurante
    private MessageModel showLocal() {
        //cria uma variavel para armazenar os dados encontrados no banco
        InfoRestauranteModel infos = infoRestauranteRepository.findInfos();

        //ser a variavel nao estiver vazia, entra nessa condição
        if (infos != null) {
            //adiciona as informações a uma variavel
            String infoMsg = "Localização: " + infos.getLocalizacao();

            //retorna uma mensagem com as informações
            return new MessageModel(infoMsg, "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        } else {
            //se a variavel estiver vazia entra nessa condição, mandando uma mensagem alternativa
            return new MessageModel("Informações sobre o restaurante não disponíveis.", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
        }
    }

    // Método que busca o queridinho do dia
    private MessageModel showVotes() {
        //pega a data do sistema
        LocalDate today = LocalDate.now();

        //busca os votos referentes ao dia atual
        List<Object[]> topVotedResults = voteRepository.findTopVotedRestaurante(today);

        //se a lista nao for vazia, entra nessa condição
        if (!topVotedResults.isEmpty()) {
            //pega o id do restaurante com maior frequência no banco, o mais votado
            UUID topRestauranteId = (UUID) topVotedResults.get(0)[0];
            //acha o restaurante de acordo com o id selecionado
            RestauranteModel topRestaurante = restauranteRepository.findById(topRestauranteId).orElse(null);

            //se a variavel for diferente de vazio, entra nessa condição
            if (topRestaurante != null) {
                //adiciona uma mensagem e os dados correspondentes do nome do restaurante a uma variavel
                String message = "O mais prato mais amado de hoje é: " + topRestaurante.getNome();

                //retorna uma mensagem com a variavel que armazena as informações
                return new MessageModel(message, "bot", Arrays.asList("Veja mais", "Voltar para o início", "Encerrar o chat"));
            }
        }
        //caso não existam votos para aquele dia e/ou dados insuficientes, manda essa mensagem alternativa
        return new MessageModel("Ainda não temos votos suficientes para escolher o queridinho do dia.", "bot", Arrays.asList("Veja Mais", "Voltar para o início", "Encerrar o chat"));
    }

    // Método que mostra as informações sobre o chatbot
    private MessageModel messageAboutChatBot() {
        return new MessageModel("O chatbot está aqui para facilitar sua vida, juntando todas as informações importantes sobre o restaurante da Bosch em um só lugar.", "bot", Arrays.asList("Voltar para o início", "Encerrar o chat"));
    }

    // Método que finaliza o chatbot e mostra a enquete para o usuário avaliar a experiência
    private MessageModel finishChat(){
        return new MessageModel("Avalie a sua experiência com o nosso chatbot: ", "bot", Arrays.asList("Ótimo", "Ruim", "Voltar para o início"));
    }

    // Método que armazena a avaliação "Ótimo" e a data local no banco
    private MessageModel avaliationGreat() {
        AvaliacaoChatModel avaliacao = new AvaliacaoChatModel(UUID.randomUUID(), "Ótimo");
        avaliacaoChatRepository.save(avaliacao);
        return new MessageModel("Agradecemos pela sua avaliação! Volte sempre que precisar!", "bot", List.of());
    }

    // Método que armazena a avaliação "Ruim" e a data local no banco
    private MessageModel avaliationBad() {
        AvaliacaoChatModel avaliacao = new AvaliacaoChatModel(UUID.randomUUID(), "Ruim");
        avaliacaoChatRepository.save(avaliacao);
        return new MessageModel("Lamentamos que sua experiência não tenha sido satisfatória. Vamos trabalhar para melhorar!", "bot", List.of());
    }

    //Método que quando chamado define um valor para a variável
    private void setMenuFlow() {
        currentFlow = "Cardápio";
    }

    //Método que quando chamado faz a comparação
    private boolean isInMenuFlow() {
        return "Cardápio".equals(currentFlow);
    }

    //Método que quando chamado define um valor para a variável
    private void setMonitoringFlow() {
        currentFlow = "Monitoramento";
    }

    //Método que quando chamado faz a comparação
    private boolean isInMonitoringFlow() {
        return "Monitoramento".equals(currentFlow);
    }


    //Método para mostrar as opções (botões)
    private List<String> getMainOptions() {
        return Arrays.asList("Cardápio", "Monitoramento", "Queridinho do dia", "Informações", "Chatbot");
    }
}
