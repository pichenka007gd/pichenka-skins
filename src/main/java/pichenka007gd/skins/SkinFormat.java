package pichenka007gd.skins;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class SkinFormat
{
    private SkinFormat() {}

    /** Converts any skin to the legacy 64x32 format used by Minecraft 1.2.5. */
    public static BufferedImage toLegacy(BufferedImage img)
    {
        if (img == null)
        {
            return null;
        }

        int w = img.getWidth();
        int h = img.getHeight();

        if (w == 64 && h == 32)
        {
            return img;
        }

        BufferedImage source = img;

        if (w != 64 || h != 64)
        {
            source = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = source.createGraphics();
            g.drawImage(img, 0, 0, 64, 64, null);
            g.dispose();
        }

        BufferedImage out = new BufferedImage(64, 32, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(source.getSubimage(0, 0, 64, 32), 0, 0, null);
        g.dispose();
        return out;
    }
}
