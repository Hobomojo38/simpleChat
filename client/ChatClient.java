// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    clientUI.display("Connecting to server " + host + " on port " + port);
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all commands given by
   * the user.
   * @param command The command and its args given by the user.
   */

  protected void runCommand(String command){
    String[] commandArgs = command.split(" ");

    switch (commandArgs[0]) {
      case "quit":
        quit();
        break;
      case "logoff":
        try { closeConnection();} 
        catch (IOException e) {e.printStackTrace();}
        break;
      case "sethost":
        if (isConnected() == false){
          setHost(commandArgs[1]);
        }
        else{
          clientUI.display("Cannot change host while connected");
        }
        break;
      case "setport":
        if (isConnected() == false){
          setPort(Integer.parseInt(commandArgs[1]));
        }
        else{
          clientUI.display("Cannot change port while connected");
        }
        break;
      case "login":
        if (isConnected() == false){
          try { openConnection();} 
          catch (IOException e) {e.printStackTrace();}
        }
        else{
          clientUI.display("Already connected");
        }
        break;
      case "gethost":
      clientUI.display("Current host: " + getHost());
        break;
      case "getport":
      clientUI.display("Current host: " + getPort());
        break;
      default:
        clientUI.display("Invalid command");
        break;
    }
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    if (message.charAt(0) == '#') //If the message is a command
    {
      runCommand(message);
    }
    else
    {
      try
      {
        sendToServer(message);
      }
      catch(IOException e)
      {
        clientUI.display
          ("Could not send message to server.  Terminating client.");
        quit();
      }
    }
  }

  /**
   * This method is called each time an exception is thrown by the
   * client's thread that is waiting for messages from the server.
   * 
   * @param exception the exception raised.
   */
  protected void connectionException(Exception exception) {
    quit();
  }

  /**
   * This method informs the client of the server's closing and
   * terminates the client.
   */
  protected void connectionClosed() {
    clientUI.display("Connection with server closed.");
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
