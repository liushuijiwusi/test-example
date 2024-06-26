package armory.threelayerschedule;

import com.dtarmory.base.ArmoryCommonStatus;
import com.dtarmory.base.BaseArmoryMsg;
import com.dtarmory.util.SnowFlakeUtil;
import lombok.Data;

import java.util.Date;

@Data
public class ScheduleTask extends BaseArmoryMsg {

    private int subTaskCount = 0;

    public static ScheduleTask buildDefault(String topic, String tag, String bizId, String bizType, String bizContext) {

        ScheduleTask task = new ScheduleTask();

        task.setId(SnowFlakeUtil.newNextId());
        task.setBizId(bizId);
        task.setBizType(bizType);
        task.setBizContext(bizContext);
        task.setCreatedAt(new Date());
        task.setUpdateAt(new Date());
        task.setStatus(ArmoryCommonStatus.PROCESSING);
        task.setTopic(topic);
        task.setTag(tag);

        return task;
    }

    public static ScheduleTask buildRetry(String topic, String tag, String bizId, String bizType, String bizContext,
                                          int maxRetryTimes, long retryDurationInSecond) {

        ScheduleTask task = ScheduleTask.buildDefault(topic, tag, bizId, bizType, bizContext);

        task.setMaxRetryTimes(maxRetryTimes);
        task.setRetryDurationInSecond(retryDurationInSecond);
        task.setNeedRetry(true);

        return task;
    }

    public SubTask createSubTask(String subTaskBizContext) {

        SubTask subTask = new SubTask();

        subTask.setId(SnowFlakeUtil.newNextId());
        subTask.setParentId(getId());
        subTask.setBizType(getBizType());
        subTask.setBizContext(subTaskBizContext);
        subTask.setBizId(getBizId());
        subTask.setCreatedAt(getCreatedAt());
        subTask.setUpdateAt(getUpdateAt());
        subTask.setRetryTimes(getRetryTimes());
        subTask.setMaxRetryTimes(getMaxRetryTimes());
        subTask.setRetryDurationInSecond(getRetryDurationInSecond());
        subTask.setNeedRetry(isNeedRetry());
        subTask.setStatus(getStatus());
        subTask.setTopic(getTopic());
        subTask.setTag(getTag());

        // increase sub task count
        setSubTaskCount(getSubTaskCount() + 1);

        return subTask;
    }
}
