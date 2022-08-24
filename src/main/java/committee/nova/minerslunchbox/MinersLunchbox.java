package committee.nova.minerslunchbox;

import committee.nova.minerslunchbox.item.LunchboxItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinersLunchbox implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Miner's Lunchbox");
	public static final LunchboxItem LUNCHBOX = new LunchboxItem(new Item.Settings().food(new FoodComponent.Builder().build()).maxCount(1).group(ItemGroup.TOOLS));

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("minerslunchbox", "lunchbox"), LUNCHBOX);
		LOGGER.info("Initialized.");
	}
}
