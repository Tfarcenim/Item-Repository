package tfar.nabba.init;

import net.minecraft.world.inventory.MenuType;
import tfar.nabba.menu.AntiBarrelMenu;

public class ModMenuTypes {
    public static final MenuType<AntiBarrelMenu> REPOSITORY = new MenuType<>(AntiBarrelMenu::new);
}
