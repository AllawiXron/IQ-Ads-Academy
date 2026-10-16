# Backpacks 2.0

Backpacks for Minecraft 26.3 (Forge). The big update of my old Backpacks mod for 1.19.2.

## What's new compared to the old version

- Way more space: Leather 27, Iron 36, Golden 45, Diamond 54, Emerald 77, Netherite 104 slots
- Upgrading keeps everything inside (no more emptying your backpack first)
- Wear it in your chestplate slot: it shows on your back and you open it with **B**
- Worn backpacks give a bit of armor (netherite also gives toughness)
- Ender Backpack: opens your ender chest from anywhere
- Buttons in the backpack: Sort, Deposit matching items, Take all
- Auto pickup: items you pick up go straight into the backpack if it already has some of them
- Tooltip shows what's inside
- Netherite backpack doesn't burn in lava or fire
- Leather backpack can be dyed any color, like leather armor
- 3D backpack models in your hand, on the ground and on your back
- You can't put a backpack inside a backpack, and the open backpack is locked in place so nothing can get duplicated

## Recipes

- Leather Backpack: string, leather, string / leather, chest, leather / leather, leather, leather
- Iron Backpack: Leather Backpack + Iron Block (crafting grid)
- Golden Backpack: Iron Backpack + Gold Block
- Diamond Backpack: Golden Backpack + Diamond Block
- Emerald Backpack: Diamond Backpack + Emerald Block
- Netherite Backpack: Emerald Backpack + Netherite Upgrade template + Netherite Ingot (smithing table)
- Ender Backpack: string, ender pearl, string / leather, ender chest, leather / leather, leather, leather
- Dye: Leather Backpack + any dye

## Controls

- Right-click the backpack: open it
- B: open the backpack you wear (or the first one in your inventory)
- You can change the key in Options > Controls > Key Binds

## Building

Needs Java 25. Run `gradlew build`, the jar ends up in `build/libs/`.
