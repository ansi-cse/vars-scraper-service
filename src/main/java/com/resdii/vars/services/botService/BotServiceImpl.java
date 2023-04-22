package com.resdii.vars.services.botService;

import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author ANSI.
 */

@Service
public class BotServiceImpl implements BotService{
    private TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;

    @Autowired
    public BotServiceImpl(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void start() {
        if (scheduledFuture != null) {
            if(scheduledFuture.getDelay(TimeUnit.SECONDS) < 5){
                return;
            }
        }
        CronTrigger cronTrigger= (CronTrigger) TriggerBuilder.newTrigger()
                .withIdentity("trigger", "group")
                .startAt(new Date())
                .withSchedule(
                        org.quartz.SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInHours(24)
                                .repeatForever())
                .build();
        scheduledFuture = taskScheduler.schedule(
                ()->{}, cronTrigger);
    }
    public void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }
}
