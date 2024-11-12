package com.example.springboot.controllers.restaurants.receitadochefe;

import com.example.springboot.dtos.CardapioIdDataDto;
import com.example.springboot.dtos.CardapioRecordDto;
import com.example.springboot.models.CardapioModel;
import com.example.springboot.models.RestauranteModel;
import com.example.springboot.repositories.CardapioRepository;
import com.example.springboot.repositories.RestauranteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/receitadochefe")
public class ReceitaDoChefeController {

    @Autowired
    CardapioRepository cardapioRepository;

    @Autowired
    RestauranteRepository restauranteRepository;

    @Operation(description = "Cadastra um novo cardápio para o restaurante Receita do Chefe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cardápio cadastrado com sucesso!"),
            @ApiResponse(responseCode = "409", description = "Já existe um cardápio cadastrado para essa data."),
            @ApiResponse(responseCode = "400", description = "Não foi possível cadastrar o cardápio no momento, tente novamente mais tarde!")
    })
    @PostMapping("/cardapios")
    public ResponseEntity<Object> saveMenu(@RequestBody @Valid CardapioRecordDto cardapioRecordDto) {
        return saveOrUpdateMenu(cardapioRecordDto, null);
    }

    @Operation(description = "Atualiza um cardápio para o restaurante Receita do Chefe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cardápio atualizado com sucesso!"),
            @ApiResponse(responseCode = "409", description = "Já existe um cardápio cadastrado para essa data."),
            @ApiResponse(responseCode = "404", description = "Não foi possível localizar o cardápio, verifique se o id está correto."),
            @ApiResponse(responseCode = "400", description = "Não foi possível atualizar o cardápio no momento, tente novamente mais tarde!")
    })
    @PutMapping("/cardapios/{id_cardapio}")
    public ResponseEntity<Object> updateMenu(@PathVariable(value = "id_cardapio") UUID id_cardapio,
                                             @RequestBody @Valid CardapioRecordDto cardapioRecordDto) {
        return saveOrUpdateMenu(cardapioRecordDto, id_cardapio);
    }

    // Método para decidir se será um PUT ou um POST
    private ResponseEntity<Object> saveOrUpdateMenu(CardapioRecordDto cardapioRecordDto, UUID id_cardapio) {
        // acha o restaurante pelo id
        Optional<RestauranteModel> restauranteOpt = restauranteRepository.findById(cardapioRecordDto.fk_restaurante());

        // verifica se a variavel acima nao esta vazia e gera uma mensagem alternativa
        if (restauranteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Restaurante não encontrado. Verifique o ID fornecido.");
        }

        RestauranteModel restaurante = restauranteOpt.get();
        CardapioModel cardapioModel;

        // Verifica se o ID do cardápio foi fornecido (caso seja o metodo PUT)
        if (id_cardapio != null) {
            Optional<CardapioModel> menu_ = cardapioRepository.findById(id_cardapio);
            if (menu_.isEmpty()) {
                //caso a variavel esteja vazia, manda uma mensagem alternativa
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cardápio não encontrado.");
            }
            cardapioModel = menu_.get();
        } else {
            // Verifica se já existe um cardápio para a mesma data e restaurante
            Optional<CardapioModel> existingMenu = cardapioRepository.findByRestauranteModelAndData(restaurante, cardapioRecordDto.data());
            if (existingMenu.isPresent()) {
                // se ja existir, manda uma mensagem alternativa
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um cardápio cadastrado com essa data para o restaurante 'Receita do Chefe'.");
            }
            // Cria um novo cardápio (caso seja POST)
            cardapioModel = new CardapioModel();
        }

        // Atualiza as propriedades do cardápio
        BeanUtils.copyProperties(cardapioRecordDto, cardapioModel);
        cardapioModel.setRestauranteModel(restaurante);

        // Realiza o PUT ou POST baseado na existência do ID do cardápio
        HttpStatus status = (id_cardapio != null) ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(cardapioRepository.save(cardapioModel));
    }


    // Método auxiliar para verificar se o cardápio contém todas as informações completas
    private boolean hasFullInformation(CardapioModel cardapioModel) {
        return (cardapioModel.getGuarnicao() != null && !cardapioModel.getGuarnicao().isEmpty()) &&
                (cardapioModel.getPrato_principal() != null && !cardapioModel.getPrato_principal().isEmpty()) &&
                (cardapioModel.getSalada() != null && !cardapioModel.getSalada().isEmpty()) &&
                (cardapioModel.getSobremesa() != null && !cardapioModel.getSobremesa().isEmpty());
    }

    @Operation(description = "Busca os cardápios do restaurante Receita do Chefe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna os cardápios encontrados."),
            @ApiResponse(responseCode = "400", description = "Não foi possível buscar os cardápios, tente novamente mais tarde!")
    })
    @GetMapping("/cardapios")
    public ResponseEntity<Object> getAllMenusThisWeek() {
        // pega o restaurante especifico
        RestauranteModel restauranteOpt = restauranteRepository.findByNome("Receita do Chefe");

        // caso nao tenha achado manda uma mensagem alternativa
        if (restauranteOpt == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurante 'Receita do Chefe' não encontrado.");
        }

        RestauranteModel restaurante = restauranteOpt;

        //pega a data atual
        LocalDate today = LocalDate.now();
        LocalDate mondayOfThisWeek = today.with(DayOfWeek.MONDAY); //pega a segunda feira diante do dia atual
        LocalDate sundayOfThisWeek = mondayOfThisWeek.plusDays(6); //soma mais 6 dias baseado na segunda, para completar a semana

        //pega os cardapios baseado no range da semana
        List<CardapioModel> menus = cardapioRepository.findByRestauranteModel(restaurante).stream()
                .filter(menu -> isDateWithinThisWeek(menu.getData(), mondayOfThisWeek, sundayOfThisWeek))
                .toList();

        // caso não tenha encontrado nenhum cardapio para a semana, manda uma mensagem alternativa
        if (menus.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum cardápio encontrado para esta semana.");
        }

        //se alguns valores estiverem nulo ele retorna somente o id do cardapio e a data, se não ele retorna os dados do menu inteiro
        List<Object> result = new ArrayList<>();
        for (CardapioModel menu : menus) {
            if ((menu.getGuarnicao() == null || menu.getGuarnicao().isEmpty()) &&
                    (menu.getPrato_principal() == null || menu.getPrato_principal().isEmpty()) &&
                    (menu.getSalada() == null || menu.getSalada().isEmpty()) &&
                    (menu.getSobremesa() == null || menu.getSobremesa().isEmpty())) {
                result.add(new CardapioIdDataDto(menu.getId_cardapio(), menu.getData()));
            } else {
                result.add(menu);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // Método para verificar se a data está dentro desta semana
    private boolean isDateWithinThisWeek(String dateStr, LocalDate mondayOfThisWeek, LocalDate sundayOfThisWeek) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            return !date.isBefore(mondayOfThisWeek) && !date.isAfter(sundayOfThisWeek);
        } catch (Exception e) {
            // Lida com a exceção caso a string não possa ser convertida para LocalDate
            return false;
        }
    }

    @Operation(description = "Busca um cardápio do restaurante Receita do Chefe pelo id fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o cardápio encontrado."),
            @ApiResponse(responseCode = "404", description = "Não foi possível localizar o cardápio, verifique se o id está correto."),
            @ApiResponse(responseCode = "400", description = "Não foi possível encontrar o cardápio no momento, tente novamente mais tarde!")
    })
    @GetMapping("/cardapios/{id_cardapio}")
    public ResponseEntity<Object> getIndividualMenu(@PathVariable(value = "id_cardapio") UUID id_cardapio) {
        Optional<CardapioModel> menu_ = cardapioRepository.findById(id_cardapio);
        if (menu_.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cardápio não encontrado.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(menu_.get());
    }
}
