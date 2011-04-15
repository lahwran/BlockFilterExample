
package net.lahwran.bukkit.blockfilter.example;

import java.io.File;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

public class BlockSendExamplePlugin extends JavaPlugin {
	private final SendListener blockListener = new SendListener(this);

	public void onEnable() {
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_SEND, blockListener, Priority.Normal, this);
	}
	public void onDisable() {}
	
	/**
	 * Initialise the data directory for this plugin.
	 *
	 * @return true if the directory has been created or already exists.
	 * @author Cogito
	 */
	public boolean createDataDirectory() {
		File file = this.getDataFolder();
		if (file == null)
			return false;
		if (!file.isDirectory()){
			if (!file.mkdirs()) {
				// failed to create the non existent directory, so failed
				return false;
			}
		}
		return true;
	}
	 
	/**
	 * Retrieve a File description of a data file for your plugin.
	 * This file will be looked for in the data directory of your plugin, wherever that is.
	 * There is no need to specify the data directory in the filename such as "plugin/datafile.dat"
	 * Instead, specify only "datafile.dat"
	 *
	 * @param filename The name of the file to retrieve.
	 * @param mustAlreadyExist True if the file must already exist on the filesystem.
	 *
	 * @return A File descriptor to the specified data file, or null if there were any issues.
	 * @author Cogito
	 */
	public File getDataFile(String filename, boolean mustAlreadyExist) {
		if (createDataDirectory()) {
			File file = new File(this.getDataFolder(), filename);
			if (mustAlreadyExist) {
				if (file.exists()) {
					return file;
				}
			} else {
				return file;
			}
		}
		return null;
	}

}
