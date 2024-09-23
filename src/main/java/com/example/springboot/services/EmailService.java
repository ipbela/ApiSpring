package com.example.springboot.services;

import com.example.springboot.models.EmailModel;
import com.example.springboot.models.CardapioModel;
import com.example.springboot.repositories.CardapioRepository;
import com.example.springboot.repositories.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private CardapioRepository cardapioRepository;

    private final JavaMailSender mailSender;

    //construtor
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //Método para envio de emails
    public void sendEmailsToAllActive() {
        //acha no banco os emails que estão ativos e guarda em uma variável
        List<EmailModel> activeEmails = emailRepository.findAll();

        //se a variável for diferente de vazia entra nessa condição
        if (activeEmails != null && !activeEmails.isEmpty()) {
            //guarda a data atual em uma variável
            LocalDate today = LocalDate.now();

            //guarda os dias da semana em uma variável
            LocalDate endOfWeek = today.plusDays(6);

            //busca todos os cardápios dentro do intervalo de 7 dias
            List<CardapioModel> menus = cardapioRepository.findAll().stream()
                    .filter(menu -> !menu.getData().isBefore(today) && !menu.getData().isAfter(endOfWeek))
                    .collect(Collectors.toList());

            //se a variavel for diferente de vazia, entra nessa opção
            if (!menus.isEmpty()) {
                //formata o cardápio em HTML para enviar por e-mail, percorrendo todos os cardápios que estão no banco
                for (EmailModel emailModel : activeEmails) {
                    //tenta formatar a mensagem que será enviada ao usuário
                    try {
                        String menuHtml = formatMenuHtml(menus, emailModel.getNome());
                        String subject = "Cardápio da Semana: " + today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                + " - " + endOfWeek.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); //assunto do email
                        sendHtmlEmail(emailModel.getEmail(), subject, menuHtml); //função que formata o email
                        System.out.println("Email enviado para: " + emailModel.getEmail()); //printa para quais emails foram enviados
                    } catch (Exception e) {
                        System.out.println("Erro ao enviar e-mail para: " + emailModel.getEmail()); //caso de erro, mostra os emails que não foram enviados
                    }
                }
            } else {
                System.out.println("Nenhum cardápio encontrado para a semana atual.");
            }
        } else {
            System.out.println("Não foi possível enviar os emails no momento. Tente novamente mais tarde!");
        }
    }

    //Método para formatar o envio de email
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        //tenta enviar o email formatado passando as informações necessárias (remetente, destinatario, assunto e mensagem)
        try {
            //objeto criado para criar um email em formato HTML
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setFrom("isabela.leite@senaisp.edu.br"); //remetente
            helper.setTo(to); //destinatário
            helper.setSubject(subject); //assunto
            helper.setText(htmlContent, true);  // 'true' indica que o conteúdo é HTML, mensagem
            mailSender.send(mimeMessage); //envia a mensagem
        } catch (MessagingException e) {
            //erro ao enviar
            System.out.println("Erro ao enviar e-mail para: " + to);
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Método que formata a mensagem em HTML
    private String formatMenuHtml(List<CardapioModel> menus, String personName) {
        StringBuilder html = new StringBuilder("<div style='font-family: Verdana, sans-serif;'>"); //define a variavel que irá conter as informações
        html.append("<h2 style='color: #34495E;'>Olá ").append(personName).append(", confira o cardápio da semana! </h2>"); //título da mensagem

        //agrupa os cardápios por data e ordena as datas em ordem crescente
        Map<LocalDate, List<CardapioModel>> menusByDate = new TreeMap<>(menus.stream()
                .collect(Collectors.groupingBy(CardapioModel::getData)));

        //mapeia pela data dos menus
        menusByDate.forEach((date, dailyMenus) -> {
            html.append("<div style='margin-bottom: 20px;'>");
            //formata os dias conforme cadastrado no banco
            html.append("<h3 style='color: #34495E;'>Dia ").append(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</h3>");

            //agrupa os cardápios do dia por restaurante
            Map<String, List<CardapioModel>> menusByRestaurante = dailyMenus.stream()
                    .collect(Collectors.groupingBy(menu -> menu.getRestauranteModel().getNome()));

            //função para registrar o cardápio do dia, adicionando as informações na variável html.
            //condição para decidir a cor do título conforme o restaurante selecionado
            menusByRestaurante.forEach((restaurantName, restaurantMenus) -> {
                if (restaurantName.equalsIgnoreCase("Moda da Casa")){
                    html.append("<p><strong style='color: #007bc0;'>").append(restaurantName).append("</strong>: <br>");
                    restaurantMenus.forEach(menu -> html.append("<span style='color: #34495E;'>").append(menu.getPrato_principal()).append("<br>").append(menu.getGuarnicao()).append("<br>").append(menu.getSalada()).append("<br>").append(menu.getSobremesa()).append("</span>, "));
                    html.setLength(html.length() - 2);
                    html.append("</p>");
                }else if(restaurantName.equalsIgnoreCase("De Bem com a Vida")){
                    html.append("<p><strong style='color: #00884a;'>").append(restaurantName).append("</strong>: <br>");
                    restaurantMenus.forEach(menu -> html.append("<span style='color: #34495E;'>").append(menu.getPrato_principal()).append("<br>").append(menu.getGuarnicao()).append("<br>").append(menu.getSalada()).append("<br>").append(menu.getSobremesa()).append("</span>, "));
                    html.setLength(html.length() - 2);
                    html.append("</p>");
                }else if(restaurantName.equalsIgnoreCase("Receita do Chefe")){
                    html.append("<p><strong style='color: #18837e;'>").append(restaurantName).append("</strong>: <br>");
                    restaurantMenus.forEach(menu -> html.append("<span style='color: #34495E;'>").append(menu.getPrato_principal()).append("<br>").append(menu.getGuarnicao()).append("<br>").append(menu.getSalada()).append("<br>").append(menu.getSobremesa()).append("</span>, "));
                    html.setLength(html.length() - 2);
                    html.append("</p>");
                }else if(restaurantName.equalsIgnoreCase("Grill e Bem-Estar")){
                    html.append("<p><strong style='color: #9e2896;'>").append(restaurantName).append("</strong>: <br>");
                    restaurantMenus.forEach(menu -> html.append("<span style='color: #34495E;'>").append(menu.getPrato_principal()).append("<br>").append(menu.getGuarnicao()).append("<br>").append(menu.getSalada()).append("<br>").append(menu.getSobremesa()).append("</span>, "));
                    html.setLength(html.length() - 2);
                    html.append("</p>");
                }
            });

            html.append("</div>");

        });

        html.append("</div>");
        return html.toString(); //retorna o texto concatenado convertendo para String
    }
}
