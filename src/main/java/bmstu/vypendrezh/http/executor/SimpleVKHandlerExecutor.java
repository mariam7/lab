package bmstu.vypendrezh.http.executor;

import bmstu.vypendrezh.http.handler.SimpleVKHandler;
import bmstu.vypendrezh.http.vk.impl.VKServiceConsumerImpl;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.LogManager;

public class SimpleVKHandlerExecutor {

    public static void main(String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration(SimpleVKHandler.class.getResourceAsStream("/logging.properties"));
        LogManager.getLogManager().readConfiguration(VKServiceConsumerImpl.class.getResourceAsStream("/logging.properties"));

        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(1234), 0);
        SimpleVKHandler simpleVKHandler = new SimpleVKHandler();
        simpleVKHandler.setVkServiceConsumer(new VKServiceConsumerImpl());
        server.createContext("/", simpleVKHandler);
        server.setExecutor(null);
        server.start();
    }
}
