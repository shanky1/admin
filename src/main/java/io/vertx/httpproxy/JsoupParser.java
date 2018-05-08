package io.vertx.httpproxy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JsoupParser {
	
	public static void main(String args[]) throws MalformedURLException, IOException {
		Document doc = Jsoup.parse(new URL("http://localhost:8087"), 30000);
		Element element = doc.selectFirst("[id=sidebar]").selectFirst("ul.nav").append("<li><a ui-sref=\"admin\" ui-sref-active=\"active\" href=\"#/admin\"><i class=\"fa fa-user fa-fw\"></i>Admin</a></li>");
		System.out.println(doc.toString());
		
	}

}
