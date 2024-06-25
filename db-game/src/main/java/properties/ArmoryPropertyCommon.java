package properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@RefreshScope
@Component
@Data
@ConfigurationProperties(prefix = "armory")
public class ArmoryPropertyCommon {

    private List<String> keepAliveWhiteIps = new ArrayList<>();
}