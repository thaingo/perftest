package org.membase.perf.tests;

import java.util.List;

import org.membase.perf.config.PerfTestCase;
import org.membase.perf.exception.InsufficientMachinesException;
import org.membase.perf.lib.MachineList;

public class ChurnTest extends PerfTest {

	protected ChurnTest(MachineList mlist, PerfTestCase tc, String version) throws InsufficientMachinesException {
		super(mlist, tc, version, "ChurnTest");
	}

	@Override
	protected List<String> executeTest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void exportResults(List<String> stats, String version) {
		// TODO Auto-generated method stub
		
	}

}
