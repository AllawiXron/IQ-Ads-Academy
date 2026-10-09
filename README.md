# Journey Guide

A Minecraft **Forge 26.3** mod that guides you from your first night all the way to killing the Ender Dragon,
so you always know what your next step is.

## What it does

- **A journey of 35 steps in 6 chapters**: The First Day, Settling In, Going Deeper, The Nether, Eyes of Ender, The End.
  Steps tick themselves off as you play. It checks your inventory, what you've crafted and picked up, and your advancements.
  You can do things in any order, and steps you've already done count right away.
- **Tracker** in the top left: a small box with your current step and progress (e.g. `Collect 8 iron ingots 3/8`).
  A new tip shows for a few seconds when it unlocks. Press **H** to switch between compact, detailed (always shows tips)
  and hidden.
- **Done early**: if you do a later step before you get to it, it's ticked off with a "Done early!" pop-up, and when
  the guide jumps over steps you already did, chat tells you which ones.
- **Journal** (press **J**): every chapter and step, why it matters and how to do it.
  More tips unlock over time while you're on a step, or press *Next tip*.
- **Hints when stuck**: on "find a place" steps (Nether fortress, stronghold), a pointer arrow and a gold beam
  unlock after 15 minutes so you don't wander forever.
- **Holographic blueprints** (journal > *Builds*): pick a build, see it as see-through real blocks, move and rotate it,
  and place it. Then build it layer by layer:
  - the current layer shows as see-through blocks; the next layers are fainter
  - **hold the right block and right-click a see-through block** to place it right there, already facing the right way
    (doors and beds included)
  - wrong blocks turn red, blocks in the way and ground to dig out are outlined in orange
  - the tracker shows what this layer needs next to how many you're carrying
  - the build checks itself and completes the matching journey step

  Included builds (cozy rustic style): **First Night Shelter, Cozy Cabin, Wheat Farm, Animal Pen, Enchanting Room, Nether Portal**.

## Install

1. Install **Minecraft Forge 26.3** (build 66.0.10 or newer).
2. Put `journeyguide-26.3-0.2.0.jar` in your `.minecraft/mods` folder.
3. Start the game with the Forge 26.3 profile.

## Controls

| Key | Action |
| --- | --- |
| J | Open / close the journal |
| H | Tracker: compact / detailed / hidden |
| Right-click | Place the block you hold into the see-through block you aim at (while building) |
| R | Rotate the build (while placing) |
| Up / Down arrows | Move the build up or down (while placing) |
| Enter | Place the build (while placing) |
| Backspace | Cancel placing |

You can change all of these in *Options > Controls > Key Binds > Journey Guide*.

## Commands

- `/journey` shows your current step
- `/journey skip` skips the current step (the journal has a *Skip step* button too)
- `/journey complete <step>` marks a step as done
- `/journey reset` starts the journey over
- `/journey buildnow` instantly finishes your current build (needs cheats; handy in creative)

## Settings

`config/journeyguide-client.toml` (your screen only):

- `trackerMode`: `COMPACT` (default), `DETAILED` or `HIDDEN`
- `trackerCorner`: `TOP_LEFT` (default) or `TOP_RIGHT`

`config/journeyguide-common.toml`:

- `extraTipMinutes` (default 6): minutes on a step before the next tip unlocks by itself
- `pointerMinutes` (default 15): minutes on a "find a place" step before the pointer unlocks
- `pointerSearchChunks` (default 100): how far the pointer searches

## Building from source

Needs Java 25 (Gradle downloads it automatically).

```
./gradlew build
```

The mod jar ends up in `build/libs/`. `./gradlew runClient` starts a test game.
