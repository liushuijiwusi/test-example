package armory.checkalive;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class CheckAliveResponse {

    private String app;
    private String ip;
    private String appId;
    private String result;
    private Map<String, String> items = new HashMap<>();
    private Date now = new Date();

    public static CheckAliveResponse of(String appName, String localIp, String appId) {

        CheckAliveResponse checkAliveResponse = new CheckAliveResponse();

        checkAliveResponse.setApp(appName);
        checkAliveResponse.setIp(localIp);
        checkAliveResponse.setAppId(appId);

        return checkAliveResponse;
    }
}
