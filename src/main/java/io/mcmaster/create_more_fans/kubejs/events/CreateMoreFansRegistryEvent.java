package io.mcmaster.create_more_fans.kubejs.events;

import java.util.LinkedList;
import java.util.List;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import io.mcmaster.create_more_fans.kubejs.KubeFanProcessingTypeBuilder;

public class CreateMoreFansRegistryEvent extends StartupEventJS {
    public final List<KubeFanProcessingTypeBuilder> created;

    public CreateMoreFansRegistryEvent() {
        this.created = new LinkedList<>();
    }

    public KubeFanProcessingTypeBuilder create(String id) {
        KubeFanProcessingTypeBuilder builder = new KubeFanProcessingTypeBuilder(
                UtilsJS.getMCID(ScriptType.STARTUP.manager.get().context, KubeJS.appendModId(id)));
        created.add(builder);
        return builder;
    }
}
