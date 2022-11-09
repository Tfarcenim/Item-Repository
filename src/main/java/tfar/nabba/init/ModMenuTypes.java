package tfar.nabba.init;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkHooks;
import tfar.nabba.menu.AntiBarrelMenu;
import tfar.nabba.menu.VanityKeyMenu;

public class ModMenuTypes {
    public static final MenuType<AntiBarrelMenu> ANTI_BARREL = new MenuType<>(AntiBarrelMenu::new);
    public static final MenuType<VanityKeyMenu> VANITY_KEY = IForgeMenuType.create((p,s,a) -> new VanityKeyMenu(p,s,a.readBlockPos()));
}
