import com.dtarmory.dto.ScheduleTaskMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.dtarmory.constants.ArmoryConstant.THREE_LAYER_SCHEDULE_SUB_TASK;

@Slf4j
@Component
@RocketMQMessageListener(topic = THREE_LAYER_SCHEDULE_SUB_TASK, consumerGroup = "${armory.three-layer-schedule-task-consumer-sub-grp}",
        selectorExpression = "${armory.three-layer-schedule-task-tags}", messageModel = MessageModel.CLUSTERING)
public class ThreeLayerScheduleSubTaskListener implements RocketMQListener<ScheduleTaskMsg> {

    @Autowired
    private ThreeLayerScheduleTaskService taskService;

    @Override
    public void onMessage(ScheduleTaskMsg message) {

        log.info("receive tlst sub msg:{}", message);

        taskService.executeSubTask(message);
    }
}
