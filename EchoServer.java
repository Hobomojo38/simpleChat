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

  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the server.
   */
  private static ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * @param serverUI The interface of the server.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
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
    // Check for first message from client
    String[] splStrings = ((String) msg).split(" ");

    if (splStrings[0].equals("#login")){
      if (client.getInfo("loginID") == null){ // If client has not logged in yet
        client.setInfo("loginID", splStrings[1]); // Set client's login ID

        serverUI.display("A new client has connected to the server.");
        serverUI.display(splStrings[1] + " has logged on.");

        this.sendToAllClients(splStrings[1] + " has logged on."); // Send message to all clients
      } else { // If client has already logged in, disconnect them
        try {
          client.sendToClient("You are already logged in.");
          close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    else { // If client has already logged in, echo message to all clients
      System.out.println("Message received: \"" + msg + "\" from " + client.getInfo("loginID"));
      this.sendToAllClients((String) client.getInfo("loginID") + "> " + msg);
    }
  }

  /**
   * This method handles any messages received from the UI
   * @param message String message received from the UI
   */
  public void handleMessageFromServerUI(String message)
  {
    // Check for command
    if(message.charAt(0) == '#') {
      runCommand(message);
    }
    else
    { // If no command, send message to all clients and echo back
      // to the serverUI
      serverUI.display("SERVER MSG> " + message);
      this.sendToAllClients("SERVER MSG> " + message);
    }
  }

  /**
   * This method handles any command received from the UI
   * @param message String command received from the UI with command and arguments
   */
  protected void runCommand(String message)
  {
    // Split message into command and arguments
    String[] commandArgs = message.split(" ");

    // Runs the appropriate command
    switch(commandArgs[0])
    {
      case "#quit": // Closes the server and terminates the program

        stopListening();

        try {
          close();
        } catch (IOException e1) { e1.printStackTrace(); }

        System.exit(0);
        break;

      case "#stop": // Stops listening for new clients
        stopListening();
        break;

      case "#close": // Disconnects all clients and stops listening for new clients
        try { close(); }
        catch(IOException e) { serverUI.display("Error closing server."); }
        break;

      case "#setport": // Sets the port number to listen on
        if(getNumberOfClients() == 0 && !isListening()) {
          try { setPort(Integer.parseInt(commandArgs[1])); }
          catch(NumberFormatException e)
          { serverUI.display("Error: Invalid port number."); }
        }
        else {
          serverUI.display("Error: Cannot change port number while server is running.");
        }
        break;

      case "#start": // Starts listening for new clients
        if(!this.isListening()) {
          try{
            listen();
          }
          catch(IOException e) {
            serverUI.display("Error: Could not listen for clients.");
          }
        }
        else {
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
    System.out.println("Client disconnected: " + client.getInfo("loginID"));
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
    int port = -1; //Filled to avoid compiler error

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    serverUI = new ServerConsole(port);
    EchoServer sv = new EchoServer(port, serverUI);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
