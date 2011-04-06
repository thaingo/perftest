package org.membase.perf.config;

import java.util.HashMap;

public class PerfTestCase {
	public HashMap<String, String> sc;
	public HashMap<String, String> tc;
	public String clazz;
	public String export_path;
	
	public PerfTestCase() {
		sc = new HashMap<String, String>();
		tc = new HashMap<String, String>();
	}
}
