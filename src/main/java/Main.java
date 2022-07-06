import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;


public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        try (ServerSocket serverSocket = new ServerSocket(8989);) { // стартуем сервер один(!) раз
            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    String word = in.readLine();
                    System.out.println("New connection accepted");
                    System.out.println("Слово запроса- " + word);
                    List<PageEntry> pageEntries = engine.search(word);
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    System.out.println(gson.toJson(pageEntries));
                    out.println(gson.toJson(pageEntries));
                    out.flush();
                    // обработка одного подключения
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}