# Armor Badges (Fabric, Minecraft 1.21.4)

Client-side mod. Above every player's nametag it shows their equipped armor
(helmet, chestplate, leggings, boots) as small item icons, each followed by the
remaining durability as a percentage. The percentage is colored green -> red
as the piece wears down.

- Works on any server, because nothing is sent over the network. Only you see it.
- Shows for every player whose nametag is visible (not your own in first person,
  same as vanilla nametags).
- Press **K** to toggle on/off (rebindable in Controls).

## Build

Requires JDK 21.

    gradle wrapper        # once, if the project has no gradlew yet
    ./gradlew build

The jar is in `build/libs/armor-badges-1.0.0.jar`. Drop it in your `mods`
folder together with Fabric API.

If a dependency version fails to resolve, copy the current 1.21.1 values from
https://fabricmc.net/develop into `gradle.properties`.

## Notes

- Targets 1.21.4 (Yarn mappings). Other versions need
  separate porting (the render code changes between 1.21.x releases).
- Tweak size/spacing with the constants at the top of `ArmorBadgeRenderer.java`.
