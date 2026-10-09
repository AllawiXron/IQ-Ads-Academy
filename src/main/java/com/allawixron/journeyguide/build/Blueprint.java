package com.allawixron.journeyguide.build;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A build the player can place as a hologram and then build block by block.
 * <p>
 * Layers are drawn from above: each string is one row from back (north) to front (south),
 * each character is one block from left (west) to right (east). The entrance faces south,
 * so when placed, the build is turned so the entrance faces the player.
 * <ul>
 *   <li>a key character = that block goes here</li>
 *   <li>{@code .} = must be empty (inside of the build)</li>
 *   <li>space = doesn't matter</li>
 * </ul>
 */
public final class Blueprint {
    public final String id;
    public final String name;
    public final String description;
    public final Item icon;
    /** How many layers sit below the ground (for floors and farms dug into the ground). */
    public final int sink;
    public final int width;
    public final int height;
    public final int depth;
    /** [y][z][x]; null = don't care. */
    private final @Nullable BlockState[][][] cells;

    private Blueprint(String id, String name, String description, Item icon, int sink, @Nullable BlockState[][][] cells) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sink = sink;
        this.cells = cells;
        this.height = cells.length;
        this.depth = cells[0].length;
        this.width = cells[0][0].length;
    }

    public record Cell(BlockPos pos, BlockState state, int layer) {
        public boolean mustBeEmpty() {
            return state.isAir();
        }
    }

    /** All cells placed in the world at the given origin and rotation, bottom layer first. */
    public List<Cell> cells(BlockPos origin, Rotation rotation) {
        List<Cell> result = new ArrayList<>();
        for (int y = 0; y < height; y++)
            for (int z = 0; z < depth; z++)
                for (int x = 0; x < width; x++) {
                    BlockState state = cells[y][z][x];
                    if (state == null)
                        continue;
                    BlockPos pos = origin.offset(new BlockPos(x, y, z).rotate(rotation));
                    result.add(new Cell(pos, state.rotate(rotation), y));
                }
        return result;
    }

    /** Blocks needed for the whole build, most needed first. */
    public Map<Block, Integer> materials() {
        Map<Block, Integer> counts = new HashMap<>();
        for (Cell cell : cells(BlockPos.ZERO, Rotation.NONE))
            if (!cell.mustBeEmpty())
                counts.merge(BuildCheck.materialOf(cell.state()), 1, Integer::sum);
        return sortByCount(counts);
    }

    public int blockCount() {
        int n = 0;
        for (Cell cell : cells(BlockPos.ZERO, Rotation.NONE))
            if (!cell.mustBeEmpty() && BuildCheck.materialOf(cell.state()) != Blocks.AIR)
                n++;
        return n;
    }

    public static Map<Block, Integer> sortByCount(Map<Block, Integer> counts) {
        Map<Block, Integer> sorted = new LinkedHashMap<>();
        counts.entrySet().stream()
                .filter(e -> e.getKey() != Blocks.AIR)
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> sorted.put(e.getKey(), e.getValue()));
        return sorted;
    }

    /** Turns the build so its entrance faces back toward a player looking in {@code facing}. */
    public static Rotation rotationFor(Direction facing) {
        return switch (facing) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    /**
     * Where to put the build's origin so that the middle of its entrance row sits on {@code anchor},
     * where anchor is the first empty block on top of the ground.
     */
    public BlockPos originFor(BlockPos anchor, Rotation rotation) {
        BlockPos frontCenter = new BlockPos(width / 2, sink, depth - 1).rotate(rotation);
        return anchor.subtract(frontCenter);
    }

    // ------------------------------------------------------------------ builder

    public static Builder builder(String id, String name, Item icon) {
        return new Builder(id, name, icon);
    }

    public static final class Builder {
        private final String id;
        private final String name;
        private final Item icon;
        private String description = "";
        private int sink;
        private final Map<Character, BlockState> keys = new HashMap<>();
        private final List<String[]> layers = new ArrayList<>();

        private Builder(String id, String name, Item icon) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            keys.put('.', Blocks.AIR.defaultBlockState());
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder sink(int sink) {
            this.sink = sink;
            return this;
        }

        public Builder key(char c, Block block) {
            return key(c, block.defaultBlockState());
        }

        public Builder key(char c, BlockState state) {
            keys.put(c, state);
            return this;
        }

        public Builder layer(String... rows) {
            layers.add(rows);
            return this;
        }

        public Blueprint build() {
            int depth = layers.getFirst().length;
            int width = layers.getFirst()[0].length();
            @Nullable BlockState[][][] cells = new BlockState[layers.size()][depth][width];
            for (int y = 0; y < layers.size(); y++) {
                String[] rows = layers.get(y);
                if (rows.length != depth)
                    throw new IllegalStateException(id + ": layer " + y + " has " + rows.length + " rows, expected " + depth);
                for (int z = 0; z < depth; z++) {
                    String row = rows[z];
                    if (row.length() != width)
                        throw new IllegalStateException(id + ": layer " + y + " row " + z + " is " + row.length() + " wide, expected " + width);
                    for (int x = 0; x < width; x++) {
                        char c = row.charAt(x);
                        if (c == ' ')
                            continue;
                        BlockState state = keys.get(c);
                        if (state == null)
                            throw new IllegalStateException(id + ": unknown key '" + c + "' in layer " + y);
                        cells[y][z][x] = state;
                    }
                }
            }
            return new Blueprint(id, name, description, icon, sink, cells);
        }
    }
}
