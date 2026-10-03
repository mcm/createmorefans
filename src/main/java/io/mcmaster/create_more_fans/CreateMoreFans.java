package io.mcmaster.create_more_fans;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.common.Mod;

@Mod(CreateMoreFans.MODID)
public class CreateMoreFans {
    public static final String MODID = "createmorefans";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateMoreFans() {
        LOGGER.info("Create: More Fans initializing");
    }
}
