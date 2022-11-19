package tfar.nabba.init;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import tfar.nabba.menu.*;

public class ModMenuTypes {
    public static final MenuType<AntiBarrelMenu> ANTI_BARREL = new MenuType<>(AntiBarrelMenu::new);
    public static final MenuType<VanityKeyMenu> VANITY_KEY = IForgeMenuType.create((p,s,a) -> new VanityKeyMenu(p,s,a.readBlockPos()));
    public static final MenuType<ControllerKeyItemMenu> ITEM_CONTROLLER_KEY = new MenuType<>(ControllerKeyItemMenu::new);
    public static final MenuType<ControllerKeyFluidMenu> FLUID_CONTROLLER_KEY = new MenuType<>(ControllerKeyFluidMenu::new);
    public static final MenuType<BarrelInterfaceMenu> BARREL_INTERFACE = new MenuType<>(BarrelInterfaceMenu::new);

}
