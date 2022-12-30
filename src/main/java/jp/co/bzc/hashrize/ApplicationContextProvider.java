package jp.co.bzc.hashrize;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext CONTEXT;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		ApplicationContextProvider.CONTEXT = context;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getBean(Class clazz) {
		return ApplicationContextProvider.CONTEXT.getBean(clazz);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getBean(String qualifier, Class clazz) {
		return ApplicationContextProvider.CONTEXT.getBean(qualifier , clazz);
	}	
	public static Object getProperty(String name) {
		return ApplicationContextProvider.CONTEXT.getEnvironment().getProperty(name);
	}	
}
