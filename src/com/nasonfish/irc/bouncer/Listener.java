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
		lastMessage = System.currentTimeMillis();
		try{
			Listener.users.add(this);
			String line;
			while((line = in.readLine()) != null){
				lastMessage = System.currentTimeMillis();
				if(line.startsWith("PONG ")){
					// who cares
				} else if(line.startsWith("QUIT ")) {
					Listener.users.remove(this);
					this.user.close();
				} else {
					opts.connecter.write(line);
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
