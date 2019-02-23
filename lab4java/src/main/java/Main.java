import io.vertx.core.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class Main extends AbstractVerticle {
    @Override
    public void start() {

        Router router = Router.router(vertx);
        vertx.createHttpServer().requestHandler(router).listen(8090);
        router.route().handler(BodyHandler.create());
        router.route("/").handler(routingContext -> {
            routingContext.response().putHeader("content-type", "text/html").end(
                    "<form action=\"/form\" method=\"post\">\n" +
                            "    <div>\n" +
                            "        <label for=\"name\">Enter your name:</label>\n" +
                            "        <input type=\"text\" id=\"name\" name=\"name\" />\n" +
                            "    </div>\n" +
                            "    <div>\n" +
                            "        <label for=\"name\">Enter your note:</label>\n" +
                            "        <input type=\"text\" id=\"note\" name=\"note\" />\n" +
                            "    </div>\n" +
                            "    <div class=\"button\">\n" +
                            "        <button type=\"submit\">Send</button>\n" +
                            "    </div>" +
                            "</form>");
        });
        router.route("/form").handler(ctx -> {
            String name = ctx.request().getParam("name");
            String note = ctx.request().getParam("note");


            vertx.sharedData().<String, String>getClusterWideMap("MAP,",(e)->{
                e.result().get(name,(c) -> {
                    vertx.eventBus().send(c.result(), note, (r) -> {
                        if (r.succeeded()) {
                            ctx.response().putHeader("content-type", "text/html").end((String) r.result().body() +
                                    "<form action=\"/form\" method=\"post\">\n" +
                                    "    <div>\n" +
                                    "        <label for=\"name\">Enter your name:</label>\n" +
                                    "        <input type=\"text\" id=\"name\" name=\"name\" />\n" +
                                    "    </div>\n" +
                                    "    <div>\n" +
                                    "        <label for=\"name\">Enter your note:</label>\n" +
                                    "        <input type=\"text\" id=\"note\" name=\"note\" />\n" +
                                    "    </div>\n" +
                                    "    <div class=\"button\">\n" +
                                    "        <button type=\"submit\">Send</button>\n" +
                                    "    </div>" +
                                    "</form>");
                        }
                        else {
                            ctx.response().putHeader("content-type", "text/html").end("<p><b>Error!</b></p>");
                            }
                        });
                    });
                });
        });
    }
    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(),event -> event.result().deployVerticle(new Main()));
    }
}
