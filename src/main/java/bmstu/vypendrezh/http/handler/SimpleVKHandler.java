package bmstu.vypendrezh.http.handler;

import bmstu.vypendrezh.http.HttpMethod;
import bmstu.vypendrezh.http.SimpleHttpResponse;
import bmstu.vypendrezh.http.SimpleHttpResponseCode;
import bmstu.vypendrezh.http.vk.api.VKServiceConsumer;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleVKHandler implements HttpHandler {
    private final static Logger LOGGER = Logger.getLogger(SimpleVKHandler.class.getName());

    private static final String ACCEPT = "/me";
    private static final String AUTHORIZE = "/authorize";
    private static final String CODE = "/code";

    private VKServiceConsumer vkServiceConsumer;

    public VKServiceConsumer getVkServiceConsumer() {
        return vkServiceConsumer;
    }

    public void setVkServiceConsumer(VKServiceConsumer vkServiceConsumer) {
        this.vkServiceConsumer = vkServiceConsumer;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        LOGGER.log(Level.FINE, "Got the following HttpRequest : " + httpExchange.getRequestURI());
        SimpleHttpResponse response = new SimpleHttpResponse(SimpleHttpResponseCode.ERROR, "{\"Error 404\":\"Unknown request\"}");

        Headers headers = httpExchange.getRequestHeaders();
        String uri = httpExchange.getRequestURI().toString();
        Map<String, String> urlParams = parseParams(uri);
        Map <String, String> bodyParams = parseBodyParams(httpExchange);

        if (HttpMethod.GET.toString().equals(httpExchange.getRequestMethod()) && uri.startsWith(ACCEPT)) {
            response = new SimpleHttpResponse(SimpleHttpResponseCode.OK, "<form name=\"authorize\" method=\"post\" action=\"authorize\">"
                    + "<div>Click button to enter from VK</div>"
                    + "<div><input type=\"submit\" value=\"Enter\"></div></form>", true);
        } else if (uri.startsWith(AUTHORIZE)) {
            LOGGER.log(Level.FINE, "Should go with POST to VK !!! : " + bodyParams);
                if (getVkServiceConsumer().getCode() == null) {
                    response = new SimpleHttpResponse(SimpleHttpResponseCode.OK, "<meta http-equiv=\"refresh\" content=\"0;"
                            +"http://oauth.vk.com/authorize?" +
                            "client_id=" + getVkServiceConsumer().getClientId() +
                            "&scope=" + getVkServiceConsumer().getScope() +
                            "&redirect_uri=" + getVkServiceConsumer().getRedirectUri() +
                            "&display=" + getVkServiceConsumer().getDisplay() +
                            "&response_type=code" +
                            "\" />", true);
                } else if (getVkServiceConsumer().getToken() == null) {
                    try {
                        getVkServiceConsumer().requestToken();
                    } catch (URISyntaxException e) {
                        LOGGER.log(Level.SEVERE, null, e);
                    }
                    response = new SimpleHttpResponse(SimpleHttpResponseCode.OK, "<meta http-equiv=\"refresh\" content=\"0;"
                            + getVkServiceConsumer().getServerAddress() + AUTHORIZE +
                            "\" />"
                            , true);
                } else if (getVkServiceConsumer().getCode() != null && getVkServiceConsumer().getToken() != null) {
                    String data = null;
                    try {
                        data = getVkServiceConsumer().getUserData();
                    } catch (URISyntaxException e) {
                        LOGGER.log(Level.SEVERE, null, e);
                    }
                    response = new SimpleHttpResponse(SimpleHttpResponseCode.OK, "Autorization is successfull. User id="+getVkServiceConsumer().getUserId()
                            + "<br>"+data, true);
                }
        } else if (HttpMethod.GET.toString().equals(httpExchange.getRequestMethod()) && uri.startsWith(CODE)) {
            if (getVkServiceConsumer().getCode() == null) {
                getVkServiceConsumer().setCode(uri);
                response = new SimpleHttpResponse(SimpleHttpResponseCode.OK, "<meta http-equiv=\"refresh\" content=\"0;"
                        + getVkServiceConsumer().getServerAddress() + AUTHORIZE +
                        "\" />"
                        , true);
            }
        }
        writeResponse(httpExchange, response);
    }

    private void writeResponse(HttpExchange httpExchange, SimpleHttpResponse response) {
        try {
            if (SimpleHttpResponseCode.REDIRECT.equals(response.getCode())) {
                if (response.getUrl().isEmpty()) {
                    response.setUrl(httpExchange.getRequestHeaders().getFirst("Referer"));
                }
                httpExchange.getResponseHeaders().add("Location", response.getUrl());
            }

            if (response.isHtml()) {
                httpExchange.getResponseHeaders().add("Content-Type", "text/html");
            }
            httpExchange.sendResponseHeaders(response.getCode().getIntCode(),
                    SimpleHttpResponseCode.OK.equals(response.getCode()) ? response.getContent().getBytes().length : -1);
            if (SimpleHttpResponseCode.OK.equals(response.getCode())) {
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getContent().getBytes());
                os.close();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private Map <String, String> parseParams(String request) {
        if (request.indexOf('?') == -1) {
            return Maps.newHashMap();
        } else {
            String query = request.substring(request.indexOf("?")).split("\\?")[1];
            return Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);
        }
    }

    private Map<String, String> parseBodyParams(HttpExchange he) {
        if (!he.getRequestHeaders().containsKey("Content-length")) {
            return Maps.newHashMap();
        }

        int length = Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"));
        byte[] body = new byte[length];

        try {
            int size = he.getRequestBody().read(body, 0, length);
            LOGGER.log(Level.INFO, "Number of bytes read is " + size);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return parseParams(new String(body));
    }
}
