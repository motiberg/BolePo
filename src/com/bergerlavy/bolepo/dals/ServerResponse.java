package com.bergerlavy.bolepo.dals;


public abstract class ServerResponse {

	private int mFailureCode;
	
	/**
	 * checks if the request has succeeded
	 * @return <code>true</code> if it does, otherwise <code>false</code>
	 */
	public boolean isOK() {
		return mFailureCode == 0;
	}
	
	/**
	 * checks if the server attached data to it's response
	 * @return <code>true</code> if it does, otherwise <code>false</code>
	 */
	public abstract boolean hasData();
	
	/**
	 * Sets the failure code of the server response.
	 * @param failureCode the failure code the server returned. This value will affect the return value of <code>isOK</code> function.
	 * @see #isOK()
	 */
	protected void setFailureCode(int failureCode) {
		mFailureCode = failureCode;
	}
	
	public int getFailureCode() {
		return mFailureCode;
	}
}
