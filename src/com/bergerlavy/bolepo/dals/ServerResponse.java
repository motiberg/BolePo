package com.bergerlavy.bolepo.dals;

import com.bergerlavy.bolepo.BolePoConstants.ServerResponseStatus;

public abstract class ServerResponse {

	private ServerResponseStatus mStatus;
	
	/**
	 * checks if the request has succeeded
	 * @return <code>true</code> if it does, otherwise <code>false</code>
	 */
	public boolean isOK() {
		return mStatus == ServerResponseStatus.OK;
	}
	
	/**
	 * checks if the server attached data to it's response
	 * @return <code>true</code> if it does, otherwise <code>false</code>
	 */
	public abstract boolean hasData();
	
	/**
	 * Sets the status of the server response.
	 * @param status the status the server returned. This value will affect the return value of <code>isOK</code> function.
	 * @see #isOK()
	 */
	protected void setStatus(ServerResponseStatus status) {
		mStatus = status;
	}
}
