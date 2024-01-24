package com.codepunisher.quests.tasks;

//TODO: Convert to command to avoid concurrency issues
//TODO: Make clear in the config that the time configured
//TODO: is for display purposes only and should MATCH whatever schedule
//TODO: is being used for the command to reset the quests (explain the reason why)
public class DailyQuestResetTaskTimer implements Runnable {
    @Override
    public void run() {
        //TODO: What to do about this running on multiple instances???

        //TODO: Clear redis cache key of daily player data
        //TODO: Clear redis daily values
        //TODO: Clear local caches
        //TODO: Check from specific time in config (every second)
        //TODO: Broadcast this through redis
    }
}
