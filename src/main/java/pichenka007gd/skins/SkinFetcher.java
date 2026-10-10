package pichenka007gd.skins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;

public final class SkinFetcher
{
    private SkinFetcher() {}

    public static BufferedImage fetchSkin(String location)
    {
        SkinConfig config = SkinConfig.get();

        if (!config.enabled)
        {
            return null;
        }

        String name = SkinSources.skinNameFromUrl(location);

        if (name == null)
        {
            return null;
        }

        List<SkinSource> sources = SkinSources.candidates();

        for (SkinSource source : sources)
        {
            try
            {
                BufferedImage img = source.fetch(name, config.timeoutMs);

                if (img == null)
                {
                    continue;
                }

                img = SkinFormat.toLegacy(img);

                if (img == null)
                {
                    continue;
                }

                SkinLog.log(name + " <- " + source.describe());
                return img;
            }
            catch (Exception e)
            {
                SkinLog.log(name + ": source " + source.id() + " failed (" + e + ")");
            }
        }

        SkinLog.log(name + ": no source returned a skin, falling back to vanilla URL");
        return null;
    }

    static BufferedImage fetchHttp(String urlTemplate, String name, int connectTimeoutMs, int readTimeoutMs) throws IOException
    {
        String url = String.format(urlTemplate, name);
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(false);
        conn.setConnectTimeout(connectTimeoutMs);
        conn.setReadTimeout(readTimeoutMs);
        conn.setRequestProperty("User-Agent", "PichenkaSkins/" + SkinConfig.VERSION + " (Minecraft 1.2.5)");

        try
        {
            conn.connect();

            if (conn.getResponseCode() / 100 != 2)
            {
                return null;
            }

            return ImageIO.read(conn.getInputStream());
        }
        finally
        {
            conn.disconnect();
        }
    }

    static BufferedImage fetchFile(File file) throws IOException
    {
        if (file == null || !file.isFile())
        {
            return null;
        }

        InputStream in = new FileInputStream(file);

        try
        {
            return ImageIO.read(in);
        }
        finally
        {
            in.close();
        }
    }
}
