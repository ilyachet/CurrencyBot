package com.example.telegrambot1.bot;

import com.example.telegrambot1.components.BotCommands;
import com.example.telegrambot1.config.BotConfig;
import com.example.telegrambot1.model.CurrencyModel;
import com.example.telegrambot1.repository.UserRepository;
import com.example.telegrambot1.service.CurrencyService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * TODO:
 * 0. Кнопки
 * 1. Конвертер
 * 2. Список кодов валют
 */
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot implements BotCommands {

    final BotConfig config;
    private final String CURRENCY_PATTERN = "[a-zA-Z]{3}";
    private final String DIGIT_PATTERN = "\\d{3}";
    private String currencyCode;

    public TelegramBot(BotConfig config) {
        this.config = config;
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        long chatId = 0;
        long userId = 0;
        String userName = null;
        String receivedMessage;

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            userId = update.getMessage().getFrom().getId();
            userName = update.getMessage().getFrom().getUserName();

            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText();
                try {
                    botAnswerUtils(receivedMessage, chatId, userName);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            receivedMessage = update.getCallbackQuery().getData();

            try {
                botAnswerUtils(receivedMessage, chatId, userName);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }


    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName) throws IOException {
        switch (receivedMessage) {
            case "/start" :
                startBot(chatId, userName);
                break;
            case "/help" :
                sendHelpText(chatId);
                break;
            default:
                if (receivedMessage.matches(CURRENCY_PATTERN)) {
                    this.currencyCode = receivedMessage.toUpperCase();
                    sendCurrency(chatId, userName, this.currencyCode);
                } else if (receivedMessage.matches(DIGIT_PATTERN)) {
                    if (Objects.equals(this.currencyCode, "")) {
                        sendErrorMessage(chatId);
                    } else {
                        convertCurrency(chatId, this.currencyCode, Double.parseDouble(receivedMessage));
                    }
                }
        }
    }

    private void sendHelpText(long chatId) throws IOException {
        SendMessage message = new SendMessage();
        StringBuilder sb = new StringBuilder();
        for (CurrencyModel c : CurrencyService.getCurrencyNames()) {
            sb.append(c.getCur_Abbreviation())
                    .append(" - ")
                    .append(c.getCur_name())
                    .append("\n");
        }
        message.setChatId(chatId);
        message.setText(sb.toString());

        try {
            execute(message);
            log.info("Help sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendCurrency(long chatId, String userName, String text) throws IOException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Сегодняшний курс для валюты \"" + text + "\" - " + CurrencyService.getCurrencyRate(text));

        try {
            execute(message);
            log.info("Message \"" + text + "\" sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String answer = "Hi, " + userName + ", nice to meet you!" + "\n" +
                "Enter the currency whose official exchange rate" + "\n" +
                "you want to know in relation to RUB." + "\n" +
                "For example: USD";
        message.setText(answer);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void convertCurrency(long chatID, String currency, Double amount) throws IOException {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(String.valueOf(amount * CurrencyService.getCurrencyRate(currency)));

        try {
            execute(message);
            log.info("Currency converted");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendErrorMessage(long chatID) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText("Сначала нужно выбрать валюту");

        try {
            execute(message);
            log.info("Error message sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    public String getBotToken() {
        return config.getToken();

    }
}
