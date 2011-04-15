
package net.lahwran.bukkit.blockfilter.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.bukkit.event.player.BlockSendEvent;
import org.bukkit.event.player.PlayerListener;

public class SendListener extends PlayerListener {
	private final BlockSendExamplePlugin plugin;
	public String[] users;

	public SendListener(final BlockSendExamplePlugin plugin) {
		this.plugin = plugin;
		loadData();
	}

	public void loadData()
	{
		//any worth-shit OS will be able to tell it's a text file, no need to add an extension
		//I don't care about windows users, if you are one, it's dead easy to fix, see line below
		File data = plugin.getDataFile("users",false);
		if (data == null)
			return;
		try {
			if(data.exists())
			{
				
					BufferedReader in = new BufferedReader(new FileReader(data));
					String str;
					ArrayList<String> builder = new ArrayList<String>();
					while ((str = in.readLine()) != null)
					{
						if(str.length()>0)
						{
							builder.add(str.trim());
						}
					}
					in.close();
					this.users=builder.toArray(new String[0]);
			}
			else
			{
				this.users = new String[]{"rock", "stone"};
				Writer out = new BufferedWriter(new FileWriter(data));
				try 
				{
					for (int i=0; i<users.length; i++)
					{
						out.write(users[i]+"\n");
					}
				}
				finally 
				{
					out.close();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void onBlockSend(BlockSendEvent event) {
		if (users==null)
		{
			loadData();
		}
		boolean found = false;
		//these string comparisons area a bad idea... probably no faster alternative
		for(int iter=0; iter<users.length; iter++)
		{
			if(users[iter].equals(event.player.getName()))
			{
				found=true;
				break;
			}
		}
		if (found)
		{
			for(int blockx=0; blockx<event.sizex; blockx++)
			for(int blocky=0; blocky<event.sizey; blocky++)
			for(int blockz=0; blockz<event.sizez; blockz++)
			{
				if(event.getBlockID(blockx, blocky, blockz) != 0)
					event.setBlockID((byte) 1, blockx, blocky, blockz);
			}
		}
	}
}
