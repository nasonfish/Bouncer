Bouncer
=======

A simple IRC "bouncer," or proxy, written in Java.

This program connects to an IRC server and relays messages to your IRC client, effectively proxying your connection from your client to wherever the bouncer is running from.

Upon disconnection of your IRC client, this program will save all messages sent through it until reconnection, upon which it will relay all messages that came through.

The bouncer will always store the MOTD and channel join messages so you can always get back to all your channels when you connect.

Multiple clients connecting at the same time will both be relayed all messages sent back through the bouncer.

When you connect, you need to specify a password to login, via `PASS <password_set_on_command_line>`. Usually clients will send a raw message with `/QUOTE` or `/RAW`.


## Manual

    nasonfish - Bouncer v0.1
    
    	-o, --oaddr <address>		Specify an address for the bouncer to connect to.
    	-O, --oport <port>		    Specify a port for the bouncer to connect to.
    	-l, --laddr <address>		Specify an address for the bouncer to listen on.
    	-L, --lport <port>	    	Specify a port for the bouncer to listen on.
    	-b, --bind <address>		Specify an address for the bouncer to bind to.
    	-P, --password <password>	Specify a password to require upon connection.
    	-n, --nick <password>		Specify a nick to use on the IRC network.
    	--help				        Display help.

A password, nick, and outgoing address and port are required to run the program.

## Warning

This has not been scanned throughoutly for errors, I can't guarantee the safety and security of any users that use it.
