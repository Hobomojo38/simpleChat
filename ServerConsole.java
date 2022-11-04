import java.io.*;
import java.util.Scanner;

import common.*;

public class ServerConsole implements ChatIF{

    // Instance variables **********************************************

    /**
     * Scanner to read from the console
     */
    static Scanner console;

    /**
     * Instance of the server that is tied to this console
     */
    static EchoServer server;

    // Constructors ****************************************************
    /**
     * Constructs an instance of the ServerConsole UI.
     * 
     * @param port
     */
    ServerConsole(int port){

        server = new EchoServer(port, this);

        try {
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

        console = new Scanner(System.in);

    }

    // Instance methods ************************************************
    /**
     * Overridden method from ChatIF. This method is prints a message
     * to the console.
     */
    @Override
    public void display(String message) {
        System.out.println("> " + message);
        
    }
    
    /**
     * This method waits for input from the console. Once it is received,
     * it sends it to the server's message handler.
     */
    public void waitForMessage(){
        while (true){
            String message = console.nextLine();
            server.handleMessageFromServerUI(message);
        }
    }

    // Class methods ***************************************************
    /**
     * This method is responsible for the creation of the Server UI.
     * 
     * @param args[0] The port number to listen on. Defaults to 5555 if no argument is entered.
     */
    public static void main(String[] args) {
        int port; //Port to listen on

        try
        {
            port = Integer.parseInt(args[0]); //Get port from command line
        }
        catch(Throwable t)
        {
            port = 5555; //Set port to 5555
        }

        ServerConsole chat = new ServerConsole(port);
        
        chat.waitForMessage();
    }
}
