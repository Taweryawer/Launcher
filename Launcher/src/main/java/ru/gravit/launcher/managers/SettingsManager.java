package ru.gravit.launcher.managers;

import ru.gravit.launcher.LauncherAPI;
import ru.gravit.launcher.NewLauncherSettings;
import ru.gravit.launcher.client.DirBridge;
import ru.gravit.launcher.hasher.HashedDir;
import ru.gravit.launcher.serialize.HInput;
import ru.gravit.launcher.serialize.HOutput;
import ru.gravit.utils.config.JsonConfigurable;
import ru.gravit.utils.helper.IOHelper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

public class SettingsManager extends JsonConfigurable<NewLauncherSettings> {
    public class StoreFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException
        {
            try(HInput input = new HInput(IOHelper.newInput(file)))
            {
                HashedDir dir = new HashedDir(input);
                settings.lastHDirs.put(file.getFileName().toString(), dir);
            }
            return super.visitFile(file, attrs);
        }

    }
    @LauncherAPI
    public static NewLauncherSettings settings;

    public SettingsManager() {
        super(NewLauncherSettings.class, DirBridge.dir.resolve("settings.json"));
    }
    @LauncherAPI
    @Override
    public NewLauncherSettings getConfig() {
        if(settings.updatesDir != null)
        settings.updatesDirPath = settings.updatesDir.toString();
        return settings;
    }
    @LauncherAPI
    @Override
    public NewLauncherSettings getDefaultConfig() {
        return new NewLauncherSettings();
    }
    @LauncherAPI
    @Override
    public void setConfig(NewLauncherSettings config) {
        settings = config;
        if(settings.updatesDirPath != null)
        settings.updatesDir = Paths.get(settings.updatesDirPath);
    }
    @LauncherAPI
    public void loadHDirStore(Path storePath) throws IOException
    {
        Files.createDirectories(storePath);
        IOHelper.walk(storePath, new StoreFileVisitor(), false);
    }
    @LauncherAPI
    public void saveHDirStore(Path storeProjectPath) throws IOException
    {
        Files.createDirectories(storeProjectPath);
        for(Map.Entry<String, HashedDir> e : settings.lastHDirs.entrySet())
        {
            Path file = Files.createFile(storeProjectPath.resolve(e.getKey()));
            try(HOutput output = new HOutput(IOHelper.newOutput(file)))
            {
                e.getValue().write(output);
            }
        }
    }
    @LauncherAPI
    public void loadHDirStore() throws IOException
    {
        loadHDirStore(DirBridge.dirStore);
    }
    @LauncherAPI
    public void saveHDirStore() throws IOException
    {
        saveHDirStore(DirBridge.dirProjectStore);
    }

    @Override
    public void setType(Type type) {
        super.setType(type);
    }
}