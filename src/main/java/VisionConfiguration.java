import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="vision",ignoreUnknownFields = false)
public class VisionConfiguration {
    private String apiImageAnnotate;
    private String apiFilesAnnotate;
    private String apiKey;

    public String getApiImageAnnotate() {
        return apiImageAnnotate;
    }

    public void setApiImageAnnotate(String apiImageAnnotate) {
        this.apiImageAnnotate = apiImageAnnotate;
    }

    public String getApiFilesAnnotate() {
        return apiFilesAnnotate;
    }

    public void setApiFilesAnnotate(String apiFilesAnnotate) {
        this.apiFilesAnnotate = apiFilesAnnotate;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
