package fr.sorbonne_u.datacenter.hardware.processors.interfaces;

/**
 * The interface <code>ProcessorStateDataConsumerI</code> defines the consumer
 * side methods used to receive state data pushed by a processor, both static
 * and dynamic.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The interface must be implemented by all classes representing components that
 * will consume as clients state data pushed by a processor. They are used by
 * <code>ProcessorStaticStateOutboundPort</code> and
 * <code>ProcessorDynamicStateOutboundPort</code> to pass these data upon
 * reception from the processor component.
 * 
 * As a client component may receive data from several different processors, it
 * can assign URI to each at the creation of outbound ports, so that these can
 * pass these URI when receiving data. Hence, the methods defined in this
 * interface will be unique in one client component but receive the data pushed
 * by all of the different processors.
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
 * Created on : April 8, 2015
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface ProcessorStateDataConsumerI {
	/**
	 * accept the static data pushed by a processor with the given URI.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	processorURI != null and staticState != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param processorURI URI of the processor sending the data.
	 * @param staticState  static state of this processor.
	 */
	void acceptProcessorStaticData(String processorURI, ProcessorStaticStateI staticState) throws Exception;

	/**
	 * accept the dynamic data pushed by a processor with the given URI.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	processorURI != null and currentDynamicState != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param processorURI        URI of the processor sending the data.
	 * @param currentDynamicState current dynamic state of this processor.
	 */
	void acceptProcessorDynamicData(String processorURI, ProcessorDynamicStateI currentDynamicState)
			throws Exception;
}
