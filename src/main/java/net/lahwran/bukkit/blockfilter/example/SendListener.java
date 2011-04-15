
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
	public usermap[] users;

	public enum usermode {
		NORMAL,
		STONE,
		WATERWALK;
	}
	
	public class usermap {
		public usermap(String user, usermode mode)
		{
			this.user = user;
			this.mode = mode;
		}
		public String user;
		public usermode mode;
	}

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
					ArrayList<usermap> builder = new ArrayList<usermap>();
					while ((str = in.readLine()) != null)
					{
						if(str.length()>0)
						{
							String[] split = str.trim().split(" ");
							if (split.length==1)
							{
								builder.add(new usermap(split[0].trim(), usermode.STONE));
							}
							else if (split.length==2)
							{
								if(split[1].equalsIgnoreCase("stone"))
								{
									builder.add(new usermap(split[0].trim(), usermode.STONE));
								}
								else if (split[1].equalsIgnoreCase("waterwalk"))
								{
									builder.add(new usermap(split[0].trim(), usermode.WATERWALK));
								}
								else continue;
							}
							else continue;
						}
					}
					in.close();
					this.users=builder.toArray(new usermap[0]);
			}
			else
			{
				this.users = new usermap[]{new usermap("rock", usermode.STONE), new usermap("jesus", usermode.WATERWALK)};
				Writer out = new BufferedWriter(new FileWriter(data));
				try 
				{
					for (int i=0; i<users.length; i++)
					{
						out.write(users[i].user+" ");
						switch (users[i].mode)
						{
						case NORMAL:
							out.write("normal");
							break;
						case STONE:
							out.write("stone");
							break;
						case WATERWALK:
							out.write("waterwalk");
							break;
						default:
							out.write("wait, what?");
						}
						out.write("\n");
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
		usermode found = usermode.NORMAL;
		//these string comparisons area a bad idea... probably no faster alternative
		for(int iter=0; iter<users.length; iter++)
		{
			if(users[iter].user.equals(event.player.getName()))
			{
				found=users[iter].mode;
				break;
			}
		}
		if (found != usermode.NORMAL)
		{
			switch(found)
			{
			case WATERWALK:
				for(int blockx=0; blockx<event.sizex; blockx++)
				for(int blocky=0; blocky<event.sizey; blocky++)
				for(int blockz=0; blockz<event.sizez; blockz++)
				{
					if((event.getBlockID(blockx, blocky, blockz) == 8 ||
						event.getBlockID(blockx, blocky, blockz) == 9) &&
						event.getBlockID(blockx, blocky+1, blockz) == 0)
						event.setBlockID((byte) 20, blockx, blocky, blockz);
				}
				break;
			case STONE:
				for(int blockx=0; blockx<event.sizex; blockx++)
				for(int blocky=0; blocky<event.sizey; blocky++)
				for(int blockz=0; blockz<event.sizez; blockz++)
				{
					if(event.getBlockID(blockx, blocky, blockz) != 0)
						event.setBlockID((byte) 1, blockx, blocky, blockz);
				}
				break;
			}
			
		}
	}
}
