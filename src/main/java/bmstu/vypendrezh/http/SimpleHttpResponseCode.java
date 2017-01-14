package bmstu.vypendrezh.http;

public enum SimpleHttpResponseCode {
    OK(200),
    REDIRECT(301),
    ERROR(404);

    private int intCode;

    public int getIntCode() {
        return intCode;
    }

    public void setIntCode(int intCode) {
        this.intCode = intCode;
    }

    SimpleHttpResponseCode(int code) {
        this.intCode = code;
    }
}
