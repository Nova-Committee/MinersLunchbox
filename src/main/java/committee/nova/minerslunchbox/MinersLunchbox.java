package committee.nova.minerslunchbox;

import committee.nova.minerslunchbox.item.LunchboxItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinersLunchbox implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Miner's Lunchbox");
	public static final LunchboxItem LUNCHBOX = new LunchboxItem(new Item.Settings().food(new FoodComponent.Builder().build()).maxCount(1));

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("minerslunchbox", "lunchbox"), LUNCHBOX);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(LUNCHBOX));
		LOGGER.info("Initialized.");
	}
}
