package com.qlib.qinterface;

import org.json.JSONObject;

public interface QHttpRequestBack {
	public void onQhttpFailure();
	public void onQhttpSuccess(JSONObject response);
}
