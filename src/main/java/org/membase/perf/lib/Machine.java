package org.membase.perf.lib;

public class Machine {
	private String user;
	private String pass;
	private String host;
	
	public Machine(String user, String pass, String host) {
		this.user = user;
		this.pass = pass;
		this.host = host;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getPassword() {
		return pass;
	}
	
	public String getHost() {
		return host;
	}
}
