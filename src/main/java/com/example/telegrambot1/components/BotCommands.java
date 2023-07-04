package com.example.telegrambot1.components;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "start_bot"),
            new BotCommand("/help", "bot info")
    );

//    String HELP_TEXT = "Этот поможет вам конвертировать одну валюту в другую. " +
//            "Вам доступны следующие команды:\n\n" +
//            "/start - запуск бота\n" +
//            "/help - помощь";
}
