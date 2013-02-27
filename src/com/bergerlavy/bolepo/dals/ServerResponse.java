package com.bergerlavy.bolepo.dals;

public class ServerResponse {

	private ServerResponseStatus mStatus;
	private String mErrorInfo;
	private Object mData;
	
	public ServerResponse(ServerResponseStatus status, String errorInfo, Object data) {
		mStatus = status;
		mErrorInfo = errorInfo;
		mData = data;
	}
	
	public ServerResponse(ServerResponseStatus status, Object data) {
		mStatus = status;
		mErrorInfo = null;
		mData = data;
	}
	
	public ServerResponse(ServerResponseStatus status, String errorInfo) {
		mStatus = status;
		mErrorInfo = errorInfo;
		mData = null;
	}
	
//	public void setState(String status) {
//		mStatus = status;
//	}
//	
//	public void setErrorInfo(String desc) {
//		mErrorInfo = desc;
//	}
//	
//	public void setData(Object data) {
//		mData = data;
//	}
	
	public boolean hasData() {
		return mData != null;
	}
	
	public Object getData() {
		return mData;
	}
	
	public ServerResponseStatus getStatus() {
		return mStatus;
	}
	
	public String getErrorInfo() {
		return mErrorInfo;
	}
}
