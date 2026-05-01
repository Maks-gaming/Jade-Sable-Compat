# Sable Jade Compat

Sable Jade Compat is a NeoForge client compatibility mod for Minecraft 1.21.1.

Its job is simple: when a block belongs to a Sable sublevel or moving construction, Jade should identify the block you are actually looking at instead of some wrong block from the main level.

## Release 1.0.0

Release `1.0.0` is the first stable version of the project.

It targets:

- Minecraft `1.21.1`
- NeoForge `21.1.227`
- Sable `1.1.3`
- Jade `15.10.5+neoforge`
- Java `21`

## What It Fixes

- Corrects Jade block selection on Sable sublevels.
- Makes Jade tooltip title, icon, and selected block line up with the actual block under the crosshair.
- Uses a Sable-aware client retrace path so moving constructions and sublevel rendering use the right pose during tooltip targeting.

## Installation

Client:

1. Install NeoForge for Minecraft `1.21.1`.
2. Put `Sable`, `Jade`, and `sablejade` into the client `mods` folder.

Server:

1. Install `Sable`.
2. Install `sablejade` if you want matching compatibility behavior in multiplayer environments.

## Build

Windows:

```powershell
.\gradlew.bat build
```

Bash:

```bash
./gradlew build
```

The release jar is generated in `build/libs/`.

## Repository Layout

- `src/main/java/dev/mgcode/sablejade` — mod source
- `src/main/templates/META-INF/neoforge.mods.toml` — generated mod metadata template
- `.github/workflows` — CI and release workflows

## License

This project is licensed under the MIT license. See `LICENSE`.
