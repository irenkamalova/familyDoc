package com.kamalova.familydoctor;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class PingJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            FamilyDoctorBot bot = (FamilyDoctorBot) jobExecutionContext
                    .getJobDetail().getJobDataMap().get("FamilyDoctorBot");
            String message = "ping";
            for (String chatId : bot.getRecipientList()) {
                bot.execute(new SendMessage()
                        .setChatId(chatId)
                        .setText(message));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
