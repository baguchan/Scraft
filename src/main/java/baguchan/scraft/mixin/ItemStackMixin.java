package baguchan.scraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    private CompoundTag tag;

    @Shadow public abstract <T extends LivingEntity> void hurtAndBreak(int p_41623_, T p_41624_, Consumer<T> p_41625_);

    @Inject(method = "hurtEnemy", at = @At("TAIL"), cancellable = true)
    public void hurtEnemy(LivingEntity p_41641_, Player p_41642_, CallbackInfo ci) {
        if (tag != null && tag.contains("ScraItem")) {

            hurtAndBreak(1, p_41641_, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
    }

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> cir){
        if(tag != null && tag.contains("ScraItem")) {
            ItemStack ingredientStack = ItemStack.of(tag.getCompound("ScraItem"));

            cir.setReturnValue(cir.getReturnValue() + ingredientStack.getMaxDamage() + 10);
        }
    }

    @Inject(method = "isDamageableItem", at = @At("HEAD"), cancellable = true)
    public void isDamageableItem(CallbackInfoReturnable<Boolean> cir) {
        if(tag != null && tag.contains("ScraItem")) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
    public void getMaxStackSize(CallbackInfoReturnable<Integer> cir){
        if(tag != null && tag.contains("ScraItem")) {
            cir.setReturnValue(1);
        }
    }
}
