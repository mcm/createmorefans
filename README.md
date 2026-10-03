
# Create: More Fans

A utility library for creating new types of processing recipes for Create's fans, similar to splashing and haunting recipes.

## Overview

This mod provides a KubeJS API to create custom fan processing types without needing to directly modify or extend Create's code. The mod handles all the integration with Create's fan processing system, recipe management, and JEI/EMI compatibility.

## Usage

This branch targets **Minecraft 1.20.1 / Forge** (Create 6.0.6+, KubeJS 2001).

A new `CreateMoreFansEvents` is available in startup scripts. 

```
const $ParticleTypes = Java.loadClass(
  "net.minecraft.core.particles.ParticleTypes"
);

CreateMoreFansEvents.registry((event) => {
  event
    .create(`create_more_fans:test`)
    .setCatalystBlockTag(`minecraft:leaves`)
    .setProcessingParticles($ParticleTypes.CHERRY_LEAVES)
    .displayName("Bulk Testing")
    .setJeiCategoryDisplayItem(`minecraft:cherry_leaves`)
    .setJeiAttachedBlock(`minecraft:oak_leaves`);
});
```

`setCatalystBlockTag` and `setCatalystFluidTag` accept a tag id with or without a leading `#`. `setProcessingParticles` accepts a particle type (as above) or a particle id string such as `"minecraft:cherry_leaves"`.

**Note**: If a namespace isn't specified it will default to KubeJS, which may cause recipes not to display in EMI.