package fr.sorbonne_u.sylalexcenter.admissioncontroller.utils;

import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;

import java.util.Arrays;

/**
 * Manage computerURI, csop and allocated cores
 *
 * @author Alexandra Tudor
 * @author Sylia Righi
 */
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

	/**
	 *
	 * @return csop
	 */
	public ComputerServicesOutboundPort getCsop() {
		return csop;
	}

	/**
	 *
	 * @return number of cores allocated
	 */
	public Integer getNumberOfCoresPerAVM() {
		return allocatedCores.length;
	}

	/**
	 *
	 * @return allocated cores
	 */
	public AllocatedCore[] getAllocatedCores() {
		return this.allocatedCores;
	}

	/**
	 *
	 * @return computer URI
	 */
	public String getComputerURI() {
		return computerURI;
	}

	/**
	 * Add new allocated cores to current allocated cores
	 *
	 * @param allocatedNewCores new cores to add
	 */
	public void addNewCores (AllocatedCore[] allocatedNewCores) {
		AllocatedCore[] result = Arrays.copyOf(this.allocatedCores, this.allocatedCores.length + allocatedNewCores.length);
		System.arraycopy(allocatedNewCores, 0, result, this.allocatedCores.length, allocatedNewCores.length);

		this.allocatedCores = new AllocatedCore[result.length];
		System.arraycopy(result, 0, this.allocatedCores, 0, result.length);
	}

	/**
	 * Remove a number of cores from allocated cores.
	 *
	 * Cores are removed from the end of the array (last in first out)
	 *
	 * @param numberOfCoresToRemove number of cores to remove from current allocated cores
	 */
	public void removeCores (int numberOfCoresToRemove) {
		AllocatedCore[] result = Arrays.copyOf(this.allocatedCores, this.allocatedCores.length - numberOfCoresToRemove);

		this.allocatedCores = new AllocatedCore[result.length];
		System.arraycopy(result, 0, this.allocatedCores, 0, result.length);
	}
}
