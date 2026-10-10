package pichenka007gd.skins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

public final class SkinConfig
{
    public static final String VERSION = "1.0.0";
    public static final String AUTHOR = "pichenka007gd";

    public static final String DEFAULT_CONFIG =
            "# Pichenka Skins " + VERSION + " by " + AUTHOR + "\n" +
            "# Skin sources for Minecraft 1.2.5, tried in the order listed in priority.\n" +
            "# Known sources: local, tlauncher, mc-heads, minotar, official\n" +
            "# local = .minecraft/skins/<playername>.png (your own file, checked first by default)\n" +
            "enabled=true\n" +
            "priority=local,tlauncher,mc-heads,minotar,official\n" +
            "timeout=5000\n";

    private static SkinConfig instance;

    public final boolean enabled;
    public final List<String> priority;
    public final int timeoutMs;
    public final File gameDir;
    public final File configFile;

    private SkinConfig(boolean enabled, List<String> priority, int timeoutMs, File gameDir, File configFile)
    {
        this.enabled = enabled;
        this.priority = priority;
        this.timeoutMs = timeoutMs;
        this.gameDir = gameDir;
        this.configFile = configFile;
    }

    public static synchronized SkinConfig get()
    {
        if (instance == null)
        {
            instance = load();
        }

        return instance;
    }

    private static SkinConfig load()
    {
        File gameDir = detectGameDir();
        File configFile = new File(gameDir, "pichenka-skins.properties");
        Properties props = new Properties();

        if (configFile.isFile())
        {
            InputStream in = null;

            try
            {
                in = new FileInputStream(configFile);
                props.load(in);
            }
            catch (IOException e)
            {
                SkinLog.log("cannot read " + configFile + ": " + e);
            }
            finally
            {
                if (in != null)
                {
                    try
                    {
                        in.close();
                    }
                    catch (IOException ignored) {}
                }
            }
        }
        else
        {
            writeDefault(configFile);
        }

        boolean enabled = Boolean.parseBoolean(props.getProperty("enabled", "true").trim());
        int timeout = parseTimeout(props.getProperty("timeout", "5000"));
        List<String> priority = parsePriority(props.getProperty("priority", SkinSources.DEFAULT_PRIORITY));
        SkinConfig config = new SkinConfig(enabled, priority, timeout, gameDir, configFile);
        SkinLog.log("v" + VERSION + " by " + AUTHOR + " loaded; config=" + configFile.getAbsolutePath()
                + "; enabled=" + enabled + "; priority=" + priority + "; timeout=" + timeout + "ms");
        return config;
    }

    private static File detectGameDir()
    {
        File dir = new File(System.getProperty("user.dir", "."));
        File parent = dir.getParentFile();

        if (containsGame(dir))
        {
            return dir;
        }

        if (parent != null && containsGame(parent))
        {
            return parent;
        }

        return dir;
    }

    private static boolean containsGame(File dir)
    {
        return new File(dir, "options.txt").isFile()
                || new File(dir, "saves").isDirectory()
                || new File(dir, "resources").isDirectory();
    }

    private static void writeDefault(File configFile)
    {
        try
        {
            FileOutputStream out = new FileOutputStream(configFile);

            try
            {
                out.write(DEFAULT_CONFIG.getBytes("UTF-8"));
            }
            finally
            {
                out.close();
            }

            SkinLog.log("created default config at " + configFile.getAbsolutePath());
        }
        catch (IOException e)
        {
            SkinLog.log("cannot create config " + configFile + ": " + e);
        }
    }

    private static int parseTimeout(String raw)
    {
        try
        {
            int value = Integer.parseInt(raw.trim());

            if (value >= 1000 && value <= 60000)
            {
                return value;
            }
        }
        catch (NumberFormatException ignored) {}

        SkinLog.log("invalid timeout '" + raw + "', using 5000ms");
        return 5000;
    }

    private static List<String> parsePriority(String raw)
    {
        Set<String> result = new LinkedHashSet<String>();

        for (String part : raw.split(","))
        {
            String id = part.trim().toLowerCase(Locale.ROOT);

            if (id.isEmpty() || id.startsWith("#"))
            {
                continue;
            }

            if (SkinSources.isKnown(id))
            {
                result.add(id);
            }
            else
            {
                SkinLog.log("unknown source '" + id + "' in priority, skipped");
            }
        }

        if (result.isEmpty())
        {
            SkinLog.log("priority is empty, using defaults");
            result.addAll(SkinSources.defaultPriority());
        }

        return new ArrayList<String>(result);
    }
}
