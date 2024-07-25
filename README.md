# Inspiration
This is a Minecraft Fabric mod for 1.21 inspired by the Minecraft SMP "Naked and Afraid". This mod introduces several new mechanics designed to test your skillset in minecraft.

# Setup:
To have compatibility with existing Minecraft worlds, This mod add a new gamerule "nakedAndAfraid". It takes in a boolean value (fancy word for true or false).

Run the following command to enable the mod in existing worlds.
```
gamerule nakedAndAfraid true
```

**For new worlds:**

Go into More Section -> Game Rules -> Scroll down to Miscellaneous -> Enable Naked And Afraid

# Features:
- **Server Side**: You do not need to install this mod on the client. However, some features are dependent on the client as well.
- **No Armor**: You will take damage while wearing any armor.
- **No F5 or peeking**: You can no longer peek around corners using F5 (change perspective button)*
- **No F3 and coordinates**: Enables gamerule `reducedDebugInfo` (yes that is a vanilla gamerule).
- **Disable Totem of Undying**: Totem of Undying is overpowered so I removed it.
- **Disable all chat messages**: Prevents all in-game chat messages.
- **Disable PvP**: You cannot kill other players like in the original 
SMP.
- **Survival Compatibility**: Works well not just for hardcore mode, but normal survival as well.

# Work In Progress:
- Add config for every settings
- Disable change perspective from server-side only (don't know if it's possible)
- Disable coordinates even for client-side mods like minimap etc. (don't know if it's possible)