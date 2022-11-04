// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

  private static ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    String[] splStrings = ((String) msg).split(" ");
    if (splStrings[0].equals("#login")){
      if (client.getInfo("loginID") == null){
        serverUI.display("Client " + client.getId() + " has logged in as " + splStrings[1]);
        client.setInfo("loginID", splStrings[1]);
      } else {
        try {
          client.sendToClient("You are already logged in.");
          close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    else {
      System.out.println("Message received: \"" + msg + "\" from " + client.getInfo("loginID") + " (" + client + ")");
      this.sendToAllClients((String) client.getInfo("loginID") + "> " + msg);
    }
  }

  public void handleMessageFromServerUI(String message)
  {
    if(message.charAt(0) == '#')
    {
      runCommand(message);
    }
    else
    {
      serverUI.display("SERVER MSG> " + message);
      this.sendToAllClients("SERVER MSG> " + message);
    }
  }

  protected void runCommand(String message)
  {
    String[] commandArgs = message.split(" ");
    switch(commandArgs[0])
    {
      case "#quit":

        stopListening();

        try {
          close();
        } catch (IOException e1) { e1.printStackTrace(); }

        System.exit(0);

        break;
      case "#stop":
        stopListening();
        break;
      case "#close":
        try { close(); }
        catch(IOException e) { serverUI.display("Error closing server."); }
        break;
      case "#setport":
        if(getNumberOfClients() == 0 && !isListening())
        {
          try { setPort(Integer.parseInt(commandArgs[1])); }
          catch(NumberFormatException e)
          { serverUI.display("Error: Invalid port number."); }
        }
        else
        {
          serverUI.display("Error: Cannot change port number while server is running.");
        }
        break;
      case "#start":
        if(!this.isListening())
        {
          try
          {
            listen();
          }
          catch(IOException e)
          {
            serverUI.display("Error: Could not listen for clients.");
          }
        }
        else
        {
          serverUI.display("Error: Server is already listening for clients.");
        }
        break;
      case "#getport":
        serverUI.display("Port: " + getPort());
        break;
      default:
        serverUI.display("Error: Invalid command.");
        break;
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when a client connects.
   */
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("Client connected: " + client.getName());
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when a client disconnects.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    System.out.println("Client disconnected: " + client.getName());
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }

    serverUI = new ServerConsole(port);
  }
}
//End of EchoServer class
