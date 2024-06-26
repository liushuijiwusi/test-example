package armory.base;

import lombok.Data;

import java.util.Date;

@Data
public abstract class BaseArmoryMsg {

    private Long id;
    private String bizType;
    private String bizContext;
    private String bizId;
    private Date createdAt;
    private Date updateAt;
    private String topic;
    private String tag;
    private int retryTimes = 0;
    private int maxRetryTimes = 3;
    private long retryDurationInSecond = 3;
    private boolean needRetry = false;
    private ArmoryCommonStatus status;
    private String cancelReason;
    private String execResult;

}
