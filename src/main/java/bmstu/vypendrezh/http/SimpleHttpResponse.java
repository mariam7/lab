package bmstu.vypendrezh.http;

public class SimpleHttpResponse {
    private SimpleHttpResponseCode code;
    private String content;
    private String url;
    private boolean isHtml = false;

    public SimpleHttpResponseCode getCode() {
        return code;
    }

    public void setCode(SimpleHttpResponseCode code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean html) {
        isHtml = html;
    }

    public SimpleHttpResponse(SimpleHttpResponseCode code) {
        this.code = code;
        url = "";
        content = "";
    }

    public SimpleHttpResponse(SimpleHttpResponseCode code, String content, boolean isHtml) {
        this(code);
        this.isHtml = isHtml;

        if (code.equals(SimpleHttpResponseCode.REDIRECT)) {
            url = content;
        } else {
            this.content = content;
        }
    }

    public SimpleHttpResponse(SimpleHttpResponseCode code, String content) {
        this(code, content, false);
    }
}
