# Pichenka Skins <img src="src/main/resources/pichenka-skins-icon.png" width="64" alt="icon">

**Player skins for Minecraft 1.2.5 that actually load** — v1.0.0 by *pichenka007gd*.

Vanilla 1.2.5 asks the long-dead Mojang S3 for skins, so everyone is Steve. This little mod asks better places instead — with a priority list you control. Works for your own skin and every other player. ✨

## Install

1. Drop `pichenka-skins-1.0.0-mc1.2.5.jar` into your instance's `mods/` folder.
2. Launch the game. That's it.

The mod quietly swaps the vanilla skin-loader class in memory at startup — no jar surgery, no JVM flags, no launcher wizardry. Works with and without the Forge 1.2.5 backport (FML sees it as a normal mod called `mod_PichenkaSkins`).

## Config

On first launch the mod writes `pichenka-skins.properties` right next to `options.txt` (your game folder). Edit it while the game is closed:

```properties
enabled=true
priority=local,tlauncher,mc-heads,minotar,official
timeout=5000
```

- `priority` — sources tried top to bottom; first skin found wins
- `local` — your own file, `skins/<playername>.png` in the game folder
- `enabled=false` — back to vanilla behaviour
- `timeout` — HTTP timeout in milliseconds (1000–60000)

## Sources

| id | what it is |
|---|---|
| `local` | `skins/<name>.png` right in your game folder |
| `tlauncher` | TLauncher skin service |
| `mc-heads` | Mojang skins by name (gives a default skin for unknown names) |
| `minotar` | Mojang skins by name |
| `official` | the original Mojang S3 URL (usually 404 these days, kept as last resort) |

Modern **64×64 skins are converted to the legacy 64×32 format** automatically.

## Build

```bash
./gradlew build
```

Output lands in `build/dist/`. Building needs JDK 17+; the mod itself runs on the game's Java 8. The build downloads the official 1.2.5 client jar from Mojang (SHA-1 verified) and remaps it with the MCP 6.2 mappings from `mappings/`, then reobfuscates the mod back for the real game.

## Notes

- Diagnostics: a `pichenka-skins.log` file appears in the game folder — everything the mod does is written there.
- Classic launchers without FML: extract the contents of the jar into `minecraft.jar` instead (the patched `ahw.class` is included).

by **pichenka007gd** · v1.0.0 · for Minecraft 1.2.5
