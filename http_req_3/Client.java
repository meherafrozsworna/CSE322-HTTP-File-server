package http_req_3;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static http_req_3.httpServer.chunk_size;

public class Client {
    public static void main(String[] args) throws IOException {
        while (true)
        {

            Scanner scanner = new Scanner(System.in);
            System.out.println("Give file name : ");
            String fileName = scanner.nextLine();

            Socket socket = new Socket("localhost",6789);
            System.out.println("connection established");
            System.out.println("Client local port :"+ socket.getLocalPort());

            ClientThread clientThread = new ClientThread(socket,fileName);
            clientThread.start();
        }

    }
}
