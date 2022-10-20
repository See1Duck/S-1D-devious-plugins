package net.unethicalite.plugins.birdhouses;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.plugins.TaskPlugin;
import net.unethicalite.plugins.birdhouses.model.BirdHouse;
import net.unethicalite.plugins.birdhouses.model.BirdHouseLocation;
import net.unethicalite.plugins.birdhouses.model.BirdHouseState;
import net.unethicalite.plugins.birdhouses.tasks.GatherTools;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Extension
@PluginDescriptor(name = "Unethical Birdhouses", enabledByDefault = false)
@Slf4j
public class BirdHousesPlugin extends TaskPlugin
{
	private static final List<Integer> INV_SETUP_ITEMS = List.of(
			ItemID.HAMMER,
			ItemID.CHISEL
	);

	private static final List<BirdHouse> BIRD_HOUSES = List.of(
			new BirdHouse(BirdHouseLocation.MEADOW_NORTH, BirdHouseState.UNKNOWN),
			new BirdHouse(BirdHouseLocation.MEADOW_SOUTH, BirdHouseState.UNKNOWN),
			new BirdHouse(BirdHouseLocation.VALLEY_NORTH, BirdHouseState.UNKNOWN),
			new BirdHouse(BirdHouseLocation.VALLEY_SOUTH, BirdHouseState.UNKNOWN)
	);

	@Inject
	private BirdHousesConfig config;

	@Inject
	private ConfigManager configManager;

	private final Task[] tasks =
			{
					new GatherTools(this)
			};

	@Override
	public Task[] getTasks()
	{
		return tasks;
	}

	@Override
	protected void startUp()
	{
		if (Game.isLoggedIn())
		{
			for (BirdHouse birdHouse : BIRD_HOUSES)
			{
				birdHouse.setState(BirdHouseState.fromVarpValue(Vars.getVarp(birdHouse.getVarp().getId())));
			}
		}
	}

	protected Optional<BirdHouse> getNextBirdhouse()
	{
		return BIRD_HOUSES.stream()
				.filter(b -> b.getState() != BirdHouseState.SEEDED && !b.isComplete())
				.findFirst();
	}

	protected List<Integer> getInventorySetup()
	{
		return INV_SETUP_ITEMS;
	}

	@Provides
	BirdHousesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BirdHousesConfig.class);
	}

	@Subscribe
	private void onGameTick(GameTick e)
	{
		for (BirdHouse birdHouse : BIRD_HOUSES)
		{
			log.debug("{}", birdHouse);
		}
	}

	@Subscribe
	private void onVarbitChanged(VarbitChanged e)
	{
		int varpId = e.getVarpId();
		for (BirdHouse birdHouse : BIRD_HOUSES)
		{
			if (birdHouse.getVarp().getId() == varpId)
			{
				birdHouse.setState(BirdHouseState.fromVarpValue(e.getValue()));
			}
		}
	}
}
