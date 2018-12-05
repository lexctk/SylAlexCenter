package fr.sorbonne_u.sylalexcenter.admissioncontroller;

import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;

public class ComputerData {
	
	private Integer mustHaveCores;
	private String computerURI;
	private ComputerServicesOutboundPort csop;

	public ComputerData(Integer mustHaveCores, String computerURI, ComputerServicesOutboundPort csop) {
		this.mustHaveCores = mustHaveCores;
		this.computerURI = computerURI;
		this.csop = csop;
	}
	
	public Integer getMustHaveCores() {
		return mustHaveCores;
	}
	public void setMustHaveCores(Integer mustHaveCores) {
		this.mustHaveCores = mustHaveCores;
	}
	public String getComputerURI() {
		return computerURI;
	}
	public void setComputerURI(String computerURI) {
		this.computerURI = computerURI;
	}
	public ComputerServicesOutboundPort getCsop() {
		return csop;
	}
	public void setCsop(ComputerServicesOutboundPort csop) {
		this.csop = csop;
	}
}
