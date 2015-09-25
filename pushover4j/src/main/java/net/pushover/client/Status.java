package net.pushover.client;

/**
 * API response object
 */
public class Status {

    private final Integer status;

    private String requestId;

    private String[] errors;

    public Status(Integer status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("status: ").append(status != null ? String.valueOf(status) : "??");
        builder.append(", requestId: ").append(requestId);
        if (errors != null) {
            builder.append(", errors: [");
            String delim = "";
            for (String error : errors) {
                builder.append(delim);
                builder.append("\"").append(error).append("\"");
                delim = ", ";
            }
            builder.append("]");
        }
        return builder.toString();
    }
}
