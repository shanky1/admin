package io.vertx.httpproxy.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.httpproxy.Configuration;
import wiremock.com.google.common.collect.Lists;
import wiremock.com.google.common.collect.Maps;
import wiremock.org.apache.commons.collections4.map.HashedMap;

public class AdminManager {
	
	private Configuration conf;
	private JobManager jm;
	
	public AdminManager(Configuration conf) {
		this.conf = conf;
		this.jm = new JobManager(conf);
	}
	
	private String readOnlineContent(URL url) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        	StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
            sb.append(inputLine);
        }
        in.close();
        
        return sb.toString();
	}
	
	public void enrichJavaScriptRequest(HttpServerRequest req) throws IOException {
		 	URL local = new URL("http",conf.getRemoteHost() , conf.getRemotePort(),"/js/index.js");
	        String content = readOnlineContent(local);
	        StringBuilder sb  = new StringBuilder(content);
	        insertState(sb);
	        writeToRespose(req, sb.toString());
		
	}
	
	
	private void insertState(StringBuilder sb) throws IOException {
		String content = readFile(HTMLStuff.JAVASCRIPT.fileName());
		content = content.replaceAll("<HOST>", conf.getHost()).replaceAll("<PORT>",String.valueOf(conf.getPort()));
		sb.append("\r\n").append(content);
	}


	public void enrichRequest(HttpServerRequest req) throws MalformedURLException, IOException {
		
			  
			  Document doc = Jsoup.parse(new URL("http", conf.getRemoteHost(), conf.getRemotePort(),"/"),60000);
			  insertAdminMenu(doc);
	          
	          String content = doc.toString();
	          writeToRespose(req, content);
	      
		
	}
	
	private void insertAdminMenu(Document doc) throws IOException {
		doc.selectFirst("[id=sidebar]").selectFirst("ul.nav").append(readFile(HTMLStuff.MENU.fileName()));
	}
	
	
	private String readFile(String file) throws IOException  {
		URL url = Resources.getResource(file);
		return Resources.toString(url, Charsets.UTF_8);
	}
	
	private void writeToRespose(HttpServerRequest req,String content) {
		long length = content.getBytes().length;
        req.response().headers().add("Content-Length", String.valueOf(length));
        req.response().write(content);
        req.response().end();
        req.response().close();
	}

	public void enrichAlertRequest(HttpServerRequest req) throws IOException {
		String content = readFile(HTMLStuff.ALERT.fileName());
		writeToRespose(req, content);
	}

	public void fetchAlertConfig(HttpServerRequest req) throws IOException {
		this.jm.getJobList(jsonResponse->{
			JsonArray ja = jsonResponse.getJsonArray("jobs");
			int size = ja.size();
			List<Map<String,String>> config = Lists.newLinkedList();
			List<Map<String,String>> history = Lists.newLinkedList();
			Map<String, String> entry;
			for(int i=0;i<size;i++) {
				entry = Maps.newHashMap();
				JsonObject jo = ja.getJsonObject(i);
				entry.put("job_id", jo.getString("id"));
				entry.put("job_name", "example");
				entry.put("status", jo.getString("status"));
				entry.put("alert_time", "Sunday");
				entry.put("emails", "sometest@email.com");
				config.add(entry);
				history.add(entry);
			}
			
			Map<String,List<Map<String,String>>> jobsMap = new HashedMap<>();
			jobsMap.put("config", config);
			jobsMap.put("history", config);
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				String content = mapper.writeValueAsString(jobsMap);
				writeToRespose(req, content);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
		});
		
	}

	public void fetchLogConfig(HttpServerRequest req) throws IOException {
		String content = readFile("dummy-log-list.json");
		writeToRespose(req, content);
		
	}

	public void enrichLogRequest(HttpServerRequest req) throws IOException {
		writeToRespose(req, readFile(HTMLStuff.LOG.fileName()));
		
		
	}

}
