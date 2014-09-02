package ru.gtncraft.chatter;

import net.md_5.bungee.api.plugin.Plugin;

public class Chatter extends Plugin {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new Listeners());
    }
}
