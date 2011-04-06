package org.membase.perf.tests;

import java.util.Iterator;
import java.util.List;

import org.membase.perf.config.PerfTestCase;
import org.membase.perf.config.PerfTestCaseList;
import org.membase.perf.exception.InsufficientMachinesException;
import org.membase.perf.lib.BulkInstaller;
import org.membase.perf.lib.Machine;
import org.membase.perf.lib.MachineList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.install.exception.InstallFailedException;
import com.couchbase.install.exception.UninstallFailedException;
import com.couchbase.lcontroller.DLClient;

// Concurrent installing
// Decide on a way to save stats
// Create some new test cases
public abstract class PerfTest {
	private static final Logger LOG = LoggerFactory.getLogger(PerfTest.class);
	private String testname;
	private String version;
	protected List<Machine> servers;
	protected List<Machine> clients;
	protected PerfTestCase tc;
	
	protected abstract List<String> executeTest();
	
	protected abstract void exportResults(List<String> stats, String version);
	
	protected PerfTest(MachineList mlist, PerfTestCase tc, String version, String testname) throws InsufficientMachinesException {
		this.servers = mlist.getServers((new Integer(tc.sc.get(PerfTestCaseList.S_SERVERS))).intValue());
		this.clients = mlist.getClients((new Integer(tc.sc.get(PerfTestCaseList.S_CLIENTS))).intValue());
		this.version = version;
		this.tc = tc;
		this.testname = testname;
	}
	
	protected final boolean initTest() {
		LOG.info("Initializing performance test");
		if (clients != null && clients.size() > 0) {
			DLClient client = new DLClient(clients.get(0).getHost());
			
			
			Iterator<String> itr = tc.tc.keySet().iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				client.setValue(key, tc.tc.get(key));
			}
			
			if (tc.sc.get(PerfTestCaseList.S_MOXI).equals(PerfTestCaseList.V_MOXI_CS)) {
				client.setValue("memcached.address", "127.0.0.1");
			} else {
				client.setValue("memcached.address", servers.get(0).getHost());
			}
			client.setValue("memcached.port", "11211");
			client.setValue("dotransactions", "false");
			client.setValue("target", "10000");
			client.setValue("threadcount", "16");
			client.setValue("requestdistribution", "zipfian");
			client.start();
			
			do {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			} while (client.getStatus());
			return true;
		}
		return false;
	}
	
	protected final boolean setup(String sVersion, String mVersion) {
		if (servers == null || clients == null)
			return false;
		
		LOG.info("Installing Servers");
		BulkInstaller installer = new BulkInstaller(servers, clients, sVersion, mVersion);
		try {
			installer.installServers();
		} catch (InstallFailedException e) {
			LOG.info("Server cluster installation failed - Reason: " + e.getMessage());
			try {
				installer.uninstallServers();
			} catch (UninstallFailedException e1) {
				LOG.error("Uninstall Failed");
			}
			return false;
		}
		
		LOG.info("Clustering Servers");
		int clusterSize = (new Integer(tc.sc.get(PerfTestCaseList.S_CLUSTER_MEM))).intValue();
		int bucketSize = (new Integer(tc.sc.get(PerfTestCaseList.S_BUCKET_SIZE))).intValue();
		int replicas = (new Integer(tc.sc.get(PerfTestCaseList.S_REPLICAS))).intValue();
		installer.clusterServers(clusterSize, bucketSize, replicas);
		
		LOG.info("Installing Clients");
		try {
			installer.installClients();
			if (tc.sc.get(PerfTestCaseList.S_MOXI).equals(PerfTestCaseList.V_MOXI_CS)) {
				installer.installMoxis();
			}
		} catch(InstallFailedException e) {
			LOG.info(e.getMessage());
			LOG.info("Client Cluster installation failed - Reason: " + e.getMessage());
			try {
				installer.uninstallClients();
			} catch (UninstallFailedException e1) {
				LOG.error("Uninstall Failed");
			}
			return false;
		}
		return true;
	}
	
	public boolean runTest(String sVersion, String mVersion) throws NumberFormatException, InsufficientMachinesException {
		LOG.info("Starting: " + testname);		
		if (!setup(sVersion, mVersion)) {
			LOG.info("Finished: " + testname);
			return false;
		}
		initTest();
		List<String> stats = executeTest();
		tearDown(sVersion, mVersion);
		LOG.info("Finished: " + testname);
		/*String s = new String("{\"ops\" : 22473,\"GET\" : {\"totallatency\" : 4.376487E7,\"stats\" : {\"0us    - 200us " +
				"\" : 0,\"200us  - 400us \" : 3,\"400us  - 600us \" : 2558,\"600us  - 800us \" : 5928,\"800us  - 1ms   \" : " +
				"4993,\"1ms    - 2ms   \" : 7691,\"2ms    - 4ms   \" : 858,\"4ms    - 8ms   \" : 274,\"8ms    - 16ms  \" : 145," +
				"\"16ms   - 32ms  \" : 10, \"32ms   - 64ms  \" : 2, \"64ms   - 128ms \" : 0, \"128ms  - 256ms \" : 0, \"256ms" +
				"  - 512ms \" : 0, \"512ms  - 1s    \" : 0 }, \"returncodes\" : { \"0\" : 22473 } } }");
		List<String> stats = new LinkedList<String>();
		stats.add(s);*/
		if (stats != null) {
			exportResults(stats, version);
			return true;
		}
		return false;
	}
	
	protected final void tearDown(String sVersion, String mVersion) {
		BulkInstaller installer = new BulkInstaller(servers, clients, sVersion, mVersion);
		try {
			installer.uninstallClients();
			installer.uninstallServers();
			if (tc.sc.get(PerfTestCaseList.S_MOXI).equals(PerfTestCaseList.V_MOXI_CS)) {
				installer.uninstallMoxis();
			}
		} catch (UninstallFailedException e) {
			LOG.error("Uninstall failed");
		}
	}
}
