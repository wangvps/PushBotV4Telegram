package com.ioreo;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.ioreo.Bots.PushBot;

public class BotApi {
    public static void main(String[] args) throws Exception {
        System.out.print("Bot Started");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new PushBot());
    }
}
