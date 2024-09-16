package dev.lanoxkodo;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class FileHandler {

	private KodoAltHandlers kodoAltHandlers;
	protected File recipeFile;
	protected YamlConfiguration recipeData;
	
	FileHandler(KodoAltHandlers kahIn)
	{
		kodoAltHandlers = kahIn;
		kodoAltHandlers.saveDefaultConfig();
		
		recipeFile = new File(kodoAltHandlers.getDataFolder(), "recipes.yml");
		
		if (!recipeFile.exists()) kodoAltHandlers.saveResource("recipes.yml", false);
		recipeData = new YamlConfiguration();
		
		try
		{
			recipeData.load(recipeFile);
		}
		catch (IOException|org.bukkit.configuration.InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}
	
	protected boolean getValue(String property)
	{
		return kodoAltHandlers.getConfig().getBoolean(property);
	}
}
