package com.nasonfish.irc.bouncer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;

public class Bouncer {
	
	public String nick = "BouncerUser";
	public int port = 6667;
	public String address = "0.0.0.0";
	public int outPort;
	public String outAddress;
	public String bindHost = null;
	//public boolean ssl = false;
	//public boolean cert_check = false;
	public String password;
	
	private ServerSocket ssocket;
	
	public Connecter connecter;
	
	public static void main(String[] args){
		Bouncer b = new Bouncer();
		b.init(args);
	}
	
	public void init(String[] args){
		int tmp_i = 0;
		if(args.length == 0){
			args = new String[]{"--help"}; // ugh, messy
		}
		for(int i = 0; i < args.length; i += 1 + tmp_i){
			System.out.println(i);
			tmp_i = 0;
			if(args[i].equalsIgnoreCase("--help")){
				System.out.println("nasonfish - Bouncer v0.1");
				System.out.println("\n\t-o, --oaddr <address>\t\tSpecify an address for the bouncer to connect to.");
				System.out.println("\t-O, --oport <port>\t\tSpecify a port for the bouncer to connect to.");
				System.out.println("\t-l, --laddr <address>\t\tSpecify an address for the bouncer to listen on.");
				System.out.println("\t-L, --lport <port>\t\tSpecify a port for the bouncer to listen on.");
				System.out.println("\t-b, --bind <port>\t\tSpecify an address for the bouncer to bind to.");
				System.out.println("\t-P, --password <password>\t\tSpecify a password to require upon connection.");
				System.out.println("\t-n, --nick <password>\t\tSpecify a nick to use on the IRC network.");
				System.out.println("\t--help\t\tDisplay help.");
				System.exit(0);
			}
			if(!args[i].startsWith("--")){
				for(int j = 1; j < args[i].length(); j++){
					tmp_i += this.parseArg(Character.toString(args[i].charAt(j)), args[i+tmp_i+1]);
				}
			} else {
				tmp_i += this.parseArg(args[i].substring(2), args[i+1]);
			}
		}
		connecter = new Connecter(this);
		new Thread(connecter).start();
		new Timer().scheduleAtFixedRate(new Pinger(), 1000*60*3, 1000*60*3);
		// listen for connections
		try {
			ssocket = new ServerSocket(port, 50, InetAddress.getByName(this.address));
			while(true){ // Accept new connections.
				Socket usock = ssocket.accept();
				Runnable connHandler = new Listener(this, usock);
				new Thread(connHandler).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int parseArg(String arg, String nextArg){
		switch(arg){
			case "o":
			case "oaddr":
				this.outAddress = nextArg;
				return 1;
			case "O":
			case "oport":
				this.outPort = Integer.parseInt(nextArg);
				return 1;
			case "l":
			case "laddr":
				this.address = nextArg;
				return 1;
			case "L":
			case "lport":
				this.port = Integer.parseInt(nextArg);
				return 1;
			case "b":
			case "bind":
				this.bindHost = nextArg;
				return 1;
//			case "S":
//			case "ssl":
//				this.ssl = true;
//				return 0;
//			case "V":
//			case "verify":
//				this.cert_check = true;
//				return 0;
			case "P":
			case "password":
				this.password = nextArg;
				return 1;
			case "n":
			case "nick":
				this.nick = nextArg;
				return 1;
			default:
				System.out.println("Unrecognized flag: " + arg);
				return 0;
		}
	}
}
