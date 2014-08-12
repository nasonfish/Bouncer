package com.nasonfish.irc.bouncer;

import java.io.IOException;
import java.util.TimerTask;

public class Pinger extends TimerTask {

	@Override
	public void run() {
		for(Listener l : Listener.users){
			if(l.lastMessage + (1000 * 60 * 5) < System.currentTimeMillis()){
				try {
					l.close();
				} catch (IOException e) {}
				Listener.users.remove(l);
			} else {
				l.write("PING :irc.nasonfish.com");
			}
		}
	}
}
