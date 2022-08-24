package committee.nova.minerslunchbox;

import com.redgrapefruit.itemnbt3.DataClient;
import committee.nova.minerslunchbox.item.LunchboxInventory;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

import java.util.concurrent.atomic.AtomicReference;

public class MinersLunchboxClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelPredicateProviderRegistry.register(MinersLunchbox.LUNCHBOX, new Identifier("contains_food"), ((stack, world, entity, seed) -> {
            AtomicReference<Float> ret = new AtomicReference<>(0.0f);
            DataClient.use(LunchboxInventory::new, stack, (data) -> {
                if (!data.getStacks().isEmpty()) {
                    ret.set(0.0000001f);
                } else {
                    ret.set(0.0f);
                }
            });
            return ret.get();
        }));
    }
}
