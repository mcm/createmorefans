package io.mcmaster.create_more_fans.kubejs.events;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface CreateMoreFansEvents {
    EventGroup GROUP = EventGroup.of("CreateMoreFansEvents");

    EventHandler REGISTRY = GROUP.startup("registry", () -> CreateMoreFansRegistryEvent.class);
}
