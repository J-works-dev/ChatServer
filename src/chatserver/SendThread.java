package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SendThread extends Thread {
    private Socket socket;
    
    @Override
    public void run() {
        super.run();
        try{
            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            String sendString;
            
            while (true) {
                sendString = buf.readLine();
                
                pw.println(sendString);
                pw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setSocket(Socket s) {
        socket = s;
    }
}
