package live.blackninja.smp.manger.addon;

import live.blackninja.smp.Core;

import java.util.HashMap;

public class AddonManger {

    private Core core;

    private HashMap<Addons, Addon> addons = new HashMap<>();

    public AddonManger(Core core) {
        this.core = core;
    }

    public void registerAddon(Addons addon, Addon addonClass) {
        addons.put(addon, addonClass);
    }

    public HashMap<Addons, Addon> getAddons() {
        return addons;
    }
}
