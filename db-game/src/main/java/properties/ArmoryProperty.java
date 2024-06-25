package properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;


@RefreshScope
@Component
@Data
@ConfigurationProperties(prefix = "armory")
public class ArmoryProperty {

    private String threeLayerScheduleTaskConsumerGrp;
    private String threeLayerScheduleTaskTags;
}