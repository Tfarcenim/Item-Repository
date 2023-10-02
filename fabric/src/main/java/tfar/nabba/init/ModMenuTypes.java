package tfar.nabba.init;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import tfar.nabba.menu.*;

public class ModMenuTypes {
    public static final MenuType<AntiBarrelMenu> ANTI_BARREL = new MenuType<>(AntiBarrelMenu::new, FeatureFlags.VANILLA_SET);
    public static final ExtendedScreenHandlerType<VanityKeyMenu> VANITY_KEY = new ExtendedScreenHandlerType<>((p, s, a) -> new VanityKeyMenu(p,s,a.readBlockPos()));
    public static final MenuType<ControllerKeyItemMenu> ITEM_CONTROLLER_KEY = new MenuType<>(ControllerKeyItemMenu::new, FeatureFlags.VANILLA_SET);
    public static final MenuType<ControllerKeyFluidMenu> FLUID_CONTROLLER_KEY = new MenuType<>(ControllerKeyFluidMenu::new, FeatureFlags.VANILLA_SET);
    public static final MenuType<BarrelInterfaceMenu> BARREL_INTERFACE = new MenuType<>(BarrelInterfaceMenu::new, FeatureFlags.VANILLA_SET);

}
