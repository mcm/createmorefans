package io.mcmaster.create_more_fans.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import io.mcmaster.create_more_fans.kubejs.events.CreateMoreFansEvents;
import io.mcmaster.create_more_fans.kubejs.events.CreateMoreFansRegistryEvent;

public class CreateMoreFansPlugin extends KubeJSPlugin {
    @Override
    public void registerEvents() {
        CreateMoreFansEvents.GROUP.register();
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addBuilder(BuilderBase<?> builder) {
        // Throws on duplicate ids, and adds the builder to RegistryInfo.ALL_BUILDERS
        RegistryInfo info = builder.getRegistryType();
        info.addBuilder(builder);
    }
}
