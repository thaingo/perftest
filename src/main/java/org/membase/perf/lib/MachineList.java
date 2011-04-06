package org.membase.perf.lib;

import java.util.LinkedList;
import java.util.List;

import org.membase.perf.exception.InsufficientMachinesException;

public class MachineList {
	private List<Machine> servers;
	private List<Machine> clients;
	private String knownHosts;
	private String keyFile;
	
	public MachineList() {
		this.servers = new LinkedList<Machine>();
		this.clients = new LinkedList<Machine>();
	}
	
	public List<Machine> getClients(int numClients) throws InsufficientMachinesException {
		List<Machine> clist = null;
		
		if (clients == null) {
			throw new InsufficientMachinesException("Not enough client machines defined");
		}
		
		if (numClients <= clients.size()) {
			clist = new LinkedList<Machine>();
			
			for (int i = 0; i < numClients; i++) {
				clist.add(clients.get(i));
			}
		} else {
			throw new InsufficientMachinesException("Not enough client machines defined");
		}
		
		return clist;
	}
	
	public List<Machine> getServers(int numServers) throws InsufficientMachinesException {
		List<Machine> slist = null;
		
		if (servers == null) {
			throw new InsufficientMachinesException("Not enough client machines defined");
		}
		
		if (numServers <= servers.size()) {
			slist = new LinkedList<Machine>();
			
			for (int i = 0; i < numServers; i++) {
				slist.add(servers.get(i));
			}
		} else {
			throw new InsufficientMachinesException("Not enough client machines defined");
		}
		
		return slist;
	}
	
	public void addServer(String host, String user, String password) {
		servers.add(new Machine(user, password, host));
	}
	
	public void addClient(String host, String user, String password) {
		clients.add(new Machine(user, password, host));
	}
	
	public String getKnownHostsFile() {
		return knownHosts;
	}
	
	public void setKnownHostFile(String knownHosts) {
		this.knownHosts = knownHosts;
	}
	
	public String getKeyFile() {
		return keyFile;
	}
	
	public void setKeyFile(String key_file) {
		this.keyFile = key_file;
	}
}
