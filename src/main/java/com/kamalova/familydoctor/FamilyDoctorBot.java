package com.kamalova.familydoctor;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.io.Files.append;
import static com.google.common.io.Files.readLines;

public class FamilyDoctorBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUserName;
    private final Set<String> recipientList = new HashSet<>();
    private final File chatIds = new File("idsBackUp.data");

    FamilyDoctorBot(String botToken, String botUserName) {
        this.botToken = botToken;
        this.botUserName = botUserName;
        initData();
    }

    private void initData() {
        try {
            if (!chatIds.exists()) {
                chatIds.createNewFile();
            } else {
                recipientList.addAll(readLines(chatIds, Charset.defaultCharset()));
            }
        } catch (IOException e) {
            System.out.println("Failed to init data");
            e.printStackTrace();
        }
    }

    private void putData(Long chatId) {
        String strChatId = String.valueOf(chatId);
        if (recipientList.add(strChatId)) {
            try {
                append(strChatId + "\n", chatIds, Charset.defaultCharset());
            } catch (IOException e) {
                System.out.println("Failed to append chatId " + strChatId);
                e.printStackTrace();
            }
        }
    }

    Set<String> getRecipientList() {
        return recipientList;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            processMessage(update.getMessage());
        }
    }

    private void processMessage(Message message) {
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        putData(chatId);
        sendMessage.setText("Chat id: " + chatId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
