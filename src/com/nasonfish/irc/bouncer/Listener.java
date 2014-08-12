package com.nasonfish.irc.bouncer;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Listener implements Runnable {

	private Socket user;
	private Bouncer opts;

	private PrintWriter out;
	private BufferedReader in;
	public static List<Listener> users = Collections.synchronizedList(new ArrayList<Listener>());
	protected long lastMessage;
	
	public Listener(Bouncer opts, Socket user) throws IOException{
		this.user = user;
		out = new PrintWriter(user.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(user.getInputStream()));
		this.opts = opts;
	}
	
	@Override
	public void run() {
		try{
			String line;
			out.write(":irc.nasonfish.com 464 " + this.user + " :Password required");
			out.write(":irc.nasonfish.com NOTICE AUTH :You need to send your password, via PASS <password_from_command_line>");
			while((line = in.readLine()) != null){
				lastMessage = System.currentTimeMillis();
				if(line.substring(0, 4).startsWith("PASS")){
					if(!line.substring(5).equals(opts.password)){
						write(":irc.nasonfish.com 464 " + this.user + " :Invalid Password");
						this.user.close();
						return;
					} else {
						break;
					}
				} else if(line.substring(0, 4).equalsIgnoreCase("QUIT")) {
					this.user.close();
					return;
				}
			}
		} catch(IOException e){
			try {
				if(!this.user.isClosed()){
					this.user.close();
					return;
				}
			} catch (IOException e1) {
			}
			return;  // just go away please
		}
		lastMessage = System.currentTimeMillis();
		out.println(opts.getConnector().getBuffer());
		out.println(opts.getConnector().getLogBack());
		opts.getConnector().clearLogBack();
		try{
			Listener.users.add(this);
			String line;
			while((line = in.readLine()) != null){
				lastMessage = System.currentTimeMillis();
				if(line.startsWith("PONG ")){
					// who cares
				} else if(line.substring(0, 4).equalsIgnoreCase("QUIT")) {
					Listener.users.remove(this);
					this.user.close();
					return;
				} else if(line.substring(0, 4).equalsIgnoreCase("USER")){
					// also who cares
				} else {
					opts.getConnector().write(line);
				}
			}
		} catch(IOException e){
			try {
				if(!this.user.isClosed()){
					this.user.close();
				}
			} catch (IOException e1) { // I didn't need you anyway!
			}
		} finally {
			Listener.users.remove(this);
		}
	}
	
	public void write(String line){
		out.println(line);
	}
	
	public void close() throws IOException{
		user.close();
	}
	
}
