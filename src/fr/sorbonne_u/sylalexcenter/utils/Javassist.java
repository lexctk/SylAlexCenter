package fr.sorbonne_u.sylalexcenter.utils;

import java.lang.reflect.Method;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationServicesI;
import fr.sorbonne_u.sylalexcenter.application.interfaces.ApplicationSubmissionI;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public abstract class Javassist {
	
	private final static Class<?> abstractConnectorClass = AbstractConnector.class;
	private final static String packageName = "fr.sorbonne_u.sylalexcenter.utils";
	
	private static Class<?> applicationNotificationConnector;
	private static Class<?> applicationServicesConnector;
	private static Class<?> applicationSubmissionConnector;
	private static Class<?> requestNotificationConnector;
	private static Class<?> requestSubmissionConnector;

	
	public synchronized static String getApplicationNotificationConnectorClassName() throws Exception {
		
		if (applicationNotificationConnector == null) {			
			applicationNotificationConnector = makeConnectorClass("ApplicationNotificationConnector", ApplicationNotificationI.class);
		}
		
		return applicationNotificationConnector.getCanonicalName();		
	}
	
	public synchronized static String getApplicationServicesConnectorClassName() throws Exception {
		
		if (applicationServicesConnector == null) {			
			applicationServicesConnector = makeConnectorClass("ApplicationServicesConnector", ApplicationServicesI.class);
		}
		
		return applicationServicesConnector.getCanonicalName();		
	}
	
	public synchronized static String getApplicationSubmissionConnectorClassName() throws Exception {
		
		if (applicationSubmissionConnector == null) {			
			applicationSubmissionConnector = makeConnectorClass("ApplicationSubmissionConnector", ApplicationSubmissionI.class);
		}
		
		return applicationSubmissionConnector.getCanonicalName();
	}

	public synchronized static String getRequestNotificationConnectorClassName() throws Exception {
		
		if (requestNotificationConnector == null) {			
			requestNotificationConnector = makeConnectorClass("RequestNotificationConnector", RequestNotificationI.class);
		}
		
		return requestNotificationConnector.getCanonicalName();			
	}

	public synchronized static String getRequestSubmissionConnectorClassName() throws Exception {
	
		if (requestSubmissionConnector == null) {			
			requestSubmissionConnector = makeConnectorClass("RequestSubmissionConnector", RequestSubmissionI.class);
		}
		
		return requestSubmissionConnector.getCanonicalName();					
	}	
	
	private static Class<?> makeConnectorClass(
			String className,
			Class<?> connectorImplementedInterface) throws Exception {
		
		ClassPool pool = ClassPool.getDefault();
		CtClass cs = pool.get(abstractConnectorClass.getCanonicalName());
		CtClass cii = pool.get(connectorImplementedInterface.getCanonicalName());		
		CtClass connectorCtClass = pool.makeClass(packageName + className);
				
		connectorCtClass.setSuperclass(cs);
		
		Method[] methodsToImplement = connectorImplementedInterface.getDeclaredMethods();

		for (Method aMethodsToImplement : methodsToImplement) {

			StringBuilder source = new StringBuilder("public ");
			source.append(aMethodsToImplement.getReturnType().getTypeName()).append(" ");
			source.append(aMethodsToImplement.getName()).append("(");

			Class<?>[] pt = aMethodsToImplement.getParameterTypes();
			StringBuilder callParam = new StringBuilder();

			for (int j = 0; j < pt.length; j++) {
				String pName = "arg" + j;
				source.append(pt[j].getCanonicalName()).append(" ").append(pName);
				callParam.append(pName);
				if (j < pt.length - 1) {
					source.append(", ");
					callParam.append(", ");
				}
			}
			source.append(")");
			Class<?>[] et = aMethodsToImplement.getExceptionTypes();
			if (et.length > 0) {
				source.append(" throws ");

				for (int z = 0; z < et.length; z++) {
					source.append(et[z].getCanonicalName());
					if (z < et.length - 1) {
						source.append(",");
					}
				}
			}
			source.append(" {\n\n	return ((");
			source.append(connectorImplementedInterface.getCanonicalName()).append(")this.offering).");
			source.append(aMethodsToImplement.getName());
			source.append("(").append(callParam).append(");\n}");
			CtMethod theCtMethod = CtMethod.make(source.toString(), connectorCtClass);
			connectorCtClass.addMethod(theCtMethod);
		}
		
		connectorCtClass.setInterfaces(new CtClass[]{cii});
		cii.detach(); 
		cs.detach();		
		Class<?> ret = connectorCtClass.toClass();
		connectorCtClass.detach();
					
		return ret;
	}
}