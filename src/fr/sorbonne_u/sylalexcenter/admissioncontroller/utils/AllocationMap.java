package fr.sorbonne_u.sylalexcenter.admissioncontroller.utils;

import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;

import java.util.Arrays;

public class AllocationMap {

	private String computerURI;
	private ComputerServicesOutboundPort csop;
	private AllocatedCore[] allocatedCores;

	public AllocationMap(String computerURI,
	              ComputerServicesOutboundPort csop,
	              AllocatedCore[] allocatedCores) {
		this.computerURI = computerURI;
		this.csop = csop;
		this.allocatedCores = allocatedCores;
	}

	public ComputerServicesOutboundPort getCsop() {
		return csop;
	}

	public Integer getNumberOfCoresPerAVM() {
		return allocatedCores.length;
	}

	public AllocatedCore[] getAllocatedCores() {
		return this.allocatedCores;
	}

	public String getComputerURI() {
		return computerURI;
	}

	public void addNewCores (AllocatedCore[] allocatedNewCores) {
		AllocatedCore[] result = Arrays.copyOf(this.allocatedCores, this.allocatedCores.length + allocatedNewCores.length);
		System.arraycopy(allocatedNewCores, 0, result, this.allocatedCores.length, allocatedNewCores.length);

		this.allocatedCores = new AllocatedCore[result.length];
		System.arraycopy(result, 0, this.allocatedCores, 0, result.length);
	}

	public void removeCores (int numberOfCoresToRemove) {
		AllocatedCore[] result = Arrays.copyOf(this.allocatedCores, this.allocatedCores.length - numberOfCoresToRemove);

		this.allocatedCores = new AllocatedCore[result.length];
		System.arraycopy(result, 0, this.allocatedCores, 0, result.length);
	}
}
