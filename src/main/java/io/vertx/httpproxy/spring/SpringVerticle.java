package io.vertx.httpproxy.spring;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.camel.CamelContext;
import org.apache.camel.component.vertx.VertxComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.httpproxy.admin.AdminManager;
import io.vertx.httpproxy.camel.CamelRoute;
import lombok.extern.slf4j.Slf4j;

@Component
//Prototype scope is needed as multiple instances of this verticle will be deployed
@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class SpringVerticle extends AbstractVerticle{
	
	  
	  @Autowired
	  private AdminManager adminManager;
	  
	  @Autowired 
	  private Configuration config;
	  
//	  @Autowired
//	  CamelContext camelContext;
//	  
//	  @Autowired
//	  CamelRoute camelRoute;

	  
	  @Override
	  public void start(Future<Void> startFuture) throws Exception {
		  
		  log.info("Starting vertical");
		  
//		  camelContext.addRoutes(camelRoute);
//		  camelContext.start();
		  
//		  VertxComponent vertxComponent = new VertxComponent();
//		  vertxComponent.setVertx(vertx);
//		  camelContext.addComponent("vertx", vertxComponent);

		  
		  
		  HttpClient client = vertx.createHttpClient(new HttpClientOptions()
			        .setMaxInitialLineLength(10000)
			        .setLogActivity(true));
			    HttpProxy proxy = HttpProxy
			        .reverseProxy(client)
			        .target(config.getRemotePort(), config.getRemoteHost());
			    
			    
			    HttpServer proxyServer = vertx.createHttpServer(new HttpServerOptions()
			        .setPort(config.getPort())
			        .setMaxInitialLineLength(10000)
			        .setLogActivity(true))
			    		
			        .requestHandler(req -> {
			        	  
			          log.info("------------------------------------------");
			          log.info(req.path());
			          
			          try {
			        	  
				        if(req.path().equals("/")) {
				        	  	writePage(req);
					        }else if(req.path().equals("/js/index.js")) {
					      	  	writeScript(req);
					      	  	
					        }else if(req.path().equals("/admin/alertConfig")) {
					      	  	writeAlertConfig(req);
					      	  	
					        }else if(req.path().equals("/admin/logConfig")) {
					      	  	writeLogConfig(req);
					      	  	
					        }else if(req.path().equals("/partials/admin/alert.html")) {
					      	  	writeAlert(req);
					      	  	
					        }else if(req.path().equals("/partials/admin/log.html")) {
					      	  	writeLog(req);
					      	  	
					        }else {
				        	  	proxy.handle(req);
				          }
				        
			          }catch(Exception e) {
			        	    log.error("error while serving request for path {}",req.path(),e);
			          }
			          
			        });
			    
			    proxyServer.listen(ar -> {
			      if (ar.succeeded()) {
			        log.info("Proxy server started on {} ",config.getPort());
			      } else {
			       log.error("Error while starting server", ar.cause());
			      }
			    });
			    
			  }
			  
			  private void writeLog(HttpServerRequest req) throws MalformedURLException, IOException {
				  adminManager.enrichLogRequest(req);
				
			}

			private void writeLogConfig(HttpServerRequest req) throws IOException {
				  adminManager.fetchLogConfig(req);
			}
			  
			  private void writeAlertConfig(HttpServerRequest req) throws IOException {
				  adminManager.fetchAlertConfig(req);
			}
			 
			private void writePage(HttpServerRequest req) throws MalformedURLException, IOException {
				  adminManager.enrichRequest(req);
			  }
			  
			  private void writeScript(HttpServerRequest req) throws IOException {
				  adminManager.enrichJavaScriptRequest(req);
			  }

			  private void writeAlert(HttpServerRequest req) throws IOException {
				  adminManager.enrichAlertRequest(req);
			  }

			  
}

