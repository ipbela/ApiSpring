package com.example.springboot.controllers.restaurants.debemcomavida;

import com.example.springboot.dtos.CardapioRecordDto;
import com.example.springboot.models.CardapioModel;
import com.example.springboot.models.RestauranteModel;
import com.example.springboot.repositories.CardapioRepository;
import com.example.springboot.repositories.RestauranteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/debemcomavida")
public class DeBemComAVidaController {

    @Autowired
    CardapioRepository cardapioRepository;

    @Autowired
    RestauranteRepository restauranteRepository;

    // Método POST que salva um novo cardápio
    @PostMapping("/cardapios")
    public ResponseEntity<Object> saveMenu(@RequestBody @Valid @DateTimeFormat(pattern = "dd/MM/yyyy") CardapioRecordDto cardapioRecordDto) {
        // Busca o restaurante pelo ID fornecido no DTO
        Optional<RestauranteModel> restauranteOpt = restauranteRepository.findById(cardapioRecordDto.fk_restaurante());

        // Se o restaurante não for encontrado, retorna BAD_REQUEST com uma mensagem clara
        if (restauranteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Restaurante não encontrado. Verifique o ID fornecido.");
        }

        // Verifica se já existe um cardápio cadastrado para o mesmo restaurante e data
        Optional<CardapioModel> existingMenu = cardapioRepository.findByRestauranteModelAndData(restauranteOpt.get(), cardapioRecordDto.data());
        if (existingMenu.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um cardápio cadastrado com essa data para o restaurante 'De Bem com a Vida'.");
        }

        var cardapioModel = new CardapioModel();

        // Faz a cópia de propriedades de DTO para Model
        BeanUtils.copyProperties(cardapioRecordDto, cardapioModel);

        // Associa o restaurante encontrado ao cardápio que está sendo salvo
        cardapioModel.setRestauranteModel(restauranteOpt.get());

        // Salva o cardápio no repositório e retorna as informações no corpo da resposta
        return ResponseEntity.status(HttpStatus.CREATED).body(cardapioRepository.save(cardapioModel));
    }

    // Método GET para buscar todos os cardápios do restaurante "De Bem com a Vida" dentro da semana atual
    @GetMapping("/cardapios")
    public ResponseEntity<Object> getAllMenusThisWeek() {
        // Busca o restaurante "De Bem com a Vida" pelo nome
        RestauranteModel restauranteOpt = restauranteRepository.findByNome("De Bem com a Vida");

        if (restauranteOpt == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurante 'De Bem com a Vida' não encontrado.");
        }

        RestauranteModel restaurante = restauranteOpt;

        // Pega a data de hoje
        LocalDate today = LocalDate.now();

        // Calcula a segunda-feira da semana atual
        LocalDate mondayOfThisWeek = today.with(java.time.DayOfWeek.MONDAY);

        // Calcula o domingo da semana atual (6 dias após a segunda-feira)
        LocalDate sundayOfThisWeek = mondayOfThisWeek.plusDays(6);

        // Busca cardápios apenas para o restaurante "De Bem com a Vida" e filtra pelo intervalo de datas (segunda a domingo)
        List<CardapioModel> menus = cardapioRepository.findByRestauranteModel(restaurante).stream()
                .filter(menu -> !menu.getData().isBefore(mondayOfThisWeek) && !menu.getData().isAfter(sundayOfThisWeek))
                .toList();

        if (menus.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum cardápio encontrado para esta semana.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(menus);
    }

    // Método GET para buscar um cardápio específico pelo ID
    @GetMapping("/cardapios/{id_cardapio}")
    public ResponseEntity<Object> getIndividualMenu(@PathVariable(value = "id_cardapio") UUID id_cardapio) {
        Optional<CardapioModel> menu_ = cardapioRepository.findById(id_cardapio);
        if (menu_.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cardápio não encontrado.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(menu_.get());
    }

    // Método PUT para atualizar um cardápio específico
    @PutMapping("/cardapios/{id_cardapio}")
    public ResponseEntity<Object> updateMenu(@PathVariable(value = "id_cardapio") UUID id_cardapio,
                                             @RequestBody @Valid CardapioRecordDto cardapioRecordDto) {
        Optional<CardapioModel> menu_ = cardapioRepository.findById(id_cardapio);
        if (menu_.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cardápio não encontrado.");
        }

        // Busca o restaurante pelo ID fornecido no DTO
        Optional<RestauranteModel> restauranteOpt = restauranteRepository.findById(cardapioRecordDto.fk_restaurante());
        if (restauranteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Restaurante não encontrado. Verifique o ID fornecido.");
        }

        var cardapioModel = menu_.get();

        // Atualiza as propriedades do cardápio
        BeanUtils.copyProperties(cardapioRecordDto, cardapioModel);

        // Associa o restaurante encontrado ao cardápio que está sendo atualizado
        cardapioModel.setRestauranteModel(restauranteOpt.get());

        // Salva o cardápio atualizado no repositório
        return ResponseEntity.status(HttpStatus.OK).body(cardapioRepository.save(cardapioModel));
    }
}
