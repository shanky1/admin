package io.vertx.httpproxy.spring;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
@org.springframework.context.annotation.Configuration
public class Configuration {
	
	  @Value("${port:8090}")
	  private int port;

	  @Value("${host:localhost}")
	  private String host;
	  
	  @Value("${remoteHost:localhost}")
	  private String remoteHost;
	  
	  @Value("${remotePort:8087}")
	  private int remotePort;
	  
//	  @Autowired
//	  CamelRoute cRoute;
	  
	  
//	  @Bean
//	  /**
//	   * Should not be called multiple time.
//	   * @return
//	   * @throws Exception
//	   */
//	  public CamelContext getCamelContext() throws Exception {
//		  CamelContext camelContext = new DefaultCamelContext();    
//		  camelContext.addRoutes(cRoute);
//		  camelContext.start();
//		  return camelContext;  
//		  
//	  }

}
