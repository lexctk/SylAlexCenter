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
	private String avmURI;

	public AllocationMap(Computer computer, AllocatedCore[] cores, String avmURI) {
		this.computer = computer;
		this.cores = cores;
		this.avmURI = avmURI;
	}

	public String getVMUri() {
		return avmURI;
	}

	public void setVMUri(String vMUri) {
		avmURI = vMUri;
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