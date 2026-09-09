package com.w385.hopper;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MultiHopperPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MultiHopperPlugin.class);
		RuneLite.main(args);
	}
}
