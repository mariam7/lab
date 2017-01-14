package bmstu.vypendrezh.http.vk.api;

import java.io.IOException;
import java.net.URISyntaxException;

public interface VKServiceConsumer {

    void requestToken() throws IOException, URISyntaxException;

    String getCode();

    String getToken();

    String getServerAddress ();

    String getClientId ();

    String getScope ();

    String getRedirectUri ();

    String getDisplay ();

    void setCode (String src);

    String getUserId ();

    String getUserData() throws IOException, URISyntaxException;

    void getUserFriends();

}
