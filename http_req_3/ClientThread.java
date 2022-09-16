package http_req_3;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static http_req_3.httpServer.chunk_size;

public class ClientThread extends Thread{
    Socket socket ;
    String fileName;
    public ClientThread(Socket sc,String fileName)
    {
        socket = sc;
        this.fileName = fileName;
    }

    public void run()
    {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pr = new PrintWriter(socket.getOutputStream());

            String[] strings = fileName.split("\\\\");
            String Name= strings[strings.length-1];
            System.out.println(fileName);

            File file = new File(fileName);
            String msg= "UPLOAD "+Name+"\n";
            pr.write(msg);
            pr.flush();

            if (file.exists())
            {
                System.out.println("file length : "+file.length());
                pr.write( file.length()+"\n");
                pr.flush();
                InputStream inputStream = new FileInputStream(file);
                byte[] byte_array = new byte[chunk_size];
                int byte_count=0;
                while ((byte_count = inputStream.read(byte_array))!= -1)
                {
                    socket.getOutputStream().write(byte_array,0,byte_count);

                }
                socket.getOutputStream().flush();
                String response =in.readLine();
                System.out.println("server send : "+response);

            }
            else
            {
                System.out.println(fileName+" File not found");
                String str = fileName+" File not found\n";
                pr.write(str);
                pr.flush();
                String response =in.readLine();
                System.out.println("server send : "+response);
            }

            pr.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
