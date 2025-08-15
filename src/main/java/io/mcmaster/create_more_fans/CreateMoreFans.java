package io.mcmaster.create_more_fans;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CreateMoreFans.MODID)
public class CreateMoreFans {
    public static final String MODID = "createmorefans";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateMoreFans(IEventBus modBus) {
        LOGGER.info("Create: More Fans initializing");
    }
}
