package mx.uv.divisasonline.ws;

public class Response {
    private boolean error;
    private int status;
    private String result;

    public Response() {
    }

    public Response(boolean error, int status, String result) {
        this.error = error;
        this.status = status;
        this.result = result;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
