package com.example.springboot.controllers.restaurants.receitadochefe;

import com.example.springboot.dtos.CardapioIdDataDto;
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

    // Método POST que salva um novo cardápio
    @PostMapping("/cardapios")
    public ResponseEntity<Object> saveMenu(@RequestBody @Valid CardapioRecordDto cardapioRecordDto) {
        return saveOrUpdateMenu(cardapioRecordDto, null);
    }

    // Método PUT para atualizar um cardápio específico
    @PutMapping("/cardapios/{id_cardapio}")
    public ResponseEntity<Object> updateMenu(@PathVariable(value = "id_cardapio") UUID id_cardapio,
                                             @RequestBody @Valid CardapioRecordDto cardapioRecordDto) {
        return saveOrUpdateMenu(cardapioRecordDto, id_cardapio);
    }

    // Método para decidir se será um PUT ou um POST
    private ResponseEntity<Object> saveOrUpdateMenu(CardapioRecordDto cardapioRecordDto, UUID id_cardapio) {
        Optional<RestauranteModel> restauranteOpt = restauranteRepository.findById(cardapioRecordDto.fk_restaurante());

        if (restauranteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Restaurante não encontrado. Verifique o ID fornecido.");
        }

        RestauranteModel restaurante = restauranteOpt.get();
        CardapioModel cardapioModel;

        // Verifica se o ID do cardápio foi fornecido (caso seja PUT)
        if (id_cardapio != null) {
            Optional<CardapioModel> menu_ = cardapioRepository.findById(id_cardapio);
            if (menu_.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cardápio não encontrado.");
            }
            cardapioModel = menu_.get();
        } else {
            // Verifica se já existe um cardápio para a mesma data e restaurante
            Optional<CardapioModel> existingMenu = cardapioRepository.findByRestauranteModelAndData(restaurante, cardapioRecordDto.data());
            if (existingMenu.isPresent()) {
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

    // Método GET para buscar todos os cardápios do restaurante "Receita do Chefe" dentro da semana atual
    @GetMapping("/cardapios")
    public ResponseEntity<Object> getAllMenusThisWeek() {
        RestauranteModel restauranteOpt = restauranteRepository.findByNome("Receita do Chefe");

        if (restauranteOpt == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurante 'Receita do Chefe' não encontrado.");
        }

        RestauranteModel restaurante = restauranteOpt;

        LocalDate today = LocalDate.now();
        LocalDate mondayOfThisWeek = today.with(DayOfWeek.MONDAY);
        LocalDate sundayOfThisWeek = mondayOfThisWeek.plusDays(6);

        List<CardapioModel> menus = cardapioRepository.findByRestauranteModel(restaurante).stream()
                .filter(menu -> isDateWithinThisWeek(menu.getData(), mondayOfThisWeek, sundayOfThisWeek))
                .toList();

        if (menus.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum cardápio encontrado para esta semana.");
        }

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

    // Método GET para buscar um cardápio específico pelo ID
    @GetMapping("/cardapios/{id_cardapio}")
    public ResponseEntity<Object> getIndividualMenu(@PathVariable(value = "id_cardapio") UUID id_cardapio) {
        Optional<CardapioModel> menu_ = cardapioRepository.findById(id_cardapio);
        if (menu_.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cardápio não encontrado.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(menu_.get());
    }
}
