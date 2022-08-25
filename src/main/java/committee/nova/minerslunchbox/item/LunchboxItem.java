package committee.nova.minerslunchbox.item;

import com.redgrapefruit.itemnbt3.DataClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LunchboxItem extends Item {
    private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.4F, 0.4F, 1.0F);

    public LunchboxItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        AtomicBoolean ret = new AtomicBoolean(false);
        DataClient.use(LunchboxInventory::new, stack, (data) -> {
            Random random = new Random();
            if (clickType == ClickType.RIGHT) {
                ItemStack slotStack = slot.getStack();
                if (slotStack.isEmpty()) {
                    if (!data.getStacks().isEmpty()) {
                        int randomIndex = random.nextInt(data.getStacks().size());
                        ItemStack removedStack = data.getStacks().get(randomIndex).copy();
                        if (!removedStack.isEmpty()) {
                            this.playRemoveOneSound(player);
                            removedStack.setCount(1);
                            slot.setStack(removedStack);
                            data.getStacks().get(randomIndex).decrement(1);
                            ret.set(true);
                        }
                    }
                } else if (slotStack.isFood() && !(slotStack.getItem() instanceof LunchboxItem)) {
                    int inserted = Math.min(LunchboxInventory.INVENTORY_LIMIT - data.getStacks().size(), slotStack.getCount());
                    if (inserted > 0) {
                        this.playInsertSound(player);
                        for (int i = 0; i < inserted; i++) {
                            ItemStack processStack = slotStack.copy();
                            processStack.setCount(1);
                            data.addStack(processStack);
                        }
                        slot.getStack().decrement(inserted);
                        ret.set(true);
                    }
                }
            }
        });
        return ret.get();
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        AtomicBoolean ret = new AtomicBoolean(false);
        DataClient.use(LunchboxInventory::new, stack, (data) -> {
            if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
                if (otherStack.isEmpty()) {
                    if (!data.getStacks().isEmpty()) {
                        int randomIndex = new Random().nextInt(data.getStacks().size());
                        ItemStack removedStack = data.getStacks().get(randomIndex).copy();
                        if (!removedStack.isEmpty()) {
                            this.playRemoveOneSound(player);
                            removedStack.setCount(1);
                            if (player.getInventory().getEmptySlot() != -1) {
                                player.giveItemStack(removedStack);
                            } else {
                                player.getWorld().spawnEntity(new ItemEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), removedStack));
                            }
                            data.getStacks().get(randomIndex).decrement(1);
                            ret.set(true);
                        }
                    }
                } else if (otherStack.isFood() && !(otherStack.getItem() instanceof LunchboxItem)) {
                    int inserted = Math.min(LunchboxInventory.INVENTORY_LIMIT - data.getStacks().size(), otherStack.getCount());
                    if (inserted > 0) {
                        this.playInsertSound(player);
                        for (int i = 0; i < inserted; i++) {
                            ItemStack processStack = otherStack.copy();
                            processStack.setCount(1);
                            data.addStack(processStack);
                        }
                        otherStack.decrement(inserted);
                        ret.set(true);
                    }
                }
            }
        });
        return ret.get();
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            if (player.isSneaking()) {
                if (!player.isCreative() && !player.isSpectator()) eatUntilFull(player, world, stack);
            } else {
                DataClient.use(LunchboxInventory::new, stack, (data) -> {
                    int eat = new Random().nextInt(data.getStacks().size());
                    ItemStack stackToEat = data.getStacks().get(eat);
                    if (!stackToEat.isEmpty()) {
                        if (stackToEat.isFood()) {
                            data.setStack(eat, user.eatFood(world, stackToEat));
                        } else {
                            world.spawnEntity(new ItemEntity(world, user.getX(), user.getY(), user.getZ(), stackToEat));
                        }
                    }
                });
            }
        }
        return stack;
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        AtomicReference<Optional<TooltipData>> tooltipData = new AtomicReference<>(Optional.empty());
        DataClient.use(LunchboxInventory::new, stack, (data) -> {
            if (Screen.hasShiftDown()) {
                tooltipData.set(Optional.of(new BundleTooltipData(data.getStacks(), data.getStacks().size())));
            }
        });
        return tooltipData.get();
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        AtomicBoolean ret = new AtomicBoolean(false);
        DataClient.use(LunchboxInventory::new, stack, (data) -> {
            if (!data.getStacks().isEmpty()) ret.set(true);
        });
        return ret.get();
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        AtomicInteger ret = new AtomicInteger(0);
        DataClient.use(LunchboxInventory::new, stack, (data) -> {
            if (!data.getStacks().isEmpty()) ret.set(data.getStacks().size());
        });
        return Math.min(1 + 12 * ret.get() / LunchboxInventory.INVENTORY_LIMIT, 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        DataClient.use(LunchboxInventory::new, stack, (data) -> {
            if (!Screen.hasShiftDown()) {
                int i = 0;
                int j = 0;
                for (ItemStack itemStack : data.getStacks()) {
                    if (!itemStack.isEmpty()) {
                        ++j;
                        if (i <= 5) {
                            ++i;
                            MutableText mutableText = itemStack.getName().copy();
                            tooltip.add(mutableText);
                        }
                    }
                }
                if (j - i > 0) {
                    tooltip.add(Text.translatable("tooltip.minerslunchbox.lunchbox.more", j - i).formatted(Formatting.ITALIC, Formatting.GRAY));
                }
                tooltip.add(Text.translatable("tooltip.minerslunchbox.lunchbox.more_info").formatted(Formatting.BLUE));
            }
        });
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        DataClient.use(LunchboxInventory::new, entity.getStack(), (data) ->
                ItemUsage.spawnItemContents(entity, data.getStacks().stream())
        );
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient()) return TypedActionResult.pass(stack);
        AtomicBoolean condition = new AtomicBoolean(false);
        DataClient.use(LunchboxInventory::new, stack, (data) -> {
            condition.set(!data.getStacks().isEmpty());
        });
        if (condition.get()) {
            return super.use(world, user, hand);
        } else {
            return TypedActionResult.pass(stack);
        }
    }

    private static void eatUntilFull(PlayerEntity user, World world, ItemStack lunchboxStack) {
        Random random = new Random();
        while (user.canConsume(false) && !user.getAbilities().invulnerable) {
            DataClient.use(LunchboxInventory::new, lunchboxStack, (data) -> {
                int eat = random.nextInt(data.getStacks().size());
                ItemStack stackToEat = data.getStacks().get(eat);
                if (!stackToEat.isEmpty()) {
                    if (stackToEat.isFood()) {
                        data.setStack(eat, user.eatFood(world, stackToEat));
                    } else {
                        world.spawnEntity(new ItemEntity(world, user.getX(), user.getY(), user.getZ(), stackToEat));
                    }
                }
            });
        }
    }
}
