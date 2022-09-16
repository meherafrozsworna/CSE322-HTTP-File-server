package http_req_3;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class httpServer {
    static final int PORT = 6789;
    static int chunk_size=10;

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        ServerSocket welcomeSocket = new ServerSocket(PORT);

        while(true) {
            System.out.println("Waiting for connection...");
            Socket socket = welcomeSocket.accept();
            System.out.println("Connection established");

            // open thread
            Thread worker = new Worker(socket);
            worker.start();


        }

    }
}
