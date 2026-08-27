# Compatibility Matrix

## Target runtime

- Minecraft: **1.20.1**
- Forge: **47.4.0**
- Java: **17**
- Mappings: official 1.20.1
- Mod ID: `computerstorage`

## Compatibility rules

1. Use Forge capabilities for inventory access (`IItemHandler`) instead of hard-coding vanilla container classes.
2. Keep all world/inventory mutations server-side; client packets are requests only.
3. Treat `BlockPos` and dimension as part of an endpoint identity; never assume the Overworld.
4. Resolve endpoint inventories at execution time so chunk unloads and replaced block entities do not leave stale handlers.
5. Avoid direct dependencies on optional mods in the core runtime.
6. Integrations with other storage/transport mods should be isolated behind capability adapters.
7. Network protocol changes must increment `NetworkConstants.PROTOCOL_VERSION`.
8. Persist only stable identifiers and coordinates; never serialize live Forge capability objects.
9. Recipes should use tags where the ingredient is intentionally interchangeable.
10. CI is pinned to a Gradle version supported by the project's ForgeGradle line.

## MVP compatibility target

The first playable release targets a clean Forge 1.20.1 installation. Vanilla inventories are the reference implementation; third-party inventories are supported when they expose the standard Forge item-handler capability.

## Future adapters

- Applied Energistics 2
- Refined Storage
- Create
- Mekanism

These are planned integration layers, not hard dependencies of the MVP.
