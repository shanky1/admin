package io.vertx.httpproxy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.httpproxy.spring.SpringVerticle;
import io.vertx.httpproxy.spring.SpringVerticleFactory;

@org.springframework.context.annotation.Configuration
@ComponentScan("io.vertx.httpproxy")
@PropertySource("classpath:application.properties")
public class MainApplication {
	
	
	
	public static void main(String[] args) throws Exception {
		InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
	    Vertx vertx = Vertx.vertx();

	    ApplicationContext context = new AnnotationConfigApplicationContext(MainApplication.class);

	    VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);
//	    CamelContext cContext = context.getBean(CamelContext.class);
//	    cContext.start();

	    // The verticle factory is registered manually because it is created by the Spring container
	    vertx.registerVerticleFactory(verticleFactory);

	    // Scale the verticles on cores: create 4 instances during the deployment
	    DeploymentOptions options = new DeploymentOptions().setInstances(4);
	    vertx.deployVerticle(verticleFactory.prefix() + ":" + SpringVerticle.class.getName(), options);
	  }

	

}
