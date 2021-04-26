/**
 * Portfolio Question 4
 * SangJoon Lee
 * 30024165
 * 23/04/2021
 */
package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

class ChatServer extends Thread {
    private static String clientName;
    protected Socket sock;
    private static ArrayList<Socket> clients = new ArrayList<Socket>(5);

    ChatServer(Socket sock) {
        this.sock = sock;
    }
    
    // remove Client which disconnected
    public void remove(Socket socket) {
        for (Socket s : ChatServer.clients) {
            if (socket == s) {
                ChatServer.clients.remove(socket);
                break;
            }
        }
    }

    @Override
    public void run() {
        InputStream fromClient = null;

        try {
            fromClient = sock.getInputStream();

            byte[] buf = new byte[1024];
            int count;
            while ((count = fromClient.read(buf)) != -1) {
                System.out.print(clientName + " : ");
                System.out.write(buf, 0, count);
            }
        } catch (IOException ex) {
            System.out.println(sock + ": Error(" + ex + ")");
        } finally {
            try {
                if (sock != null) {
                    sock.close();
                    // remove Client which disconnected
                    remove(sock);
                }
                fromClient = null;
            } catch (IOException ex) {
                System.out.println("Error(" + ex + ")");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("admin", "0000"));
        users.add(new User("client1", "1111"));
        users.add(new User("client2", "2222"));
        boolean verified = false;
        boolean verifiedClient = false;
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Java Chat - Version 1.0 ===\n");
        while (!verified) {
            System.out.print("Admin ID: ");
            String id = sc.next();
            if (id.equals("admin")) {
                System.out.print("Admin Password: ");
                String pw = sc.next();
                verified = users.get(0).checkPassword(pw);
            } else {
                System.out.println("Wrong ID!\n");
            }
        }
        System.out.println("Admin loged in");
        
        ServerSocket server = new ServerSocket(9999);
        System.out.println("Server is listening on port #" + server.getLocalPort());
        SendThread st = new SendThread();
        String sendString = "";
        boolean found = false;
        while (true) {
            Socket client = server.accept();
            while (true) {
                BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                
                String receivedString = buf.readLine();
                String[] parts = receivedString.split(",");
                String clientId = parts[0];
                String clientPW = parts[1];
                for (int i = 0; i < users.size(); i++) {
                    if ((users.get(i).getId()).equals(clientId)) {
                        verifiedClient = users.get(i).checkPassword(clientPW);
                        sendString = "verified";
                        found = true;
                    }
                }
                if (found) {
                    if (!verifiedClient) sendString = "Password not matched";
                } else {
                    sendString = "ID not found";
                }
                PrintWriter pw = new PrintWriter(client.getOutputStream()); 
                pw.println(sendString);
                pw.flush();
                
                if (verifiedClient) {
                    ChatServer.clientName = clientId;
                    break;
                }
            }
            st.setSocket(client);
            st.start();
            // connected client socket add to ArrayList
            clients.add(client);
            String clientHostName = client.getInetAddress().getHostName(); // client name
            int clientPortNumber = client.getLocalPort(); // port used
            System.out.println(clientName + " is connected from " + clientHostName + " on #" + clientPortNumber);

            // Thread Start
            ChatServer myServer = new ChatServer(client);
            myServer.start();
        }
    }
    
}