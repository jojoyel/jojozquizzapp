package com.jojo.jojozquizz.tools;

import android.app.Application;

public class Global extends Application {

	private String serverKey;
	private String processedKey;

	public String getAuthKey() {
		return serverKey;
	}

	public String getProcessedKey() {
		return processedKey;
	}

	public void setAuthKey(String authKey) {
		this.serverKey = authKey;
	}

	public void setProcessedKey(String processedKey) {
		this.processedKey = processedKey;
	}
}
