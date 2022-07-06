
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String host = "localhost";
        int port = 8989;

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(scanner.nextLine().toLowerCase());


            String jsonAnswerFromServer = in.readLine();
            List<PageEntry> answerFromServer = jsonToList(jsonAnswerFromServer);
            answerFromServer.forEach(System.out::println);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List<PageEntry> jsonToList(String jsonString) {
        JSONParser parser = new JSONParser();
        GsonBuilder builder = new GsonBuilder();
        List<PageEntry> finalList = new ArrayList<>();
        Gson gson = builder.setPrettyPrinting().create();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(jsonString);
            for (Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                finalList.add(gson.fromJson(String.valueOf(jsonObject), PageEntry.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return finalList;
    }
}


