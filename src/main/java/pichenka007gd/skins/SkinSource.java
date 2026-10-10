package pichenka007gd.skins;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface SkinSource
{
    String id();

    String describe();

    BufferedImage fetch(String name, int timeoutMs) throws IOException;
}
