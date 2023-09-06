package Yin.rpc.cousumer.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.HashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import Yin.rpc.cousumer.annotation.RemoteInvoke;
import Yin.rpc.cousumer.core.NettyClient;
import Yin.rpc.cousumer.param.ClientRequest;
import Yin.rpc.cousumer.param.Response;

@Component
public class InvokeProxy implements BeanPostProcessor {
	public static Enhancer enhancer = new Enhancer();

	public Object postProcessAfterInitialization(Object bean, String arg1) throws BeansException {
		return bean;
	}

	//include all the methods and the type of attributes in the Hashmap
	private void putMethodClass(HashMap<Method, Class> methodmap, Field field) {
		Method[] methods = field.getType().getDeclaredMethods();
		for(Method method : methods){
			methodmap.put(method, field.getType());
		}
		
	}

	public Object postProcessBeforeInitialization(Object bean, String arg1) throws BeansException {
//		System.out.println(bean.getClass().getName());
		Field[] fields = bean.getClass().getDeclaredFields();
		for(Field field : fields){
			if(field.isAnnotationPresent(RemoteInvoke.class)){
				field.setAccessible(true);
				
//				final HashMap<Method, Class> methodmap = new HashMap<Method, Class>();
//				putMethodClass(methodmap,field);
//				Enhancer enhancer = new Enhancer();
				enhancer.setInterfaces(new Class[]{field.getType()});
				enhancer.setCallback(new MethodInterceptor() {
					
					public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
						ClientRequest clientRequest = new ClientRequest();
						clientRequest.setContent(args[0]);
//						String command= methodmap.get(method).getName()+"."+method.getName();
						String command = method.getName();//modify
//						System.out.println("the Command at the InvokeProxy is: "+command);
						clientRequest.setCommand(command);
						
						Response response = NettyClient.send(clientRequest);
						return response;
					}
				});
				try {
					field.set(bean, enhancer.create());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return bean;
	}

}
