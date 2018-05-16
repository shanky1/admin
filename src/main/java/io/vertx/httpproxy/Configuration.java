package io.vertx.httpproxy;

import lombok.Data;

@Data
public class Configuration {
	
	 private String host;
	 private int port;
	 private String remoteHost;
	 private int remotePort;

}
