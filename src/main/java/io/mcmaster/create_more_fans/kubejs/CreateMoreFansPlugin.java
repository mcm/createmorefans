package io.mcmaster.create_more_fans.kubejs;

import com.simibubi.create.api.registry.CreateRegistries;

import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.script.ScriptType;
import io.mcmaster.create_more_fans.kubejs.events.CreateMoreFansEvents;
import io.mcmaster.create_more_fans.kubejs.events.CreateMoreFansRegistryEvent;

public class CreateMoreFansPlugin implements KubeJSPlugin {
    // @Override
    // public void registerBuilderTypes(BuilderTypeRegistry registry) {
    // CreateMoreFans.LOGGER.info("Registering Create fan processing type builder...");
    // registry.addDefault(CreateRegistries.FAN_PROCESSING_TYPE, KubeFanProcessingTypeBuilder.class,
    // KubeFanProcessingTypeBuilder::new);
    // }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(CreateMoreFansEvents.GROUP);
    }

    @Override
    public void initStartup() {
        CreateMoreFansRegistryEvent event = new CreateMoreFansRegistryEvent();
        CreateMoreFansEvents.REGISTRY.post(ScriptType.STARTUP, event);

        event.created.forEach((builder) -> {
            addBuilder(builder);
            addBuilder(builder.getSerializerBuilder());
            addBuilder(builder.getRecipeTypeBuilder());
        });
    }

    private <T> void addBuilder(BuilderBase<T> builder) {
        RegistryObjectStorage<T> storage = RegistryObjectStorage.of(builder.registryKey);
        if (storage.objects.containsKey(builder.id)) {
            throw new IllegalArgumentException("Duplicate key '" + builder.id + "' in registry '"
                    + CreateRegistries.FAN_PROCESSING_TYPE.location() + "'!");
        }
        storage.objects.put(builder.id, (BuilderBase<T>) builder);
        RegistryObjectStorage.ALL_BUILDERS.add(builder);
    }
}
