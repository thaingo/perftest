package org.membase.perf.lib;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.cli.CouchbaseClient;
import com.couchbase.cli.internal.Auth;
import com.couchbase.install.Installer;
import com.couchbase.install.exception.InstallFailedException;
import com.couchbase.install.exception.UninstallFailedException;

public class BulkInstaller {
	private static final Logger LOG = LoggerFactory.getLogger(BulkInstaller.class);
	private List<Machine> servers;
	private List<Machine> clients;
	private String sVersion;
	private String mVersion;
	
	public BulkInstaller(List<Machine> servers, List<Machine> clients, String sVersion, String mVersion) {
		this.servers = servers;
		this.clients = clients;
		this.sVersion = sVersion;
		this.mVersion = mVersion;
	}
	
	public void installServers() throws InstallFailedException{
		for (int i = 0; i < servers.size(); i++) {
			Machine m = servers.get(i);
			LOG.info("Installing Membase on host: " + m.getHost());
			Installer inst = new Installer(m.getUser(), m.getPassword(), m.getHost(), sVersion, mVersion);
			inst.installServer();
		}
	}
	
	public void installClients() throws InstallFailedException {
		for (int i = 0; i < clients.size(); i++) {
			Machine m = clients.get(i);
			LOG.info("Installing Load Generator on host: " + m.getHost());
			Installer inst = new Installer(m.getUser(), m.getPassword(), m.getHost(), sVersion, mVersion);
			inst.installClient();
		}
	}
	
	public void installMoxis() throws InstallFailedException {
		for (int i = 0; i < clients.size(); i++) {
			Machine m = clients.get(i);
			LOG.info("Installing Moxi on host: " + m.getHost());
			Installer inst = new Installer(m.getUser(), m.getPassword(), m.getHost(), sVersion, mVersion);
			inst.installMoxi();
		}
	}
	
	public void uninstallServers() throws UninstallFailedException {
		for (int i = 0; i < servers.size(); i++) {
			Machine m = servers.get(i);
			Installer inst = new Installer(m.getUser(), m.getPassword(), m.getHost(), sVersion, mVersion);
			inst.uninstallLinux();
		}
	}
	
	public void uninstallClients() throws UninstallFailedException {
		for (int i = 0; i < clients.size(); i++) {
			Machine m = clients.get(i);
			Installer inst = new Installer(m.getUser(), m.getPassword(), m.getHost(), sVersion, mVersion);
			inst.uninstallClient();
		}
	}
	
	public void uninstallMoxis() throws UninstallFailedException {
		for (int i = 0; i < clients.size(); i++) {
			Machine m = clients.get(i);
			Installer inst = new Installer(m.getUser(), m.getPassword(), m.getHost(), sVersion, mVersion);
			inst.uninstallMoxi();
		}
	}
	
	public boolean clusterServers(int clusterSize, int bucketSize, int replicas) {
		String user = "Administrator";
		String pass = "password";
		CouchbaseClient rclient;
		
		if (servers.size() > 0) {
			for (int i = 0; i < servers.size(); i++) {
				LOG.info("Initializing Membase on host: " + servers.get(i).getHost());
				rclient = new CouchbaseClient(servers.get(i).getHost(), user, pass);
				rclient.configureDataPath("/var/opt/membase/1.6.5/data/ns_1");
				rclient.configureClusterSize(clusterSize);
				rclient.setCredentials(user, pass);
				rclient.createMembaseBucket("default", bucketSize, Auth.NONE, replicas, 11212, null);
			}
			
			rclient = new CouchbaseClient(servers.get(0).getHost(), user, pass);
			for (int i = 1; i < servers.size(); i++) {
				rclient.serverAdd(servers.get(i).getHost(), 8091, user, pass);
			}
			rclient.rebalance(null);
			while (rclient.isRebalancing()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
		return true;
	}
	
	public boolean clusterClients() {
		
		return true;
	}
	
	public List<Machine> getServers() {
		return servers;
	}
	
	public List<Machine> getClients() {
		return clients;
	}
}
