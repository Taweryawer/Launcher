package ru.gravit.launchserver.command.service;

import ru.gravit.launchserver.LaunchServer;
import ru.gravit.launchserver.command.Command;
import ru.gravit.utils.helper.LogHelper;

public class ReloadInfoCommand extends Command {
    public ReloadInfoCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return "";
    }

    @Override
    public String getUsageDescription() {
        return "print reloadable configs";
    }

    @Override
    public void invoke(String... args) throws Exception {
        server.reloadManager.printReloadables();
    }
}
