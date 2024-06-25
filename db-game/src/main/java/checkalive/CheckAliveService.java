package checkalive;

import com.dtarmory.properties.ArmoryProperty;
import com.dtarmory.properties.ArmoryPropertyCommon;
import com.dtarmory.util.IpUtil;
import com.dtarmory.util.SnowFlakeUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Service
public class CheckAliveService {

    @Autowired(required = false)
    private CheckAliveMapper checkAliveMapper;

    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    private ArmoryProperty armoryProperty;
    @Autowired
    private ArmoryPropertyCommon armoryPropertyCommon;

    @Autowired(required = false)
    private RedissonClient checkAliveRedissonClient;

    private String localIp = SnowFlakeUtil.getLocalIp();

    public boolean dbConnectionWorking() {

        return checkAliveMapper.tryDbConnection() == 2;
    }

    public boolean redisWorking() {

        String redisWorkingKey = appName + "_" + localIp + "_REDIS_WORKING";

        RBucket<Boolean> bucket = checkAliveRedissonClient.getBucket(redisWorkingKey);

        bucket.set(true);

        return Boolean.parseBoolean(checkAliveRedissonClient.getBucket(redisWorkingKey).get() + "");
    }

    public CheckAliveResponse check(HttpServletRequest request) {

        CheckAliveResponse response = CheckAliveResponse.of(appName, localIp, appName + "_" + localIp);

        String remoteIp = IpUtil.getRemoteIp(request);

        if (!armoryPropertyCommon.getKeepAliveWhiteIps().contains(remoteIp) && !armoryPropertyCommon.getKeepAliveWhiteIps().contains("*")) {

            CheckAliveResponse error = new CheckAliveResponse();

            error.setResult("IP not allowed:" + remoteIp);

            return error;
        }

        checkDb(response);

        checkRedis(response);

        checkAllResult(response);

        return response;
    }

    private void checkAllResult(CheckAliveResponse response) {

        for (Map.Entry<String, String> entry : response.getItems().entrySet()) {

            if (!StringUtils.equals(entry.getValue(), "OK")) {

                response.setResult("FAIL");
                return;
            }
        }

        response.setResult("OK");
    }

    private void checkRedis(CheckAliveResponse response) {

        if (Objects.nonNull(checkAliveRedissonClient)) {

            try {
                boolean result = redisWorking();
                response.getItems().put("REDIS", result ? "OK" : "FAIL");
            } catch (Exception e) {
                response.getItems().put("REDIS", e.getLocalizedMessage());
            }
        }
    }

    private void checkDb(CheckAliveResponse response) {


        if (Objects.nonNull(checkAliveMapper)) {

            try {
                boolean result = dbConnectionWorking();
                response.getItems().put("DB", result ? "OK" : "FAIL");
            } catch (Exception e) {
                response.getItems().put("DB", e.getLocalizedMessage());
            }
        }
    }
}
