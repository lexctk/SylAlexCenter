package fr.sorbonne_u.sylalexcenter.admissioncontroller.utils;

import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class AllocationMap {
	private Computer computer;
	private AllocatedCore[] cores;
	private String VMUri;

	public AllocationMap(Computer computer, AllocatedCore[] cores, String VMUri) {
		this.computer = computer;
		this.cores = cores;
		this.VMUri = VMUri;
	}

	public String getVMUri() {
		return VMUri;
	}

	public void setVMUri(String vMUri) {
		VMUri = vMUri;
	}

	public Computer getComputer() {
		return computer;
	}

	public void setComputer(Computer computer) {
		this.computer = computer;
	}

	public AllocatedCore[] getCores() {
		return cores;
	}

	public void setCores(AllocatedCore[] cores) {
		this.cores = cores;
	}
	
	public void freeCores() throws Exception {
		computer.releaseCores(cores);
	}
}
