package eu.heliovo.dpas.ie.services.directory.utils;

import utilities.TimeUtilities;


public class DebugUtilities
{
	TimeUtilities	tUtils	=	new TimeUtilities();
	
	public	void	printLog(String where, String message)
	{
		System.out.println(
				"[" + 
				tUtils.getShortStamp() + 
				", " +
				where + 
				"] --> " + 
				message);
	}
}