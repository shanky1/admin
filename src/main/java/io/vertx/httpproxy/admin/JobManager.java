package io.vertx.httpproxy.admin;

import java.util.Iterator;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.httpproxy.Configuration;

public class JobManager {
	
	private Configuration config;
	WebClient client = null;
	
	public JobManager(Configuration config) {
	this.config = config;
		WebClientOptions options = new WebClientOptions()
				  .setUserAgent("Admin/1.0.0");
		options.setKeepAlive(false);
	    client = WebClient.create(Vertx.vertx(),options);
	}
	
	public void getJobList(Handler<JsonObject> handler) {
		client
		  .get(this.config.getRemotePort(), this.config.getRemoteHost(), "/jobs")
		  .as(BodyCodec.jsonObject())
		  .send(ar -> {
		    if (ar.succeeded()) {
		    		HttpResponse<JsonObject> response = ar.result();
		        JsonObject body = response.body();
		        handler.handle(body);
		    } else {
		    		System.out.println("Something went wrong " + ar.cause().getMessage());
		    }
		  });
		
	}
	
	public static void main(String args[]) {
		Configuration config = new Configuration();
		config.setRemoteHost("localhost");
		config.setRemotePort(8089);
		new JobManager(config).getJobList(json->{
			JsonArray ja = json.getJsonArray("jobs");
			Iterator itr = ja.iterator();
			while(itr.hasNext()) {
				System.out.println(itr.next());
			}
			
			System.out.println(String.format(" body %s",  json) );
		});
	}

}
