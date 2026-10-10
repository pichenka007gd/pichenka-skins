package pichenka007gd.skins;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class SkinLog
{
    private SkinLog() {}

    public static void log(String message)
    {
        String line = "[PichenkaSkins] " + message;
        System.out.println(line);

        try
        {
            File dir = new File(System.getProperty("user.dir", "."));
            FileWriter w = new FileWriter(new File(dir, "pichenka-skins.log"), true);

            try
            {
                w.write(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " " + line + "\n");
            }
            finally
            {
                w.close();
            }
        }
        catch (Exception e)
        {
            System.out.println("[PichenkaSkins] cannot write pichenka-skins.log: " + e);
        }
    }
}
