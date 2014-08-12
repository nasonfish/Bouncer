package com.nasonfish.irc.bouncer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetSocketAddress;

public class Connecter implements Runnable {

	private Bouncer opts;
	private PrintWriter out;
	private BufferedReader in;
	public String buffer = "";
	
	public Connecter(Bouncer opts){
		this.opts = opts;
	}
	
	@Override
	public void run() {
		Socket sock = null;
		//PrintWriter log = null;
		while(true){
			try {
				//log = new PrintWriter(new FileWriter("~/.bouncerlog.txt", true));
				sock = new Socket(opts.outAddress, opts.outPort);
				if(opts.bindHost != null){
					sock.bind(new InetSocketAddress(opts.bindHost, 0));
				}
				out = new PrintWriter(sock.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				write("NICK " + opts.nick);
				write("USER " + opts.nick + " 0 * :" + opts.nick);
				String line;
				while((line = in.readLine()) != null){
					String _line = line;
					System.out.println(line);
					if(line.startsWith(":")){
						_line = line.replaceFirst("^[^ ]+ ", "");
					}
					if(_line.startsWith("PING ")){
						write("PONG " + _line.substring(5));
					} else if(_line.startsWith("433")){  // nickname already in use
						opts.nick += "_";
						write("NICK :" + opts.nick);
					} else if(_line.startsWith("432")){  // erroneous nickname
						opts.nick = "BouncerUser";
						write("NICK :" + opts.nick);
					} else {
						for(Listener l : Listener.users){
							l.write(line);
						}
						buffer += line + "\n";
						//log.println(line);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(!sock.isClosed()){
						sock.close();
					}
					//log.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void write(String line){
		System.out.println(">>> " + line);
		this.out.println(line);
	}

}
