package io.vertx.httpproxy.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import io.vertx.core.http.HttpServerRequest;

public class AdminManager {
	
	private String host;
	private int port;
	
	public AdminManager(String host, int port) {
		this.host = host;
		this.port = port;
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
		 	URL local = new URL("http",host,port,"/js/index.js");
	        String content = readOnlineContent(local);
	        StringBuilder sb  = new StringBuilder(content);
	        insertState(sb);
	        writeToRespose(req, sb.toString());
		
	}
	
	
	private void insertState(StringBuilder sb) throws IOException {
		sb.append("\r\n").append(readFile(HTMLStuff.JAVASCRIPT.fileName()));
	}


	public void enrichRequest(HttpServerRequest req) throws MalformedURLException, IOException {
		
			  
			  Document doc = Jsoup.parse(new URL("http", host, port,"/"),60000);
			  insertAdminMenu(doc);
	          
	          String content = doc.toString();
	          System.out.println(content);
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

	public void enrichJAlertRequest(HttpServerRequest req) throws IOException {
		String content = readFile(HTMLStuff.ALERT.fileName());
		writeToRespose(req, content);
	}

}
