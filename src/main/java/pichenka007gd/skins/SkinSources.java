package pichenka007gd.skins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SkinSources
{
    public static final String DEFAULT_PRIORITY = "local,tlauncher,mc-heads,minotar,official";

    static final String LOCAL_ID = "local";
    static final String TLAUNCHER_ID = "tlauncher";
    static final String MCHEADS_ID = "mc-heads";
    static final String MINOTAR_ID = "minotar";
    static final String OFFICIAL_ID = "official";

    private static final String SKIN_URL_PREFIX = "http://s3.amazonaws.com/MinecraftSkins/";
    private static final String SKIN_URL_PREFIX_SECURE = "https://s3.amazonaws.com/MinecraftSkins/";

    private SkinSources() {}

    public static boolean isKnown(String id)
    {
        return LOCAL_ID.equals(id) || TLAUNCHER_ID.equals(id) || MCHEADS_ID.equals(id)
                || MINOTAR_ID.equals(id) || OFFICIAL_ID.equals(id);
    }

    public static List<String> defaultPriority()
    {
        return Arrays.asList(DEFAULT_PRIORITY.split(","));
    }

    public static boolean isPlayerSkin(String location)
    {
        if (location == null || !location.endsWith(".png"))
        {
            return false;
        }

        return location.startsWith(SKIN_URL_PREFIX) || location.startsWith(SKIN_URL_PREFIX_SECURE);
    }

    public static String skinNameFromUrl(String location)
    {
        if (!isPlayerSkin(location))
        {
            return null;
        }

        String prefix = location.startsWith(SKIN_URL_PREFIX_SECURE) ? SKIN_URL_PREFIX_SECURE : SKIN_URL_PREFIX;
        String name = location.substring(prefix.length(), location.length() - ".png".length());

        return name.isEmpty() ? null : name;
    }

    public static List<SkinSource> candidates()
    {
        SkinConfig config = SkinConfig.get();
        List<SkinSource> out = new ArrayList<SkinSource>();

        for (String id : config.priority)
        {
            SkinSource source = create(id, config.gameDir);

            if (source != null)
            {
                out.add(source);
            }
        }

        return out;
    }

    private static SkinSource create(String id, File gameDir)
    {
        if (LOCAL_ID.equals(id))
        {
            return new LocalSource(gameDir);
        }

        if (TLAUNCHER_ID.equals(id))
        {
            return new HttpSource(TLAUNCHER_ID, "TLauncher",
                    "https://auth.tlauncher.org/skin/fileservice/skins/skin_%s.png");
        }

        if (MCHEADS_ID.equals(id))
        {
            return new HttpSource(MCHEADS_ID, "mc-heads",
                    "https://mc-heads.net/skin/%s");
        }

        if (MINOTAR_ID.equals(id))
        {
            return new HttpSource(MINOTAR_ID, "Minotar",
                    "https://minotar.net/skin/%s");
        }

        if (OFFICIAL_ID.equals(id))
        {
            return new HttpSource(OFFICIAL_ID, "Mojang S3 (official)",
                    SKIN_URL_PREFIX + "%s.png");
        }

        return null;
    }

    static final class LocalSource implements SkinSource
    {
        private final File gameDir;

        LocalSource(File gameDir)
        {
            this.gameDir = gameDir;
        }

        public String id()
        {
            return LOCAL_ID;
        }

        public String describe()
        {
            return "local file in " + new File(gameDir, "skins").getPath();
        }

        public BufferedImage fetch(String name, int timeoutMs) throws IOException
        {
            return SkinFetcher.fetchFile(file(name));
        }

        private File file(String name)
        {
            return new File(new File(gameDir, "skins"), name + ".png");
        }
    }

    static final class HttpSource implements SkinSource
    {
        private final String id;
        private final String label;
        private final String urlTemplate;

        HttpSource(String id, String label, String urlTemplate)
        {
            this.id = id;
            this.label = label;
            this.urlTemplate = urlTemplate;
        }

        public String id()
        {
            return id;
        }

        public String describe()
        {
            return label + " (" + urlTemplate.replace("%s", "<name>") + ")";
        }

        public BufferedImage fetch(String name, int timeoutMs) throws IOException
        {
            return SkinFetcher.fetchHttp(urlTemplate, name,
                    Math.min(timeoutMs, 4000), timeoutMs);
        }
    }
}
