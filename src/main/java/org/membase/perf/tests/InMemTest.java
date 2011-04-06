package org.membase.perf.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.membase.perf.config.PerfTestCase;
import org.membase.perf.config.PerfTestCaseList;
import org.membase.perf.exception.InsufficientMachinesException;
import org.membase.perf.lib.MachineList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.lcontroller.DLClient;
import com.freebase.json.JSON;

public class InMemTest extends PerfTest {
	private static final Logger LOG = LoggerFactory.getLogger(InMemTest.class);
	private final int opinterval = 10000;
	private final int timeinterval = 120;
	private final int warmuptime = 60;
	
	public InMemTest(MachineList mlist, PerfTestCase tc, String version) throws InsufficientMachinesException {
		super(mlist, tc, version, "InMemGetTest");
	}
	
	protected List<String> executeTest() {
		int ops = 0;
		long newops = 0;
		
		LOG.info("Executing performance test");
		DLClient client = new DLClient(clients.get(0).getHost());
		Iterator<String> itr = tc.tc.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			client.setValue(key, tc.tc.get(key));
		}

		if (tc.sc.get("moxi").equals("client-side")) {
			client.setValue("memcached.address", "127.0.0.1");
		} else {
			client.setValue("memcached.address", servers.get(0).getHost());
		}
		client.setValue("memcached.port", "11211");
		client.setValue("operationcount", "-1");
		client.setValue("dotransactions", "true");
		client.getStats();
		client.start();
		
		List<String> stats = new LinkedList<String>();
		while (ops - (opinterval/2) < newops) {
			// Warm up time
			ops+=10000;
			client.setValue("target", ops + "");
			LOG.info("Throughput changed to " + ops + " ops/sec");
			try {
				Thread.sleep(warmuptime * 1000);
			} catch (InterruptedException e) {
				System.out.println("Warmup interupted");
			}
			client.getStats();
			
			// Testing time
			try {
				Thread.sleep(timeinterval * 1000);
			} catch (InterruptedException e) {
				System.out.println("Test interupted");
			}
			String newStats = client.getStats();
			stats.add(newStats);
			try {
				newops = ((Long)JSON.parse(newStats).get("ops").value()).longValue() / timeinterval;
			} catch (ParseException e) {
				newops = 0;
			}
		}
		
		return stats;
	}
	
	protected final void exportResults(List<String> stats, String version) {
		int clients = (new Integer(tc.sc.get(PerfTestCaseList.S_CLIENTS))).intValue();
		int servers = (new Integer(tc.sc.get(PerfTestCaseList.S_SERVERS))).intValue();
		String sc = "(s" + servers + ")(c" + clients + ")";
		File folder = new File("results/" + "InMemTest/" + tc.export_path + version);
		File ofile = new File(folder.getAbsolutePath() + "/overview" + sc + ".txt");
		File lfile = new File(folder.getAbsolutePath() + "/latencies" + sc + ".txt");
		File cfile = new File(folder.getAbsolutePath() + "/config" + sc + ".txt");
		folder.mkdirs();

		String[] slist = new String[] {"0us    - 200us ",
				"200us  - 400us ",
				"400us  - 600us ",
				"600us  - 800us ",
				"800us  - 1ms   ",
				"1ms    - 2ms   ",
				"2ms    - 4ms   ",
				"4ms    - 8ms   ",
				"8ms    - 16ms  ",
				"16ms   - 32ms  ",
				"32ms   - 64ms  ",
				"64ms   - 128ms ",
				"128ms  - 256ms ",
				"256ms  - 512ms ",
				"512ms  - 1s    "};
			
		
		try {
			cfile.createNewFile();
			ofile.createNewFile();
			lfile.createNewFile();
			
			FileOutputStream latencyStream = new FileOutputStream(lfile);
			for (int i = 0; i < slist.length; i++) {
				latencyStream.write(("\"" + slist[i].replace(" ", "") + "\"").getBytes());
				for (int j = 0; j < stats.size(); j++) {
					long ops = ((Long)JSON.parse(stats.get(j)).get("ops").value()).longValue();
					JSON s = JSON.parse(stats.get(j)).get("GET").get("stats");
					latencyStream.write(("\t" + s.get(slist[i]).number().longValue()).getBytes());
					double percentage = (double)Math.round((s.get(slist[i]).number().doubleValue() / (double)ops) * 1000) / 1000.0;
					
					latencyStream.write(("\t" + percentage).getBytes());
				}
				latencyStream.write("\n".getBytes());
			}
			
			FileOutputStream overviewStream = new FileOutputStream(ofile);
			for (int i = 0; i < stats.size(); i++) {
				JSON nodes = JSON.parse(stats.get(i));
				long ops = ((Long)nodes.get("ops").value()).longValue();
				double latency = ((Double)nodes.get("GET").get("totallatency").value()).longValue();
				overviewStream.write((ops / timeinterval + "\t" + latency / ops + "\n").getBytes());
			}
				
			overviewStream.close();
			latencyStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
}
