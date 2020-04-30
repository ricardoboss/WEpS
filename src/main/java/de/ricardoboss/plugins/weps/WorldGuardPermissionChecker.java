/*
 * MIT License
 *
 * Copyright (c) 2020 Ricardo Boss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.ricardoboss.plugins.weps;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

class WorldGuardPermissionChecker implements PermissionChecker {
    private final WorldGuard wg;
    private final WorldGuardPlugin wgp;
    private final RegionQuery query;

    public WorldGuardPermissionChecker() {
        wg = WorldGuard.getInstance();
        wgp = WorldGuardPlugin.inst();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        query = container.createQuery();
    }

    public boolean canEditSign(Player player, Sign sign) {
        LocalPlayer lp = wgp.wrapPlayer(player);

        if (wg.getPlatform().getSessionManager().hasBypass(lp, lp.getWorld()))
            return true;

        Location signLocation = BukkitAdapter.adapt(sign.getLocation());

        return query.testState(signLocation, lp, Flags.BUILD);
    }

//    private String getPermissionNameForPlacing(Block block)
//    {
//        String prefix = "worldguard.build.block.place.";
//
//        String materialName = block.getType().name().toLowerCase(Locale.ENGLISH);
//
//        return prefix + materialName;
//    }
}
