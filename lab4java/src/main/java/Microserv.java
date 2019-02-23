import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

class Microserv extends AbstractVerticle {

    String name = "";
    String adr = "";

    public Microserv(String name,String adr) {
        this.name = name;
        this.adr = adr;
    }

    public void start() {
        vertx.sharedData().<String, String>getClusterWideMap("MAP,",(e)->{
            e.result().put(name,adr,(r)->{});
        }) ;

        vertx.eventBus().consumer(adr).handler((e)->{
                String data = "";
                try {
                    data = new String(Files.readAllBytes(Paths.get("C:\Users\Krokus283\Desktop\labsLabs\new4\lab4java\src\DataBase\" + name + ".txt")), StandardCharsets.UTF_8) + e.body();
                    File file = new File("C:\Users\Krokus283\Desktop\labsLabs\new4\lab4java\src\DataBase\" + name + ".txt");
                    FileWriter fr = new FileWriter(file, true);
                    fr.write(e.body() + "<br>");
                    fr.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.reply(data);
        });
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        String adr = sc.nextLine();
        Vertx.clusteredVertx(new VertxOptions(),(event ->
                event.result().deployVerticle(new Microserv(name,adr))));
    }
}
