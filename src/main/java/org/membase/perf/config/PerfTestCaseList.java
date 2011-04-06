package org.membase.perf.config;

import java.util.LinkedList;
import java.util.List;

public class PerfTestCaseList {
	public static final String S_MOXI = "moxi";
	public static final String V_MOXI_SS = "server-side";
	public static final String V_MOXI_CS = "client-side";
	public static final String S_CLUSTER_MEM = "clusterMem";
	public static final String S_BUCKET_SIZE = "bucketSize";
	public static final String S_REPLICAS = "replicas";
	public static final String S_SERVERS = "servers";
	public static final String S_CLIENTS = "clients";
	
	private String moxi_version;
	private String membase_version;
	private String version_number;
	private List<PerfTestCase> testcases;
	
	public PerfTestCaseList() {
		testcases = new LinkedList<PerfTestCase>();
	}
	
	public void add(PerfTestCase ptc) {
		testcases.add(ptc);
	}
	
	public PerfTestCase get(int index) {
		return testcases.get(index);
	}
	
	public int size() {
		return testcases.size();
	}
	
	public String getMoxiVersion() {
		return moxi_version;
	}
	
	public String getMembaseVersion() {
		return membase_version;
	}
	
	public String getVersionNumber() {
		return version_number;
	}
	
	public void setMoxiVersion(String moxi_version) {
		this.moxi_version = moxi_version;
	}
	
	public void setMembaseVersion(String membase_version) {
		this.membase_version = membase_version;
	}
	
	public void setVersionNumber(String version_number) {
		this.version_number = version_number;
	}
	
}
