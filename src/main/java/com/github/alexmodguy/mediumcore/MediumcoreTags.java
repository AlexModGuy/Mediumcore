package com.github.alexmodguy.mediumcore;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class MediumcoreTags {
    public static final TagKey<Item> RESTORES_MAX_HEALTH = TagKey.create(Registries.ITEM, new ResourceLocation("mediumcore:restores_max_health"));

}
