package com.github.alexmodguy.mediumcore.client;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;

public class ClientMediumcoreUtil {

    public static WorldCreationUiState.SelectedGameMode getMediumcoreGameMode(){
        return WorldCreationUiState.SelectedGameMode.valueOf("MEDIUMCORE");
    }
}
