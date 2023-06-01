package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.entry.HH;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FreelanceTgBot {
    TelegramBot bot;
    FreelanceTgBot(){
        this.bot = new TelegramBot("6202309317:AAG7xsBZVwdO5GMwIwJdMIp9gs_DBCRqjHk");
    }
    public void Listen() {
        this.bot.setUpdatesListener(element -> {
            System.out.println(element);
            element.forEach(it -> {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.hh.ru/vacancies?text=" + it.message().text() + "&area=3"))
                        .build();
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    String body = response.body();
                    System.out.println(body);
                    HH hh = mapper.readValue(body, HH.class);
                    hh.getItems().subList(0, 3).forEach(job -> {
                        bot.execute(new SendMessage(it.message().chat().id(), "Вакансия:" + job.getName() + "\nСсылка: http://hh.ru/vacancy/" + job.getId()));
                        System.out.println(job.getId() + " " + job.getName());
                    });
                    response.body();
                } catch (IOException | InterruptedException e) {
                    System.out.println(e.getMessage());
                }

            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
