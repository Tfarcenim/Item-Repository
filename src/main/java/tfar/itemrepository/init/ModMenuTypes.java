package tfar.itemrepository.init;

import net.minecraft.world.inventory.MenuType;
import tfar.itemrepository.RepositoryMenu;

public class ModMenuTypes {
    public static final MenuType<RepositoryMenu> REPOSITORY = new MenuType<>(RepositoryMenu::new);
}
