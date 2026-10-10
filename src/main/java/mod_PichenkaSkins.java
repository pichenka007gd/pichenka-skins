import pichenka007gd.skins.PatchInstaller;
import pichenka007gd.skins.SkinConfig;

public class mod_PichenkaSkins extends BaseMod
{
    public void load()
    {
        SkinConfig.get();
        PatchInstaller.install();
    }

    public String getVersion()
    {
        return "Pichenka Skins " + SkinConfig.VERSION + " by " + SkinConfig.AUTHOR + " (Minecraft 1.2.5)";
    }
}
