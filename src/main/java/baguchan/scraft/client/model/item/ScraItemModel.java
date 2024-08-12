package baguchan.scraft.client.model.item;

import baguchan.scraft.Scraft;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.*;

public class ScraItemModel implements BakedModel
{
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();

    private final ModelBakery bakery;
    private final BakedModel originalModel;

    public ScraItemModel(ModelBakery bakery, BakedModel originalModel) {
        this.bakery = bakery;
        this.originalModel = Preconditions.checkNotNull(originalModel);
    }

    private final ItemOverrides itemOverrides = new ItemOverrides()
    {
        @Nonnull
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entityIn, int seed) {
            CompoundTag tag = stack.getOrCreateTag();

            if (tag.contains("ScraItem")) {
                ItemStack ingredientStack = ItemStack.of(tag.getCompound("ScraItem"));
                return (BakedModel) ScraItemModel.this.getScraModel(ingredientStack);
            }

            return originalModel;
        }
    };

    @Nonnull
    @Override
    public ItemOverrides getOverrides() {
        return itemOverrides;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) {
        return originalModel.getQuads(state, side, rand);
    }

    @Nonnull
    @Override
    public ItemTransforms getTransforms() {
        return originalModel.getTransforms();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return originalModel.isCustomRenderer();
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        return originalModel.getModelData(level, pos, state, modelData);
    }

    private final HashMap<Item, CompositeBakedModel> cache = new HashMap<>();

    private CompositeBakedModel getScraModel(ItemStack ingredientStack) {
        return cache.computeIfAbsent(ingredientStack.getItem(), p -> new CompositeBakedModel(bakery, ingredientStack, originalModel));
    }

    private class CompositeBakedModel extends WrappedItemModel<BakedModel>
    {
        private final List<BakedQuad> genQuads = new ArrayList<>();
        private final Map<Direction, List<BakedQuad>> faceQuads = new EnumMap<>(Direction.class);

        public CompositeBakedModel(ModelBakery bakery, ItemStack ingredientStack, BakedModel original) {
            super(original);

            ResourceLocation ingredientLocation = ForgeRegistries.ITEMS.getKey(ingredientStack.getItem());
            UnbakedModel ingredientUnbaked = bakery.getModel(new ModelResourceLocation(ingredientLocation, "inventory"));

            ResourceLocation name = new ResourceLocation(Scraft.MODID, "item_with_" + ingredientLocation.toString().replace(':', '_'));

            ModelBaker baker = bakery.new ModelBakerImpl((modelLoc, material) -> material.sprite(), name);

            BakedModel ingredientBaked;
            if (ingredientUnbaked instanceof BlockModel bm && ((BlockModel) ingredientUnbaked).getRootModel() == ModelBakery.GENERATION_MARKER) {
                ModelState transform = new SimpleModelState(
                        new Transformation(
                                new Vector3f(0.5F, 0.5F, 0.0F),
                                Axis.XP.rotationDegrees(0),
                                new Vector3f(1F, 1F, 1F), null));
                ingredientBaked = ITEM_MODEL_GENERATOR
                        .generateBlockModel(Material::sprite, bm)
                        .bake(baker, bm, Material::sprite, transform, name, false);
            } else {
                ModelState transform = new SimpleModelState(
                        new Transformation(
                                new Vector3f(0.5F, 0.5F, 0.0F),
                                Axis.XP.rotationDegrees(0),
                                new Vector3f(0.75F, 0.75F, 0.75F), null));
                ingredientBaked = ingredientUnbaked.bake(baker, Material::sprite, transform, name);
            }

            for (Direction e : Direction.values()) {
                faceQuads.put(e, new ArrayList<>());
            }

            RandomSource rand = RandomSource.create(0);
            for (BakedModel pass : ingredientBaked.getRenderPasses(ingredientStack, false)) {
                genQuads.addAll(pass.getQuads(null, null, rand, ModelData.EMPTY, null));

                for (Direction e : Direction.values()) {
                    rand.setSeed(0);
                    faceQuads.get(e).addAll(pass.getQuads(null, e, rand, ModelData.EMPTY, null));
                }
            }
        }
        @Override
        public boolean isCustomRenderer() {
            return originalModel.isCustomRenderer();
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, @Nonnull RandomSource rand) {
            List<BakedQuad> list = Lists.newArrayList();

            list.addAll(ScraItemModel.this.originalModel.getQuads(state, face, rand));
            list.addAll(face == null ? genQuads : faceQuads.get(face));
            return list;
        }

        @Override
        public BakedModel applyTransform(@Nonnull ItemDisplayContext cameraTransformType, PoseStack stack, boolean leftHand) {
            super.applyTransform(cameraTransformType, stack, leftHand);
            return this;
        }
    }
}