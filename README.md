# Journey Guide

A mod that guides you from your first night all the way to killing the ender dragon! It tells you what to do
next, gives you tips when you get stuck and shows you how to build cool houses so you never get lost in your
minecraft world.

Made for Forge 26.3.

## Features

- 35 steps in 6 chapters, from punching your first tree to the ender dragon. Steps tick off by themselves when
  you do them, and if you already did something it counts right away
- A small tracker in the corner that shows your current step (press H to make it bigger or hide it)
- A journal (press J) with every step, why you need it and tips on how to do it
- If you're stuck looking for a fortress or a stronghold for too long you get an arrow pointing the way
- Blueprints for houses and stuff: First Night Shelter, Cozy Cabin, Wheat Farm, Animal Pen, Enchanting Room
  and a Nether Portal. You see the build as see-through blocks and just right-click them with the right block
  to place it, it even faces the right way

## How to install

1. Install Forge 26.3 (66.0.10 or newer)
2. Put the jar in your `.minecraft/mods` folder
3. Start the game with Forge

## Controls

- J: open the journal
- H: tracker small / big / hidden
- Right-click a see-through block: place the block you're holding there
- While placing a build: R rotates it, up/down arrows move it up and down, Enter places it, Backspace cancels

You can change the keys in Options > Controls > Key Binds.

## Commands

- `/journey` shows your current step
- `/journey skip` skips the step you're on
- `/journey complete <step>` marks a step as done
- `/journey reset` starts over
- `/journey buildnow` finishes your build instantly (needs cheats)

## Config

`journeyguide-client.toml`: `trackerMode` (COMPACT, DETAILED or HIDDEN) and `trackerCorner` (TOP_LEFT or TOP_RIGHT)

`journeyguide-common.toml`: `extraTipMinutes`, `pointerMinutes` and `pointerSearchChunks`

## Building

`./gradlew build`, the jar ends up in `build/libs/`.
