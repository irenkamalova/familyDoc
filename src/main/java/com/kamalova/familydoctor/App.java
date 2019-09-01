package com.kamalova.familydoctor;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.Thread.sleep;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class App {

    public static void main(String[] args) {
        try (InputStream secretIn = App.class.getClassLoader()
                .getResourceAsStream("secrets.properties");
             InputStream generalIn = App.class.getClassLoader()
                     .getResourceAsStream("general.properties")) {

            Properties secretProperties = new Properties();
            secretProperties.load(secretIn);
            String botToken = secretProperties.getProperty("bot.token");

            Properties generalProperties = new Properties();
            generalProperties.load(generalIn);
            String botName = generalProperties.getProperty("bot.name");


            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();
            try {
                FamilyDoctorBot familyDoctorBot = new FamilyDoctorBot(botToken, botName);
                scheduleBot(familyDoctorBot);
                botsApi.registerBot(familyDoctorBot);
                System.out.println("Registered successfully");
            } catch (TelegramApiException | SchedulerException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Failed to read properties");
        }

    }

    private static void scheduleBot(FamilyDoctorBot bot) throws SchedulerException {
        Map<String, String> timeMessages = timeMessage();

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        scheduler.start();
        for (Map.Entry<String, String> entry : timeMessages.entrySet()) {
            JobDetail jobDetail = newJob(FDJob.class).build();
            CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule(entry.getKey());
            jobDetail.getJobDataMap().putIfAbsent("FamilyDoctorBot", bot);
            jobDetail.getJobDataMap().putIfAbsent("Message", entry.getValue());

            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(schedule)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        }

        JobDetail jobDetail = newJob(FDJob.class).build();
        CronScheduleBuilder schedule = CronScheduleBuilder.cronSchedule(" 0 0/25 0 ? * * *");
        jobDetail.getJobDataMap().putIfAbsent("FamilyDoctorBot", bot);
        jobDetail.getJobDataMap().putIfAbsent("Message", "Ping");
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(schedule)
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    private static Map<String, String> timeMessage() {
        Map<String, String> map = new HashMap<>();
        map.put("0 15 8 ? * MON-FRI *", "1");
        map.put("0 0 10 ? * MON-FRI *", "2");
        map.put("0 0 11 ? * MON-FRI *", "3");
        map.put("0 0 12 ? * MON-FRI *", "4");
        map.put("0 0 16 ? * MON-FRI *", "5");
        map.put("0 0 18 ? * MON-FRI *", "6");
        map.put("0 0 21 ? * MON-FRI *", "7");
        return map;
    }
}
