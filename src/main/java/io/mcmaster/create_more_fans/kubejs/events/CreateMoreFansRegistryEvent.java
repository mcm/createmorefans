package io.mcmaster.create_more_fans.kubejs.events;

import java.util.LinkedList;
import java.util.List;

import com.simibubi.create.api.registry.CreateRegistries;

import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.rhino.Context;
import io.mcmaster.create_more_fans.kubejs.KubeFanProcessingTypeBuilder;

public class CreateMoreFansRegistryEvent implements KubeStartupEvent {
    public final List<KubeFanProcessingTypeBuilder> created;

    public CreateMoreFansRegistryEvent() {
        this.created = new LinkedList<>();
    }

    public KubeFanProcessingTypeBuilder create(Context cx, KubeResourceLocation id) {
        SourceLine sourceLine = SourceLine.of(cx);
        KubeFanProcessingTypeBuilder builder = new KubeFanProcessingTypeBuilder(id.wrapped());
        builder.sourceLine = sourceLine;
        builder.registryKey = CreateRegistries.FAN_PROCESSING_TYPE;
        created.add(builder);
        return builder;
    }
}
