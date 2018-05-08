package io.vertx.httpproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.httpproxy.admin.AdminManager;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Main {

  @Parameter(names = "--port")
  public int port = 8090;

  @Parameter(names = "--address")
  public String address = "0.0.0.0";
  
  @Parameter(names = "--remoteHost")
  public String remoteHost = "localhost";
  
  @Parameter(names = "--remotePort")
  public int remotePort = 8087;

  public static void main(String[] args) {
    Main main = new Main();
    JCommander jc = new JCommander(main);
    jc.parse(args);
    main.run();
    
    
  }

  public void run() {
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
    Vertx vertx = Vertx.vertx();
    HttpClient client = vertx.createHttpClient(new HttpClientOptions()
        .setMaxInitialLineLength(10000)
        .setLogActivity(true));
    HttpProxy proxy = HttpProxy
        .reverseProxy(client)
        .target(remotePort, remoteHost);

    HttpServer proxyServer = vertx.createHttpServer(new HttpServerOptions()
        .setPort(port)
        .setMaxInitialLineLength(10000)
        .setLogActivity(true))
    		
        .requestHandler(req -> {
          System.out.println("------------------------------------------");
          System.out.println(req.path());
          try {
	          if(req.path().equals("/")) {
	        	  	//proxy.handle(new HttpServerRequestCustom(req));
	        	  	writePage(req);
	        	  	
	          }else if(req.path().equals("/js/index.js")) {
	      	  	//proxy.handle(new HttpServerRequestCustom(req));
	      	  	writeScript(req);
	      	  	
	        }else if(req.path().equals("/partials/admin/alert.html")) {
	      	  	//proxy.handle(new HttpServerRequestCustom(req));
	      	  	writeAlert(req);
	      	  	
	        }else {
	        	  	proxy.handle(req);
	          }
          }catch(Exception e) {
        	    e.printStackTrace();
          }
        });
    proxyServer.listen(ar -> {
      if (ar.succeeded()) {
        System.out.println("Proxy server started on " + port);
      } else {
        ar.cause().printStackTrace();
      }
    });
  }
  
  private void writePage(HttpServerRequest req) throws MalformedURLException, IOException {
	  new AdminManager(this.remoteHost,this.remotePort).enrichRequest(req);
  }
  
  private void writeScript(HttpServerRequest req) throws IOException {
	  new AdminManager(this.remoteHost,this.remotePort).enrichJavaScriptRequest(req);
  }

  private void writeAlert(HttpServerRequest req) throws IOException {
	  new AdminManager(this.remoteHost,this.remotePort).enrichJAlertRequest(req);
  }

  
  
}
  
