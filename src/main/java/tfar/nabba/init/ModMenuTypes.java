package tfar.nabba.init;

import net.minecraft.world.inventory.MenuType;
import tfar.nabba.RepositoryMenu;

public class ModMenuTypes {
    public static final MenuType<RepositoryMenu> REPOSITORY = new MenuType<>(RepositoryMenu::new);
}
