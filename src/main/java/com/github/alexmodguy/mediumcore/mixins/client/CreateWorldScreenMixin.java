package com.github.alexmodguy.mediumcore.mixins.client;

import com.github.alexmodguy.mediumcore.GameRuleRegistry;
import com.github.alexmodguy.mediumcore.Mediumcore;
import com.github.alexmodguy.mediumcore.client.ClientMediumcoreUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @Shadow @Nullable private TabNavigationBar tabNavigationBar;

    @Nullable
    private CycleButton modifiedCycleButton;

    @Inject(
            method = {"Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;init()V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void mediumcore_init(CallbackInfo ci) {
        if(this.tabNavigationBar != null){
            for(Tab tab : this.tabNavigationBar.tabs){
                if(tab instanceof CreateWorldScreen.GameTab gameTab){
                    gameTab.visitChildren(this::mediumcore_processWidget);
                }
            }
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;createLevelSettings(Z)Lnet/minecraft/world/level/LevelSettings;"},
            remap = true,
            cancellable = true,
            at = @At(value = "RETURN")
    )
    public void mediumcore_createLevelSettings(boolean debug, CallbackInfoReturnable<LevelSettings> cir) {
        if(modifiedCycleButton != null && modifiedCycleButton.getValue() == ClientMediumcoreUtil.getMediumcoreGameMode()){
            GameRules gameRules = cir.getReturnValue().gameRules;
            GameRuleRegistry.setMediumcoreMode(gameRules, true, null);
            Mediumcore.LOGGER.info("Set mediumcoreMode config by default to true on world creation");
            cir.setReturnValue(cir.getReturnValue());
        }
    }

    private void mediumcore_processWidget(AbstractWidget widget) {
        if(widget instanceof CycleButton cycleButton && cycleButton.getValue() instanceof WorldCreationUiState.SelectedGameMode){ //for cycle buttons pertaining to game mode
            if(!cycleButton.values.getDefaultList().contains(ClientMediumcoreUtil.getMediumcoreGameMode()) || !cycleButton.values.getSelectedList().contains(ClientMediumcoreUtil.getMediumcoreGameMode())){
                List<WorldCreationUiState.SelectedGameMode> expandedVanillaList = new ArrayList<>();
                expandedVanillaList.addAll(cycleButton.values.getDefaultList());
                expandedVanillaList.add(ClientMediumcoreUtil.getMediumcoreGameMode());
                cycleButton.values = CycleButton.ValueListSupplier.create(expandedVanillaList);
                if(Mediumcore.CONFIG.mediumcoreDefaultGameMode.get()) {
                    cycleButton.setValue(ClientMediumcoreUtil.getMediumcoreGameMode());
                    //resets label
                    cycleButton.cycleValue(0);
                }
                modifiedCycleButton = cycleButton;
            }
        }
    }
}
