package fr.sorbonne_u.datacenter.hardware.computers.interfaces;

import java.util.Map;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.interfaces.TimeStampingI;

/**
 * The class <code>ComputerStaticStateI</code> implements objects representing
 * the static state information of computers transmitted through the
 * <code>ComputerStaticStateDataI</code> interface of <code>Computer</code>
 * components.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The interface is used to type objects pulled from or pushed by a computer
 * using a data interface in pull or push mode. It gives access to static
 * information, that is information *not* subject to changes during the
 * existence of the computer.
 * 
 * Data objects are timestamped in standard Unix local time format, with the IP
 * of the computer doing this timestamp.
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>
 * Created on : April 14, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ComputerStaticStateI extends DataOfferedI.DataI, DataRequiredI.DataI, TimeStampingI {
	/**
	 * return the computer URI.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true // no precondition.
	 * post	true // no postcondition.
	 * </pre>
	 *
	 * @return the computer URI.
	 */
	String getComputerURI();

	/**
	 * return the number of processors in the computer.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true // no precondition.
	 * post	true // no postcondition.
	 * </pre>
	 *
	 * @return the number of processors in the computer.
	 */
	int getNumberOfProcessors();

	/**
	 * return the number of cores per processor on this computer.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true // no precondition.
	 * post	true // no postcondition.
	 * </pre>
	 *
	 * @return the number of cores per processor on this computer.
	 */
	int getNumberOfCoresPerProcessor();

	/**
	 * return an array of the processors URI on this computer.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true // no precondition.
	 * post	true // no postcondition.
	 * </pre>
	 *
	 * @return array of the processors URI on this computer.
	 */
	Map<Integer, String> getProcessorURIs();

	/**
	 * return a map from processors URI to a map from processor's port types to
	 * processors' ports URI.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true // no precondition.
	 * post	true // no postcondition.
	 * </pre>
	 *
	 * @return map from processors URI to a map from processor's port types to
	 *         processors' ports URI.
	 */
	Map<String, Map<Processor.ProcessorPortTypes, String>> getProcessorPortMap();
}
