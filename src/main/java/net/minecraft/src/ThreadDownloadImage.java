package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import pichenka007gd.skins.SkinFetcher;
import pichenka007gd.skins.SkinSources;

class ThreadDownloadImage extends Thread
{
    final String location;
    final ImageBuffer buffer;
    final ThreadDownloadImageData imageData;

    ThreadDownloadImage(ThreadDownloadImageData par1ThreadDownloadImageData, String par2Str, ImageBuffer par3ImageBuffer)
    {
        this.imageData = par1ThreadDownloadImageData;
        this.location = par2Str;
        this.buffer = par3ImageBuffer;
    }

    public void run()
    {
        try
        {
            BufferedImage var1;

            if (SkinSources.isPlayerSkin(this.location))
            {
                var1 = SkinFetcher.fetchSkin(this.location);

                if (var1 == null)
                {
                    var1 = this.downloadVanilla();
                }
            }
            else
            {
                var1 = this.downloadVanilla();
            }

            if (var1 == null)
            {
                return;
            }

            this.imageData.image = this.buffer == null ? var1 : this.buffer.parseUserSkin(var1);
        }
        catch (Exception var3)
        {
            var3.printStackTrace();
        }
    }

    private BufferedImage downloadVanilla() throws Exception
    {
        HttpURLConnection var1 = null;

        try
        {
            URL var2 = new URL(this.location);
            var1 = (HttpURLConnection)var2.openConnection();
            var1.setDoInput(true);
            var1.setDoOutput(false);
            var1.connect();

            if (var1.getResponseCode() / 100 == 4)
            {
                return null;
            }

            return ImageIO.read(var1.getInputStream());
        }
        finally
        {
            if (var1 != null)
            {
                var1.disconnect();
            }
        }
    }
}
