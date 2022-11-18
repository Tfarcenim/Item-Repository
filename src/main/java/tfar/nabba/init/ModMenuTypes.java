package tfar.nabba.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import tfar.nabba.menu.AntiBarrelMenu;
import tfar.nabba.menu.ControllerKeyFluidMenu;
import tfar.nabba.menu.ControllerKeyItemMenu;
import tfar.nabba.menu.VanityKeyMenu;

public class ModMenuTypes {
    public static final MenuType<AntiBarrelMenu> ANTI_BARREL = new MenuType<>(AntiBarrelMenu::new);
    public static final MenuType<VanityKeyMenu> VANITY_KEY = IForgeMenuType.create((p,s,a) -> new VanityKeyMenu(p,s,a.readBlockPos()));
    public static final MenuType<ControllerKeyItemMenu> ITEM_CONTROLLER_KEY = new MenuType<>(ControllerKeyItemMenu::new);
    public static final MenuType<ControllerKeyFluidMenu> FLUID_CONTROLLER_KEY = new MenuType<>(ControllerKeyFluidMenu::new);


}
