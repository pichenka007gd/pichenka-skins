package pichenka007gd.skins;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

public final class PatchInstaller
{
    private PatchInstaller() {}

    public static void install()
    {
        try
        {
            byte[] bytes = readResource("/patched/ahw.class");

            if (bytes == null)
            {
                SkinLog.log("ERROR: /patched/ahw.class is missing from the mod jar");
                return;
            }

            ClassLoader loader = PatchInstaller.class.getClassLoader();
            Object unsafe = getUnsafe();
            Method defineClass = unsafe.getClass().getMethod("defineClass",
                    String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
            defineClass.invoke(unsafe, "ahw", bytes, 0, bytes.length, loader, PatchInstaller.class.getProtectionDomain());
            SkinLog.log("patch installed: class ahw (skin loader) replaced in memory, no jar edits or JVM settings needed");
        }
        catch (Exception e)
        {
            Throwable cause = e instanceof InvocationTargetException ? ((InvocationTargetException)e).getCause() : e;

            if (cause instanceof LinkageError)
            {
                SkinLog.log("WARNING: class ahw was loaded before the mod could patch it, skins stay vanilla (" + cause + ")");
            }
            else
            {
                SkinLog.log("WARNING: patch install failed, skins stay vanilla (" + cause + ")");
            }
        }
    }

    private static Object getUnsafe() throws Exception
    {
        Field f = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return f.get(null);
    }

    private static byte[] readResource(String path)
    {
        InputStream in = PatchInstaller.class.getResourceAsStream(path);

        if (in == null)
        {
            return null;
        }

        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;

            while ((n = in.read(buf)) > 0)
            {
                out.write(buf, 0, n);
            }

            return out.toByteArray();
        }
        catch (Exception e)
        {
            return null;
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (Exception ignored) {}
        }
    }
}
