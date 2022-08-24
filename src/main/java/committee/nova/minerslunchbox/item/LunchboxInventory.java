package committee.nova.minerslunchbox.item;

import com.redgrapefruit.itemnbt3.CustomData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

public class LunchboxInventory implements CustomData {
    private final DefaultedList<ItemStack> stacks;

    public static final int INVENTORY_LIMIT = 128;

    public LunchboxInventory() {
        this.stacks = DefaultedList.ofSize(INVENTORY_LIMIT);
    }

    public DefaultedList<ItemStack> getStacks() {
        return stacks;
    }

    public void addStack(ItemStack stack) {
        this.stacks.add(stack);
    }

    public void setStack(int index, ItemStack stack) {
        stacks.set(index, stack);
    }

    @Override
    public @NotNull String getNbtCategory() {
        return "Items";
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        NbtList nbtList = nbt.getList("Items", 10);
        stacks.clear();
        nbtList.forEach(nbtElement -> stacks.add(ItemStack.fromNbt((NbtCompound) nbtElement)));
    }

    @Override
    public void writeNbt(@NotNull NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (ItemStack itemStack : stacks) {
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                itemStack.writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }
        nbt.put("Items", nbtList);
    }
}
