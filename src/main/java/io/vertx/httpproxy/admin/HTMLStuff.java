package io.vertx.httpproxy.admin;

public enum HTMLStuff {
	
	MENU("menu.html"),
	JAVASCRIPT("index.js"), 
	ALERT("alert.html");
	
	
	private String fileName;
	private HTMLStuff(String fileName) {
		this.fileName = fileName;
	}
	
	public String fileName() {
		return this.fileName;
	}

}
