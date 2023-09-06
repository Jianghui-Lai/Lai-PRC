# Introduction

**This is my personal project of a light version of RPC based on Netty, Zookeeper and Spring. I would love to hear any feedback, please email me at laijianghui@gmail.com**

# Features

- **HTTP persistent connection**
- **asynchronous communication**
- **Keep Alive**
- **JSON Serialization**
- **Nearly zero configuration, implemented based on annotations for invocation**
- **Service Registry(Zookeeper)**
- **Automatically sending client requests based on annotations with proxy mechanism**
- **client-side service monitoring and discovery**
- **server-side service registration**
- **Netty 4.X version**

# Quick Start

### Server

- **Add your own service under the server's Service and annotate it with @Service**
<pre>
@Service
public class TestService {
	public void test(User user){
		System.out.println("use TestService.test");
	}
}
</pre>

- **Generate a service interface and create a class that implements this interface**
  ###### The interface is defined as follows:
  <pre>
  public interface TestRemote {
  	public Response testUser(User user);  
  }
  </pre>
  ###### The implementation class is as follows. Add the @Remote annotation to your implementation class. This class is where you actually call the service, and you can generate any form of Response that you want to return to the client.
  <pre> 
  @Remote
  public class TestRemoteImpl implements TestRemote{
  	@Resource
  	private TestService service;
  	public Response testUser(User user){
  		service.test(user);
  		Response response = ResponseUtil.createSuccessResponse(user);
  		return response;
  	}
  }	
  </pre>

### Client

- **Create an interface on the client side**
<pre>
public interface TestRemote {
	public Response testUser(User user);
}
</pre>

### Usage

- **In the place where you want to use the interface, create a property in the form of an interface and annotate it with @RemoteInvoke**
<pre>
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RemoteInvokeTest.class)
@ComponentScan("\\")
public class RemoteInvokeTest {
	@RemoteInvoke
	public static TestRemote userremote;
	public static User user;
	@Test
	public void testSaveUser(){
		User user = new User();
		user.setId(1000);
		user.setName("somebody");
		userremote.testUser(user);
	}
}	
</pre>

# Overview

![Markdown](https://s1.ax1x.com/2018/07/06/PZK3SP.png)
