package org.bukkit.craftbukkit.block.data;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BlockSupport;
import org.bukkit.block.BlockType;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftSoundGroup;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.CraftBlockSupport;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftBlockData implements BlockData {

    private net.minecraft.world.level.block.state.BlockState state;
    private Map<Property<?>, Comparable<?>> parsedStates;

    protected CraftBlockData() {
        throw new AssertionError("Template Constructor");
    }

    protected CraftBlockData(net.minecraft.world.level.block.state.BlockState state) {
        this.state = state;
    }

    @Override
    public Material getMaterial() {
        return this.state.getBukkitMaterial(); // Paper - optimise getType calls
    }

    public net.minecraft.world.level.block.state.BlockState getState() {
        return this.state;
    }

    /**
     * Get a given BlockStateEnum's value as its Bukkit counterpart.
     *
     * @param nms the NMS state to convert
     * @param bukkit the Bukkit class
     * @param <B> the type
     * @return the matching Bukkit type
     */
    protected <B extends Enum<B>> B get(EnumProperty<?> nms, Class<B> bukkit) {
        return CraftBlockData.toBukkit(this.state.getValue(nms), bukkit);
    }

    /**
     * Convert all values from the given BlockStateEnum to their appropriate
     * Bukkit counterpart.
     *
     * @param nms the NMS state to get values from
     * @param bukkit the bukkit class to convert the values to
     * @param <B> the bukkit class type
     * @return an immutable Set of values in their appropriate Bukkit type
     */
    @SuppressWarnings("unchecked")
    protected <B extends Enum<B>> Set<B> getValues(EnumProperty<?> nms, Class<B> bukkit) {
        ImmutableSet.Builder<B> values = ImmutableSet.builder();

        for (Enum<?> e : nms.getPossibleValues()) {
            values.add(CraftBlockData.toBukkit(e, bukkit));
        }

        return values.build();
    }

    /**
     * Set a given {@link EnumProperty} with the matching enum from Bukkit.
     *
     * @param nms the NMS BlockStateEnum to set
     * @param bukkit the matching Bukkit Enum
     * @param <B> the Bukkit type
     * @param <N> the NMS type
     */
    protected <B extends Enum<B>, N extends Enum<N> & StringRepresentable> void set(EnumProperty<N> nms, Enum<B> bukkit) {
        this.parsedStates = null;
        this.state = this.state.setValue(nms, CraftBlockData.toNMS(bukkit, nms.getValueClass()));
    }

    @Override
    public BlockData merge(BlockData data) {
        CraftBlockData craft = (CraftBlockData) data;
        Preconditions.checkArgument(craft.parsedStates != null, "Data not created via string parsing");
        Preconditions.checkArgument(this.state.getBlock() == craft.state.getBlock(), "States have different types (got %s, expected %s)", data, this);

        CraftBlockData clone = (CraftBlockData) this.clone();
        clone.parsedStates = null;

        for (Property parsed : craft.parsedStates.keySet()) {
            clone.state = clone.state.setValue(parsed, craft.state.getValue(parsed));
        }

        return clone;
    }

    @Override
    public boolean matches(BlockData data) {
        if (data == null) {
            return false;
        }
        if (!(data instanceof CraftBlockData)) {
            return false;
        }

        CraftBlockData craft = (CraftBlockData) data;
        if (this.state.getBlock() != craft.state.getBlock()) {
            return false;
        }

        // Fastpath an exact match
        boolean exactMatch = this.equals(data);

        // If that failed, do a merge and check
        if (!exactMatch && craft.parsedStates != null) {
            return this.merge(data).equals(this);
        }

        return exactMatch;
    }

    private static final Map<Class<? extends Enum<?>>, Enum<?>[]> ENUM_VALUES = new java.util.concurrent.ConcurrentHashMap<>(); // Paper - cache block data strings; make thread safe

    /**
     * Convert an NMS Enum (usually a BlockStateEnum) to its appropriate Bukkit
     * enum from the given class.
     *
     * @throws IllegalStateException if the Enum could not be converted
     */
    @SuppressWarnings("unchecked")
    private static <B extends Enum<B>> B toBukkit(Enum<?> nms, Class<B> bukkit) {
        if (nms instanceof Direction) {
            return (B) CraftBlock.notchToBlockFace((Direction) nms);
        }
        return (B) CraftBlockData.ENUM_VALUES.computeIfAbsent(bukkit, Class::getEnumConstants)[nms.ordinal()];
    }

    /**
     * Convert a given Bukkit enum to its matching NMS enum type.
     *
     * @param bukkit the Bukkit enum to convert
     * @param nms the NMS class
     * @return the matching NMS type
     * @throws IllegalStateException if the Enum could not be converted
     */
    @SuppressWarnings("unchecked")
    public static <N extends Enum<N> & StringRepresentable> N toNMS(Enum<?> bukkit, Class<N> nms) {
        if (bukkit instanceof BlockFace) {
            return (N) CraftBlock.blockFaceToNotch((BlockFace) bukkit);
        }
        return (N) CraftBlockData.ENUM_VALUES.computeIfAbsent(nms, Class::getEnumConstants)[bukkit.ordinal()];
    }

    /**
     * Get the current value of a given state.
     *
     * @param ibs the state to check
     * @param <T> the type
     * @return the current value of the given state
     */
    protected <T extends Comparable<T>> T get(Property<T> ibs) {
        // Straight integer or boolean getter
        return this.state.getValue(ibs);
    }

    /**
     * Set the specified state's value.
     *
     * @param ibs the state to set
     * @param v the new value
     * @param <T> the state's type
     * @param <V> the value's type. Must match the state's type.
     */
    public <T extends Comparable<T>, V extends T> void set(Property<T> ibs, V v) {
        // Straight integer or boolean setter
        this.parsedStates = null;
        this.state = this.state.setValue(ibs, v);
    }

    @Override
    public String getAsString() {
        return this.toString(this.state.getValues());
    }

    @Override
    public String getAsString(boolean hideUnspecified) {
        return (hideUnspecified && this.parsedStates != null) ? this.toString(this.parsedStates) : this.getAsString();
    }

    @Override
    public BlockData clone() {
        try {
            return (BlockData) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError("Clone not supported", ex);
        }
    }

    @Override
    public String toString() {
        return "CraftBlockData{" + this.getAsString() + "}";
    }

    // Mimicked from BlockDataAbstract#toString()
    public String toString(Map<Property<?>, Comparable<?>> states) {
        StringBuilder stateString = new StringBuilder(BuiltInRegistries.BLOCK.getKey(this.state.getBlock()).toString());

        if (!states.isEmpty()) {
            stateString.append('[');
            stateString.append(states.entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
            stateString.append(']');
        }

        return stateString.toString();
    }

    public Map<String, String> toStates(boolean hideUnspecified) {
        return (hideUnspecified && this.parsedStates != null) ? CraftBlockData.toStates(this.parsedStates) : CraftBlockData.toStates(this.state.getValues());
    }

    private static Map<String, String> toStates(Map<Property<?>, Comparable<?>> states) {
        Map<String, String> compound = new HashMap<>();

        for (Map.Entry<Property<?>, Comparable<?>> entry : states.entrySet()) {
            Property iblockstate = (Property) entry.getKey();

            compound.put(iblockstate.getName(), iblockstate.getName(entry.getValue()));
        }

        return compound;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CraftBlockData && this.state.equals(((CraftBlockData) obj).state);
    }

    @Override
    public int hashCode() {
        return this.state.hashCode();
    }

    protected static BooleanProperty getBoolean(String name) {
        throw new AssertionError("Template Method");
    }

    protected static BooleanProperty getBoolean(String name, boolean optional) {
        throw new AssertionError("Template Method");
    }

    protected static EnumProperty<?> getEnum(String name) {
        throw new AssertionError("Template Method");
    }

    protected static IntegerProperty getInteger(String name) {
        throw new AssertionError("Template Method");
    }

    protected static BooleanProperty getBoolean(Class<? extends Block> block, String name) {
        return (BooleanProperty) CraftBlockData.getState(block, name, false);
    }

    protected static BooleanProperty getBoolean(Class<? extends Block> block, String name, boolean optional) {
        return (BooleanProperty) CraftBlockData.getState(block, name, optional);
    }

    protected static EnumProperty<?> getEnum(Class<? extends Block> block, String name) {
        return (EnumProperty<?>) CraftBlockData.getState(block, name, false);
    }

    protected static IntegerProperty getInteger(Class<? extends Block> block, String name) {
        return (IntegerProperty) CraftBlockData.getState(block, name, false);
    }

    /**
     * Get a specified {@link Property} from a given block's class with a
     * given name
     *
     * @param block the class to retrieve the state from
     * @param name the name of the state to retrieve
     * @param optional if the state can be null
     * @return the specified state or null
     * @throws IllegalStateException if the state is null and {@code optional}
     * is false.
     */
    private static Property<?> getState(Class<? extends Block> block, String name, boolean optional) {
        Property<?> state = null;

        for (Block instance : BuiltInRegistries.BLOCK) {
            if (instance.getClass() == block) {
                if (state == null) {
                    state = instance.getStateDefinition().getProperty(name);
                } else {
                    Property<?> newState = instance.getStateDefinition().getProperty(name);

                    Preconditions.checkState(state == newState, "State mistmatch %s,%s", state, newState);
                }
            }
        }

        Preconditions.checkState(optional || state != null, "Null state for %s,%s", block, name);

        return state;
    }

    public static final BlockFace[] ROTATION_CYCLE = {
        BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST_SOUTH_WEST,
        BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST,
        BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST,
        BlockFace.EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST
    };

    private static final Map<Class<? extends Block>, Function<net.minecraft.world.level.block.state.BlockState, CraftBlockData>> MAP = new HashMap<>();

    static {
        //<editor-fold desc="CraftBlockData Registration" defaultstate="collapsed">
        // Paper start - Generated/CraftBlockData#MAP
        // Paper end - Generated/CraftBlockData#MAP
        //</editor-fold>
    }

    private static void register(Class<? extends Block> nms, Function<net.minecraft.world.level.block.state.BlockState, CraftBlockData> bukkit) {
        Preconditions.checkState(CraftBlockData.MAP.put(nms, bukkit) == null, "Duplicate mapping %s->%s", nms, bukkit);
    }

    // Paper start - cache block data strings
    private static Map<String, CraftBlockData> stringDataCache = new java.util.concurrent.ConcurrentHashMap<>();

    static {
        // cache all of the default states at startup, will not cache ones with the custom states inside of the
        // brackets in a different order, though
        reloadCache();
    }

    public static void reloadCache() {
        stringDataCache.clear();
        Block.BLOCK_STATE_REGISTRY.forEach(blockData -> stringDataCache.put(blockData.toString(), blockData.createCraftBlockData()));
    }
    // Paper end - cache block data strings

    public static CraftBlockData newData(BlockType blockType, String data) {

        // Paper start - cache block data strings
        if (blockType != null) {
            Block block = CraftBlockType.bukkitToMinecraftNew(blockType);
            if (block != null) {
                net.minecraft.resources.ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
                data = data == null ? key.toString() : key + data;
            }
        }

        CraftBlockData cached = stringDataCache.computeIfAbsent(data, s -> createNewData(null, s));
        return (CraftBlockData) cached.clone();
    }

    private static CraftBlockData createNewData(BlockType blockType, String data) {
        // Paper end - cache block data strings
        net.minecraft.world.level.block.state.BlockState blockData;
        Block block = blockType == null ? null : ((CraftBlockType<?>) blockType).getHandle();
        Map<Property<?>, Comparable<?>> parsed = null;

        // Data provided, use it
        if (data != null) {
            try {
                // Material provided, force that material in
                if (block != null) {
                    data = BuiltInRegistries.BLOCK.getKey(block) + data;
                }

                StringReader reader = new StringReader(data);
                BlockStateParser.BlockResult arg = BlockStateParser.parseForBlock(CraftRegistry.getMinecraftRegistry(Registries.BLOCK), reader, false);
                Preconditions.checkArgument(!reader.canRead(), "Spurious trailing data: " + data);

                blockData = arg.blockState();
                parsed = arg.properties();
            } catch (CommandSyntaxException ex) {
                throw new IllegalArgumentException("Could not parse data: " + data, ex);
            }
        } else {
            blockData = block.defaultBlockState();
        }

        CraftBlockData craft = CraftBlockData.fromData(blockData);
        craft.parsedStates = parsed;
        return craft;
    }

    // Paper start - optimize creating BlockData to not need a map lookup
    static {
        // Initialize cached data for all IBlockData instances after registration
        Block.BLOCK_STATE_REGISTRY.iterator().forEachRemaining(net.minecraft.world.level.block.state.BlockState::createCraftBlockData);
    }
    public static CraftBlockData fromData(net.minecraft.world.level.block.state.BlockState data) {
        return data.createCraftBlockData();
    }

    public static CraftBlockData createData(net.minecraft.world.level.block.state.BlockState data) {
        // Paper end
        return CraftBlockData.MAP.getOrDefault(data.getBlock().getClass(), CraftBlockData::new).apply(data);
    }

    @Override
    public SoundGroup getSoundGroup() {
        return CraftSoundGroup.getSoundGroup(this.state.getSoundType());
    }

    @Override
    public int getLightEmission() {
        return this.state.getLightEmission();
    }

    @Override
    public boolean isOccluding() {
        return this.state.canOcclude();
    }

    @Override
    public boolean requiresCorrectToolForDrops() {
        return this.state.requiresCorrectToolForDrops();
    }

    @Override
    public boolean isPreferredTool(ItemStack tool) {
        Preconditions.checkArgument(tool != null, "tool must not be null");

        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(tool);
        return CraftBlockData.isPreferredTool(this.state, nms);
    }

    public static boolean isPreferredTool(net.minecraft.world.level.block.state.BlockState iblockdata, net.minecraft.world.item.ItemStack nmsItem) {
        return !iblockdata.requiresCorrectToolForDrops() || nmsItem.isCorrectToolForDrops(iblockdata);
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return PistonMoveReaction.getById(this.state.getPistonPushReaction().ordinal());
    }

    @Override
    public boolean isSupported(org.bukkit.block.Block block) {
        Preconditions.checkArgument(block != null, "block must not be null");

        CraftBlock craftBlock = (CraftBlock) block;
        return this.state.canSurvive(craftBlock.getCraftWorld().getHandle(), craftBlock.getPosition());
    }

    @Override
    public boolean isSupported(Location location) {
        Preconditions.checkArgument(location != null, "location must not be null");

        CraftWorld world = (CraftWorld) location.getWorld();
        Preconditions.checkArgument(world != null, "location must not have a null world");

        BlockPos position = CraftLocation.toBlockPosition(location);
        return this.state.canSurvive(world.getHandle(), position);
    }

    @Override
    public boolean isFaceSturdy(BlockFace face, BlockSupport support) {
        Preconditions.checkArgument(face != null, "face must not be null");
        Preconditions.checkArgument(support != null, "support must not be null");

        return this.state.isFaceSturdy(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CraftBlock.blockFaceToNotch(face), CraftBlockSupport.toNMS(support));
    }

    // Paper start
    @Override
    public org.bukkit.util.VoxelShape getCollisionShape(Location location) {
        Preconditions.checkArgument(location != null, "location must not be null");

        CraftWorld world = (CraftWorld) location.getWorld();
        Preconditions.checkArgument(world != null, "location must not have a null world");

        BlockPos position = CraftLocation.toBlockPosition(location);
        net.minecraft.world.phys.shapes.VoxelShape shape = this.state.getCollisionShape(world.getHandle(), position);
        return new org.bukkit.craftbukkit.util.CraftVoxelShape(shape);
    }
    // Paper end

    @Override
    public Color getMapColor() {
        return Color.fromRGB(this.state.getMapColor(null, null).col);
    }

    @Override
    public Material getPlacementMaterial() {
        return CraftItemType.minecraftToBukkit(this.state.getBlock().asItem());
    }

    @Override
    public void rotate(StructureRotation rotation) {
        this.state = this.state.rotate(Rotation.valueOf(rotation.name()));
    }

    @Override
    public void mirror(Mirror mirror) {
        this.state = this.state.mirror(net.minecraft.world.level.block.Mirror.valueOf(mirror.name()));
    }

    @Override
    public void copyTo(BlockData blockData) {
        CraftBlockData other = (CraftBlockData) blockData;
        net.minecraft.world.level.block.state.BlockState nms = other.state;
        for (Property<?> property : this.state.getBlock().getStateDefinition().getProperties()) {
            if (nms.hasProperty(property)) {
                nms = this.copyProperty(this.state, nms, property);
            }
        }

        other.state = nms;
    }

    private <T extends Comparable<T>> net.minecraft.world.level.block.state.BlockState copyProperty(net.minecraft.world.level.block.state.BlockState source, net.minecraft.world.level.block.state.BlockState target, Property<T> property) {
        return target.setValue(property, source.getValue(property));
    }

    @NotNull
    @Override
    public BlockState createBlockState() {
        return CraftBlockStates.getBlockState(this.state, null);
    }

    // Paper start - destroy speed API
    @Override
    public float getDestroySpeed(final ItemStack itemStack, final boolean considerEnchants) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.unwrap(itemStack);
        float speed = nmsItemStack.getDestroySpeed(this.state);
        if (speed > 1.0F && considerEnchants) {
            final net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute = net.minecraft.world.entity.ai.attributes.Attributes.MINING_EFFICIENCY;
            // Logic sourced from AttributeInstance#calculateValue
            final double initialBaseValue = attribute.value().getDefaultValue();
            final org.apache.commons.lang3.mutable.MutableDouble modifiedBaseValue = new org.apache.commons.lang3.mutable.MutableDouble(initialBaseValue);
            final org.apache.commons.lang3.mutable.MutableDouble baseValMul = new org.apache.commons.lang3.mutable.MutableDouble(1);
            final org.apache.commons.lang3.mutable.MutableDouble totalValMul = new org.apache.commons.lang3.mutable.MutableDouble(1);

            net.minecraft.world.item.enchantment.EnchantmentHelper.forEachModifier(
                nmsItemStack, net.minecraft.world.entity.EquipmentSlot.MAINHAND, (attributeHolder, attributeModifier) -> {
                    switch (attributeModifier.operation()) {
                        case ADD_VALUE -> modifiedBaseValue.add(attributeModifier.amount());
                        case ADD_MULTIPLIED_BASE -> baseValMul.add(attributeModifier.amount());
                        case ADD_MULTIPLIED_TOTAL -> totalValMul.setValue(totalValMul.doubleValue() * (1D + attributeModifier.amount()));
                    }
                }
            );

            final double actualModifier = modifiedBaseValue.doubleValue() * baseValMul.doubleValue() * totalValMul.doubleValue();

            speed += (float) attribute.value().sanitizeValue(actualModifier);
        }
        return speed;
    }
    // Paper end - destroy speed API

    // Paper start - Block tick API
    @Override
    public boolean isRandomlyTicked() {
        return this.state.isRandomlyTicking();
    }
    // Paper end - Block tick API
}
