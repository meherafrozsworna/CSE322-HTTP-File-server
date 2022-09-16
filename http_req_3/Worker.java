package http_req_3;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.StringTokenizer;

import static http_req_3.httpServer.chunk_size;


public class Worker extends Thread {
    Socket s;

    public Worker(Socket socket)
    {
        this.s = socket;

    }

    public void writeFile(String str,File fileName)throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(str);
        writer.close();
    }

    String readfile(String fileName)
    {

        File file = new File(fileName);
        String content= "";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
            content = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }
    public String getrequest(String input,PrintWriter pr) throws IOException {

        String logContent="";
        String inputStr = input.substring(5);
        StringTokenizer stringTokenizer = new StringTokenizer(input," ");
        String reqtype = stringTokenizer.nextToken();
        String findPath = stringTokenizer.nextToken();
        findPath=findPath.substring(1);

        if(inputStr.equals(" HTTP/1.1"))
        {
            String file_name ="index.html";
            String content= readfile(file_name);
            pr.write("HTTP/1.1 200 OK\r\n");
            pr.write("Server: Java HTTP Server: 1.0\r\n");
            pr.write("Date: " + new Date() + "\r\n");
            pr.write("Content-Type: text/html\r\n");
            pr.write("Content-Length: " + content.length() + "\r\n");
            pr.write("\r\n");
            pr.write(content);
            pr.flush();


            logContent+="http request : "+input+"\n\n";
            logContent+="Response : \n";
            logContent+="HTTP/1.1 200 OK\r\n";
            logContent+="Server: Java HTTP Server: 1.0\r\n";
            logContent+="Date: " + new Date() + "\r\n";
            logContent+="Content-Type: text/html\r\n";
            logContent+="Content-Length: " + content.length() + "\r\n\n";
            logContent+=content+"\n\n";

        }

        else
        {
            String first= "<html>\n" +
                    "\t<head>\n" +
                    "\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                    "\t</head>\n" +
                    "\t<body>\n";
            String last= "</body>\n" +
                    "</html>\n";
            String middle="";
            String[] parts=inputStr.split(" HTTP/1.1");



//            File f = new File("D:\\L-3 T-2\\CSE_322_Computer Networks Sessional" +
//                    "\\offline\\1\\Offline 1\\"+findPath);
            File f = new File(findPath);

            if (f.isDirectory())
            {
                middle+="<ul style=\"list-style-type:square;\">\n";
                File[] fList = f.listFiles();

                for (File file : fList) {
                    if (file.isFile())
                    {
                        //System.out.println("find path : "+ findPath);
                        middle+="<li><a href=\"http://localhost:6789/" + findPath+"/"+
                                file.getName() + "\" > "+ file.getName() + " </a></li>";
                    }
                    else if (file.isDirectory())
                    {
                        middle+="<li><b><a href=\"http://localhost:6789/" +findPath+"/"+
                                file.getName() + "\" > " + file.getName() + " </a></b></li>\n";
                    }
                }
                middle+="</ul>\n";
                if (f.getName().equals("root"))
                {
                    middle+="<form method=\"POST\" action=\"file\" enctype=\"multipart/form-data\" >\n" +
                            "\t\t\tFile:\n" +
                            "\t\t\t<input type=\"file\" name=\"file\" id=\"file\" /> <br/>\n" +
                            "\t\t\t<input type=\"submit\" value=\"Upload\" name=\"upload\" id=\"upload\" />\n" +
                            "\t\t</form>";
                }

                String content = first+middle+last;

                pr.write("HTTP/1.1 200 OK\r\n");
                pr.write("Server: Java HTTP Server: 1.0\r\n");
                pr.write("Date: " + new Date() + "\r\n");
                pr.write("Content-Type: text/html\r\n");
                pr.write("Content-Length: " + content.length() + "\r\n");
                pr.write("\r\n");
                //System.out.println(content);
                pr.write(content);
                pr.flush();


                logContent+="http request : "+input+"\n\n";
                logContent+="Response : \n";
                logContent+="HTTP/1.1 200 OK\r\n";
                logContent+="Server: Java HTTP Server: 1.0\r\n";
                logContent+="Date: " + new Date() + "\r\n";
                logContent+="Content-Type: text/html\r\n";
                logContent+="Content-Length: " + content.length() + "\r\n\n";
                logContent+=content+"\n\n";

            }



            else if (f.isFile())
            {
                pr.write("HTTP/1.1 200 OK\r\n");
                pr.write("Server: Java HTTP Server: 1.0\r\n");
                pr.write("Date: " + new Date() + "\r\n");
                String mimeType = Files.probeContentType(f.toPath());
                //System.out.println("Mime type : "+mimeType);
                pr.write(("Content-Type: "+mimeType+"\r\n"));
                pr.write(("Content-Disposition: attachment; filename=\""+f.getName()+"\"\r\n"));
                //pr.write("Content-Type: application/force-download\r\n");
                pr.write("Content-Length: " + f.length() + "\r\n");
                pr.write("\r\n");
                pr.flush();


                logContent+="http request : "+input+"\n\n";
                logContent+="Response : \n";
                logContent+="HTTP/1.1 200 OK\r\n";
                logContent+="Server: Java HTTP Server: 1.0\r\n";
                logContent+="Date: " + new Date() + "\r\n";
                logContent+="Content-Type: " + mimeType + "\r\n";
                logContent+="Content-Disposition: attachment; filename=\"" + f.getName() + "\"\r\n";
                logContent+="Content-Length: " + f.length() + "\r\n";
                logContent+="File : "+f.getName()+" has been sent .\n\n";


                InputStream inputStream = new FileInputStream(f);
                byte[] byte_array = new byte[chunk_size];
                int byte_count=0;

                while ((byte_count = inputStream.read(byte_array))!= -1)
                {
                    s.getOutputStream().write(byte_array,0,byte_count);
                }
                s.getOutputStream().flush();

            }
            else
            {
                String content= "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "   <title>404 Not Found</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "   <h1>Not Found</h1>\n" +
                        "   <p>The requested URL "+ findPath +" was not found on this server.</p>\n" +
                        "</body>\n" +
                        "</html>";
                pr.write("HTTP/1.1 404 Not Found\r\n");
                pr.write("Server: Java HTTP Server: 1.0\r\n");
                pr.write("Date: " + new Date() + "\r\n");
                pr.write("Content-Type: text/html\r\n");
                pr.write("Content-Length: " + content.length() + "\r\n");
                pr.write("\r\n");
                //System.out.println(content);
                pr.write(content);
                pr.flush();


                logContent+="http request : "+input+"\n\n";
                logContent+="Response : \n";
                logContent+="HTTP/1.1 404 Not Found\r\n";
                logContent+="Server: Java HTTP Server: 1.0\r\n";
                logContent+="Date: " + new Date() + "\r\n";
                logContent+="Content-Type: text/html\r\n";
                logContent+="Content-Length: " + content.length() + "\r\n\n";
                logContent+=content+"\n\n";


            }

        }
        //System.out.println("............................................");
        //System.out.println(logContent);
        return logContent;

    }

    public String UploadRequest(String input,BufferedReader in,PrintWriter pr) throws IOException {
        String logContent="http request from client : "+input+"\n\n";
        String[] strings=input.split("UPLOAD ");
        String fileName= strings[strings.length-1];

        OutputStream out = s.getOutputStream();

        String msg = in.readLine();
        //System.out.println(msg);

        if (msg.indexOf("File not found")!=-1)
        {
            System.out.println(msg);
            pr.write("Error 404 (File : "+ fileName +" not found)");
            logContent+= "Response : Error 404 (File : "+ fileName +" not found)\n\n\n";
            pr.flush();
        }
        else
        {
            logContent+="File size "+msg+"\n";
            int fileSize=Integer.parseInt(msg);
            System.out.println(fileSize);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            FileOutputStream fos = new FileOutputStream("root\\"+fileName);
            byte[] buffer = new byte[chunk_size];
            int read = 0;
            int totalRead = 0;
            int remaining = fileSize;
            while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0 && remaining!=0) {
                totalRead += read;
                remaining -= read;
                //System.out.println("read " + totalRead + " bytes.");
                fos.write(buffer, 0, read);
                fos.flush();
            }

            pr.write("File : "+fileName+ " is recieved by server");
            logContent+="\nResponse : File : "+fileName+ " is recieved by server\n\n\n";
            pr.flush();

            fos.close();
            dis.close();
        }

        return logContent;


    }

    public void postRequest(String input,BufferedReader in,PrintWriter pr) throws IOException
    {
        System.out.println(input);

        String msg = in.readLine();
        if (msg.length() > 0)
        {
            System.out.println(msg);
            msg=in.readLine();
        }
    }
    public void run()
    {
        // buffers
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter pr = new PrintWriter(s.getOutputStream());
            String input = in.readLine();
            String logContent ="";
            //System.out.println("input : ** "+input);
            if(input.length() > 0) {
                System.out.println(input);
                if(input.startsWith("GET"))
                {
                    logContent=getrequest(input,pr);
                }

                else if (input.startsWith("UPLOAD"))
                {
                    logContent=UploadRequest(input,in,pr);
                }
                else if (input.startsWith("POST"))
                {
                    postRequest(input,in,pr);
                }


                synchronized(this)
                {
                    PrintWriter fileWriter = new PrintWriter(new FileWriter("log.txt", true));
                    fileWriter.write(logContent);
                    fileWriter.flush();
                    fileWriter.close();
                }

            }
            s.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}

