package dev.lanoxkodo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class KodoAltHandlers extends JavaPlugin implements Listener {

	private FileHandler fileHandler;
	private static List<NamespacedKey> SmeltingRecipes;
	private static List<NamespacedKey> CraftingRecipes;
	
	@Override
	public void onEnable()
	{
		fileHandler = new FileHandler(this);
		
		if (fileHandler.getValue("useDispenserBucketLogic")) getServer().getPluginManager().registerEvents(new DispenserBucketLogic(), this);
		
		createRecipes();
		discoverRecipes(new ArrayList<Player>(Bukkit.getOnlinePlayers()));
		
		getLogger().info("KodoAltHandlers has been enabled.");
	}

	@Override
	public void onDisable()
	{
		getLogger().info("KodoAltHandlers has been disabled.");
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		discoverRecipes(Collections.singletonList(event.getPlayer()));
	}
	
	private void discoverRecipes(List<Player> playerList)
	{
		for (Player player : playerList)
		{
			player.discoverRecipes(CraftingRecipes);
			player.discoverRecipes(SmeltingRecipes);
		}
	}
	
	private String[] colors()
	{
		return new String[] {"BROWN", "RED", "ORANGE", "YELLOW", "LIME", "GREEN", "BLUE", "CYAN",
			"LIGHT_BLUE","PINK", "MAGENTA", "PURPLE", "WHITE", "LIGHT_GRAY", "GRAY", "BLACK"};
	}
	
	private void createRecipes()
	{
		SmeltingRecipes = new ArrayList<>();
		CraftingRecipes = new ArrayList<>();
		processCategoryRecipes(fileHandler.recipeData, "ColoredSmelt");
		processCategoryRecipes(fileHandler.recipeData, "PlainSmelt");
		processCategoryRecipes(fileHandler.recipeData, "Crafting");
	}
	
	private void processCategoryRecipes(FileConfiguration config, String category)
	{
		if (config.contains(category))
		{
			ConfigurationSection recipes = config.getConfigurationSection(category);
			
			for (String recipeKey : recipes.getKeys(false))
			{
				ConfigurationSection recipe = recipes.getConfigurationSection(recipeKey);
				String output = recipe.getString("Output");
				
				Object inputObject = recipe.get("Input");
				List<String> inputs = new ArrayList<>();
				
				if (inputObject instanceof List<?>)
				{
					for (Object item : (List<?>) inputObject)
					{
						if (item instanceof String) inputs.add((String) item);
					}
				}
				else if (inputObject instanceof String) inputs.add((String) inputObject);
				
				switch (category)
				{
					case "ColoredSmelt":
						coloredSmeltingRecipes(recipeKey, inputs.get(0), output, (float) recipe.getDouble("XP", .1), recipe.getInt("Time", 200));
						break;
					case "PlainSmelt":
						plainSmeltingRecipes(recipeKey, inputs.get(0), output, (float) recipe.getDouble("XP", .1), recipe.getInt("Time", 200));
						break;
					case "Crafting":
						craftingRecipes(recipeKey, inputs, output, recipe.getInt("OutputCount", 1));
						break;
				}
			}
		}
	}
	
	/**
	 * Create colored recipes where the conversions can be automated
	 * 
	 * @param inputType		- The input object type that a color name will be appended to the start of
	 * @param outputType	- The output object type that a color name will be appended to the start of
	 * @param xp			- The amount of xp granted for this recipe
	 * @param time			- The amount of time it takes to complete this recipe
	 * @return
	 */
	private void coloredSmeltingRecipes(String recipeName, String inputType, String outputType, float xp, int time)
	{
		ArrayList<NamespacedKey> recipes = new ArrayList<>();
		for (String color : colors())
		{
			NamespacedKey key = new NamespacedKey((Plugin)this, recipeName + color);
			Material material = Material.valueOf(color + inputType.toUpperCase());
			ItemStack result = new ItemStack(Material.valueOf(color + outputType.toUpperCase()));
			FurnaceRecipe recipe = new FurnaceRecipe(key, result, material, xp, time);
			Bukkit.addRecipe((Recipe)recipe);
			recipes.add(key);
		}
		
		SmeltingRecipes.addAll(recipes);
	}
	
	private void plainSmeltingRecipes(String recipeName, String input, String output, float xp, int time)
	{
		NamespacedKey key = new NamespacedKey((Plugin)this, recipeName);
		Material material = Material.valueOf(input.toUpperCase());
		ItemStack result = new ItemStack(Material.valueOf(output.toUpperCase()));
		FurnaceRecipe recipe = new FurnaceRecipe(key, result, material, xp, time);
		
		Bukkit.addRecipe((Recipe)recipe);
		SmeltingRecipes.add(key);
	}
	
	private void craftingRecipes(String recipeName, List<String> inputs, String output, int count)
	{
		ShapelessRecipe sr = new ShapelessRecipe(new NamespacedKey(this, recipeName), new ItemStack(Material.valueOf(output.toUpperCase()), count));
		for (String input : inputs)
		{
			sr.addIngredient(Material.valueOf(input.toUpperCase()));
		}
		
		Bukkit.addRecipe((Recipe)sr);
		CraftingRecipes.add(sr.getKey());
	}
}
