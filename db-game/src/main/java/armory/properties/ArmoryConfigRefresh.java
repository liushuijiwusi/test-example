package armory.properties;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ArmoryConfigRefresh {

    private final ArmoryProperty armoryProperty;
    private final ArmoryPropertyCommon armoryPropertyCommon;
    private final RefreshScope refreshScope;

    @ApolloConfigChangeListener(value = "application.yml", interestedKeyPrefixes = {"armory"})
    public void onChange(ConfigChangeEvent changeEvent) {

        log.info("{}", changeEvent.changedKeys());

        log.info("changed config to armory {}", armoryProperty);

        refreshScope.refresh("armoryProperty");
    }

    @ApolloConfigChangeListener(value = "common-cfg.yml", interestedKeyPrefixes = {"armory"})
    public void onChangeCommon(ConfigChangeEvent changeEvent) {

        log.info("{}", changeEvent.changedKeys());

        log.info("changed config to armoryCommon {}", armoryPropertyCommon);

        refreshScope.refresh("armoryPropertyCommon");
    }

}
