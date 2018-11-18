package fr.sorbonne_u.sylalexcenter.utils;

import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;

/**
 *
 * Sorbonne University 2018-2019
 * @author Alexandra Tudor
 * @author Sylia Righi
 *
 */
public class ComputerInfo {
	
	private ComputerURI computerURIs;
	private Computer computer;
	private ComputerServicesOutboundPort csop;
	
	
	public ComputerInfo(ComputerURI uris, Computer computer, ComputerServicesOutboundPort csop) {
		this.computerURIs = uris;
		this.computer = computer;
		this.csop = csop;
	}
	
	public ComputerServicesOutboundPort getCsop() {
		return csop;
	}

	public void setCsop(ComputerServicesOutboundPort csop) {
		this.csop = csop;
	}
	
	public Computer getComputer() {
		return computer;
	}

	public ComputerURI getUris() {
		return computerURIs;
	}

	public void setComputer(Computer computer) {
		this.computer = computer;
	}

	public void setUris(ComputerURI uris) {
		this.computerURIs = uris;
	}
}
