package dev.lanoxkodo;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DispenserBucketLogic implements Listener {

	public DispenserBucketLogic() {}
	
	@EventHandler
	public void dispenseHandler(BlockDispenseEvent event)
	{
		if (!(event.getBlock().getState() instanceof Dispenser)) return;
		
		Dispenser dispenser = (Dispenser) event.getBlock().getState();
		BlockFace facing = ((Directional) dispenser.getBlockData()).getFacing();
		
		ItemStack itemstack = event.getItem();
		if (itemstack.getType() != Material.BUCKET) return;
		
		Block cauldronBlock = event.getBlock().getRelative(facing);
		Material type = cauldronBlock.getType();
		
	    ItemStack result = null;
	    
	    if (type == Material.LAVA_CAULDRON) result = new ItemStack(Material.LAVA_BUCKET);
	    else
	    {
	    	Levelled cauldron = (Levelled) cauldronBlock.getBlockData();
	    	
	    	if (cauldron.getLevel() == 3)
	    	{
	    		if (type == Material.POWDER_SNOW_CAULDRON) result = new ItemStack(Material.POWDER_SNOW_BUCKET);
	    		else if (type == Material.WATER_CAULDRON) result = new ItemStack(Material.WATER_BUCKET);
	    	}
	    }
	    
	    if (result != null)
	    {
	    	Inventory inv = dispenser.getInventory();
	    	ItemStack[] contents = inv.getContents();
	    	
	    	for (int a = 0; a < contents.length; a++)
    		{
    			if (contents[a] != null && contents[a].getType() == Material.BUCKET)
    			{
    				contents[a].setAmount(contents[a].getAmount() - 1);
    				inv.setContents(contents);
    				break;
    			}
    		}
	    	
	    	Location dispenseLoc = dispenser.getLocation();
	    	World world = dispenseLoc.getWorld();
	    	Location ejectLoc = dispenseLoc.clone().add(facing.getModX() + 0.5, facing.getModY() + 0.5, facing.getModZ() + 0.5);

	    	cauldronBlock.setType(Material.CAULDRON);
	    	world.dropItem(ejectLoc, result);
	    }
	    
    	event.setCancelled(true);
	}
}
