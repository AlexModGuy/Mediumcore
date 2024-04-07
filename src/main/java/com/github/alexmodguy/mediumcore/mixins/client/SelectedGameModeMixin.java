package com.github.alexmodguy.mediumcore.mixins.client;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(WorldCreationUiState.SelectedGameMode.class)
@Unique
public class SelectedGameModeMixin {

    @Shadow
    @Final
    @Mutable
    private static WorldCreationUiState.SelectedGameMode[] $VALUES;

    private static final WorldCreationUiState.SelectedGameMode MEDIUMCORE = mediumcore_addGameMode("MEDIUMCORE", "mediumcore", GameType.SURVIVAL);

    @Invoker("<init>")
    public static WorldCreationUiState.SelectedGameMode mediumcore_invokeInit(String internalName, int internalId, String name, GameType gameType) {
        throw new AssertionError();
    }

    private static WorldCreationUiState.SelectedGameMode mediumcore_addGameMode(String internalName, String name, GameType gameType) {
        ArrayList<WorldCreationUiState.SelectedGameMode> variants = new ArrayList<WorldCreationUiState.SelectedGameMode>(Arrays.asList($VALUES));
        WorldCreationUiState.SelectedGameMode instrument = mediumcore_invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, name, gameType);
        variants.add(instrument);
        SelectedGameModeMixin.$VALUES = variants.toArray(new WorldCreationUiState.SelectedGameMode[0]);
        return instrument;
    }

}
