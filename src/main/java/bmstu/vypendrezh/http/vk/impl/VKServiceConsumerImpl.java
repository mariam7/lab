package bmstu.vypendrezh.http.vk.impl;

import bmstu.vypendrezh.http.vk.api.VKServiceConsumer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class VKServiceConsumerImpl implements VKServiceConsumer {
    private String server_address = "http://localhost:1234";
    private String client_id = "5822314";
    private String scope = "friends";
    private String redirect_uri = server_address + "/code";
    private String display = "page";
    private String response_type = "code";
    private String client_secret = "3KRJcbrbynVzH6mvaBQV";
    private String access_code;
    private String access_token;
    private String user_id;
    private String user_data;

    @Override
    public void requestToken() throws IOException, URISyntaxException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://oauth.vk.com/access_token?" +
                "client_id=" + client_id +
                "&client_secret=" + client_secret +
                "&redirect_uri=" + redirect_uri +
                "&code=" + access_code);
        HttpResponse response = httpClient.execute(post);
        InputStream content = response.getEntity().getContent();
        access_token = IOUtils.toString(content, "UTF-8");
        access_token = access_token.substring(17, access_token.length()-1);
        user_id = access_token.substring(access_token.lastIndexOf(":")+1, access_token.length());
        access_token = access_token.substring(0, access_token.indexOf("\""));
        post.abort();
    }

    @Override
    public String getCode() {
        return access_code;
    }

    @Override
    public String getToken() {
        return access_token;
    }

    @Override
    public String getServerAddress() {
        return server_address;
    }

    @Override
    public String getClientId() {
        return client_id;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String getRedirectUri() {
        return redirect_uri;
    }

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public void setCode(String src) {
        if (src.contains("/code?code=")) {
            src = src.replace("/code?code=", "");
            this.access_code = src;
        }
    }

    @Override
    public String getUserId() {
        return this.user_id;
    }

    @Override
    public String getUserData() throws IOException, URISyntaxException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://api.vk.com/method/friends.getOnline?" +
                "user_id=" + user_id +
                "&access_token=" + access_token);
        HttpResponse response = httpClient.execute(post);
        InputStream content = response.getEntity().getContent();
        user_data = IOUtils.toString(content, "UTF-8");
        post.abort();
        return user_data;
    }

    @Override
    public void getUserFriends() {

    }
}
