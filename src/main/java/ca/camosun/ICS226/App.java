package ca.camosun.ICS226;
import java.io.*;
import java.net.*;
import java.util.Scanner;

    public class App {

        protected String serverName;
        protected int serverPort;
        protected String key;

        public App(String serverName, int serverPort, String key) {
            this.serverName = serverName;
            this.serverPort = serverPort;
            this.key = key;
        }
    

        // generate a random key of length 8
        public String makeKey()
        {
            String legalCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "01234567890"
            + "!@#$%^&*";
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++)
            {
                int index = (int)(legalCharacters.length() * Math.random());
                sb.append(legalCharacters.charAt(index));
            }
            return sb.toString();
        }

        // return the next key and message as one string
        public String get_message(String key, PrintWriter out, BufferedReader in) throws IOException 
        {
            out.println(key);
            String reply = in.readLine();
            return reply;
        }

        // take user input for a message and concatenate that 
        // after PUT and a newly generated key
        public void put_message(String key, PrintWriter out, BufferedReader in) throws IOException
        {
            Scanner userInput = new Scanner(System.in);
            String nextMessage = userInput.nextLine();
            // generate a new key to put 
            String newKey = makeKey();
            out.println("PUT" + key.substring(3) + newKey + nextMessage);
            System.out.println(in.readLine());
            userInput.close();
        }

        // poll the server every 5 seconds to see if the newest key already exists
        // with a message
        public void poll_server(String key, PrintWriter out, BufferedReader in) throws InterruptedException, IOException 
        {
            while (true) 
            {
                Thread.sleep(5000);
                String reply = get_message(key, out, in);
                if (reply.substring(8).length() > 0) 
                {
                    System.out.println(reply.substring(8).length());
                }
            }
        }

        public void connect()
        {
            String reply;
            try (
                Socket socket = new Socket(serverName, serverPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            ) {
                while (true) 
                {   
                    // System.out.println("main started"); // DEBUG
                    // message & key
                    System.out.println("next key being sent: " + key);
                    reply = get_message(key, out, in);
                    System.out.println(reply);

                    if (reply.length() < 8)
                    {
                        // Runnable runnable = () -> this.poll_server(key, out, in);
                        put_message(key, out, in);
                        break;
                    }
                    this.key = "GET" + reply.substring(0, 8);
                }

            } catch (Exception e) {
                System.err.println(e);
                System.exit(-6);
            }
    }

    public static void main(String[] args) 
    {
        try {
            if (args.length != 3)
            {
                System.err.print("Need <host> <port> <key>");
                System.exit(-2);
            }

            App client = new App(args[0], Integer.parseInt(args[1]), args[2]);
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }   
}