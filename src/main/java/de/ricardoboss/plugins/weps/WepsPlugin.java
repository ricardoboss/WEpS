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

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

<<<<<<<HEAD

=======
        >>>>>>>e0cdfe4...Initial code check-in
        package de.ricardoboss.plugins.weps;

public class WepsPlugin extends JavaPlugin {
    private Logger logger;
    private PermissionChecker permissionChecker;

    @Override
    public void onLoad() {
        super.onLoad();

        logger = getLogger();
    }

    public void registerPermissionChecker(PermissionChecker permissionChecker)
    {
        if (logger == null)
            throw new IllegalPluginAccessException("WEpS is not loaded yet! Cannot register custom permission checker.");

        logger.warning("Registering new permission checker: " + permissionChecker.getClass().getName());

        this.permissionChecker = permissionChecker;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (permissionChecker != null)
            return;

        Plugin wgPlugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (wgPlugin != null) {
            permissionChecker = new WorldGuardPermissionChecker();

            logger.info("Enabled WorldGuard integration.");
        }
        else
            permissionChecker = new FallbackPermissionChecker();
    }

    @Override
    public void onDisable() {
        permissionChecker = null;

        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("/s"))
            return false;

        if (!(sender instanceof Player)) {
            sender.sendMessage("This is a player-only command!");

            return true;
        }

        Player player = (Player) sender;

        int i = 0;
        Block block;
        if (args.length >= 4)
        {
            // check if using an absolute position (//s <x> <y> <z> <line> [text])

            int x, y, z;
            try
            {
                // TODO: add support for '~' (relative coordinates)

                x = Integer.parseInt(args[i++]);
                y = Integer.parseInt(args[i++]);
                z = Integer.parseInt(args[i++]);
            }
            catch (NumberFormatException nfe)
            {
                player.sendMessage("Invalid coordinate: " + args[i - 1]);

                return false;
            }

            // TODO: sanitize (check bounds) coordinates!
            block = player.getWorld().getBlockAt(x, y, z);
        }
        else
        {
            // ...or get the block the player is looking at (//s <line> [text])
            block = player.getTargetBlockExact(getServer().getViewDistance());
        }

        if (block == null || !(block.getState() instanceof Sign))
        {
            player.sendMessage("Block is not a sign!");

            return true;
        }

        // get sign data
        Sign sign = (Sign) block.getState();

        // let implementation of permission checker decide whether the player can edit this sign
        if (!permissionChecker.canEditSign(player, sign))
        {
            logger.warning(String.format(
                    "Player %s intended to edit a sign at [%d|%d|%d], but had no permission to do so.",
                    player.getName(),
                    block.getLocation().getBlockX(),
                    block.getLocation().getBlockY(),
                    block.getLocation().getBlockZ()
            ));

            player.sendMessage("You don't have permission to edit this sign!");

            return true;
        }

        int line;
        try
        {
            line = Integer.parseInt(args[i++]);
        }
        catch (NumberFormatException nfe)
        {
            player.sendMessage("Invalid line number: " + args[i - 1]);

            return false;
        }

        // validate line number range
        if (line < 1 || line > 4)
        {
            player.sendMessage("The line number must be between 1 and 4.");

            return false;
        }

        // setLine uses 0-based indices (shift line number from 1-4 to 0-3)
        line--;

        // text is optional
        String text;
        if (args.length == i + 1)
            text = args[i];
        else
            text = "";

        if (!sign.isEditable())
            sign.setEditable(true);

        sign.setLine(line, text);
        sign.update();

        String message = String.format(
                "Player %s edited a sign at [%d|%d|%d].",
                player.getName(),
                block.getLocation().getBlockX(),
                block.getLocation().getBlockY(),
                block.getLocation().getBlockZ()
        );

        logger.info(message);

        player.sendMessage("Sign updated.");

        return true;
    }
}
