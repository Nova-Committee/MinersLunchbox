package committee.nova.minerslunchbox;

import com.redgrapefruit.itemnbt3.DataClient;
import committee.nova.minerslunchbox.item.LunchboxInventory;
import committee.nova.minerslunchbox.item.LunchboxItem;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.util.Identifier;

import java.util.concurrent.atomic.AtomicReference;

public class MinersLunchboxClient implements ClientModInitializer {
    private static final UnclampedModelPredicateProvider MODEL_PREDICATE_PROVIDER =  ((stack, world, entity, seed) -> {
        if (!(stack.getItem() instanceof LunchboxItem lunchbox)) return 0.0f;
        AtomicReference<Float> ret = new AtomicReference<>(0.0f);
        DataClient.use(lunchbox::getInventory, stack, (data) -> {
            if (!data.getStacks().isEmpty()) {
                ret.set(0.0000001f);
            } else {
                ret.set(0.0f);
            }
        });
        return ret.get();
    });

    @Override
    public void onInitializeClient() {
        ModelPredicateProviderRegistry.register(MinersLunchbox.LUNCHBOX, new Identifier("contains_food"), MODEL_PREDICATE_PROVIDER);
        ModelPredicateProviderRegistry.register(MinersLunchbox.GOLDEN_LUNCHBOX, new Identifier("contains_food"), MODEL_PREDICATE_PROVIDER);
    }
}
