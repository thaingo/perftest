package org.membase.perf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.membase.perf.config.ConfigParser;
import org.membase.perf.config.HostsParser;
import org.membase.perf.config.PerfTestCase;
import org.membase.perf.config.PerfTestCaseList;
import org.membase.perf.exception.InsufficientMachinesException;
import org.membase.perf.lib.MachineList;
import org.membase.perf.tests.PerfTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerfRunner {
	private static final Logger LOG = LoggerFactory.getLogger(PerfRunner.class);
	private PerfTestCaseList testcases;
	MachineList mlist;
	
	public PerfRunner() {
		HostsParser hp = new HostsParser("/Users/mikewied/Desktop/machinelist.xml");
		mlist = hp.parse(); 
		testcases = ConfigParser.parse();
	}
	
	public void runTests() {
		
		for (int i = 0; i < testcases.size(); i++) {
			PerfTestCase tc = testcases.get(i);
			
			try {
				Class<?> cl = getClass().getClassLoader().loadClass(tc.clazz);
				Constructor<?> c = cl.getConstructor(MachineList.class, PerfTestCase.class, String.class);
				PerfTest test = (PerfTest) c.newInstance(mlist, tc, testcases.getVersionNumber());
				if(test.runTest(testcases.getMembaseVersion(), testcases.getMoxiVersion())) {
					LOG.info("InMemGetTest passed");
				} else {
					LOG.info("InMemGetTest failed");
				}
			} catch (InsufficientMachinesException e) {
				LOG.error(e.getMessage());
				LOG.info("InMemGetTest failed");
			} catch (ClassNotFoundException e) {
				LOG.error("Couldn't find test class " + tc.clazz + ". Skipping this test.");
				LOG.info("InMemGetTest failed");
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				LOG.error("Couldn't find method for " + tc.clazz + ". Skipping this test.");
				LOG.info("InMemGetTest failed");
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		PerfRunner runner = new PerfRunner();
		runner.runTests();
	}
}
