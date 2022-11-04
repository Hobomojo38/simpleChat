import java.io.*;
import java.util.Scanner;

import common.*;

public class ServerConsole implements ChatIF{

    static Scanner console;

    static EchoServer server;

    ServerConsole(int port){

        server = new EchoServer(port);
    }

    @Override
    public void display(String message) {
        // TODO Auto-generated method stub
        System.out.println("> " + message);
        
    }
    
    public void waitForMessage(){
        while (true){
            String message = console.nextLine();
            server.handleMessageFromServerUI(message);
        }
    }

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

        ServerConsole console = new ServerConsole(port);
        console.waitForMessage();
    }
}
