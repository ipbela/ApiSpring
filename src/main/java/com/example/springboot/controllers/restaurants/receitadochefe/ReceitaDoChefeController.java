package com.example.springboot.controllers.restaurants.receitadochefe;

import com.example.springboot.dtos.CardapioRecordDto;
import com.example.springboot.models.CardapioModel;
import com.example.springboot.models.RestauranteModel;
import com.example.springboot.repositories.CardapioRepository;
import com.example.springboot.repositories.RestauranteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    // Método POST que salva um novo cardápio
    @PostMapping("/cardapios")
    public ResponseEntity<Object> saveMenu(@RequestBody @Valid CardapioRecordDto cardapioRecordDto) {
        // Busca o restaurante pelo ID fornecido no DTO
        Optional<RestauranteModel> restauranteOpt = restauranteRepository.findById(cardapioRecordDto.fk_restaurante());

        // Se o restaurante não for encontrado, retorna BAD_REQUEST com uma mensagem clara
        if (restauranteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Restaurante não encontrado. Verifique o ID fornecido.");
        }

        var cardapioModel = new CardapioModel();
        // Faz a cópia de propriedades de DTO para Model
        BeanUtils.copyProperties(cardapioRecordDto, cardapioModel);

        // Associa o restaurante encontrado ao cardápio que está sendo salvo
        cardapioModel.setRestauranteModel(restauranteOpt.get());

        // Salva o cardápio no repositório e retorna as informações no corpo da resposta
        return ResponseEntity.status(HttpStatus.CREATED).body(cardapioRepository.save(cardapioModel));
    }

    // Método GET para buscar todos os cardápios do restaurante "Moda da Casa" dentro da semana atual
    @GetMapping("/cardapios")
    public ResponseEntity<Object> getAllMenusForModaDaCasaThisWeek() {
        // Busca o restaurante "Moda da Casa" pelo nome
        RestauranteModel restauranteOpt = restauranteRepository.findByNome("Receita do Chefe");

        if (restauranteOpt.equals("")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurante 'Receita do Chefe' não encontrado.");
        }

        RestauranteModel restaurante = restauranteOpt;

        // Define o intervalo de 7 dias a partir da data atual
        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(6);

        // Busca cardápios apenas para o restaurante "Moda da Casa" e filtra pelo intervalo de datas
        List<CardapioModel> menus = cardapioRepository.findByRestauranteModel(restaurante).stream()
                .filter(menu -> !menu.getData().isBefore(today) && !menu.getData().isAfter(endOfWeek))
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
