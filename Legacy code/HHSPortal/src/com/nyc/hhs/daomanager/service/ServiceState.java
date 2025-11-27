package com.nyc.hhs.daomanager.service;

import com.nyc.hhs.frameworks.transaction.Channel;

/**
 * 
 * QuestionAnswerService: This class is extended by all the Service classes
 *                        which requires the Channel
 * 
 */
public class ServiceState
{
	private StringBuffer moState = new StringBuffer();

	private Channel moChannel;

	/**
	 * This method gets the Channel object
	 * 
	 * @return Channel moChannel
	 */
	public Channel getChannel()
	{
		return moChannel;
	}

	/**
	 * This method sets the Channel object
	 * 
	 * @param aoChannel
	 */
	public void setChannel(Channel aoChannel)
	{
		this.moChannel = aoChannel;
	}

	/**
	 * This method gets the getMoState
	 * 
	 * @return moState
	 */
	public StringBuffer getMoState()
	{
		return moState;
	}

	/**
	 * This method sets the asState
	 * 
	 * @param asState
	 */
	public void setMoState(String asState)
	{
		this.moState.append(asState);
	}
}
