package dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

import static com.dtarmory.constants.ArmoryConstant.*;

@Data
public class ScheduleTaskMsg {

    private Long parentTaskId;
    private String topic;
    private String tag;
    private String msgContent;
    private int subTaskCount;
    private String subTaskScope;
    private Map<String, Object> extendInfo;

    private Date sendTime = new Date();

    public static ScheduleTaskMsg buildThreeLayerTask(String tag, String msgContent) {

        ScheduleTaskMsg msg = new ScheduleTaskMsg();

        msg.setTopic(THREE_LAYER_SCHEDULE_TASK);
        msg.setTag(tag);
        msg.setMsgContent(msgContent);

        return msg;
    }

    public static ScheduleTaskMsg buildThreeLayerTaskFinish(String tag, Long parentTaskId) {

        ScheduleTaskMsg msg = new ScheduleTaskMsg();

        msg.setTopic(THREE_LAYER_SCHEDULE_TASK_FINISH);
        msg.setTag(tag);
        msg.setParentTaskId(parentTaskId);

        return msg;
    }

    public static ScheduleTaskMsg buildThreeLayerTask(String tag, String msgContent, int subTaskCount, String subTaskScope) {

        ScheduleTaskMsg msg = new ScheduleTaskMsg();

        msg.setTopic(THREE_LAYER_SCHEDULE_TASK);
        msg.setTag(tag);
        msg.setMsgContent(msgContent);
        msg.setSubTaskCount(subTaskCount);
        msg.setSubTaskScope(subTaskScope);

        return msg;
    }

    public static ScheduleTaskMsg buildThreeLayerSubTask(String tag, String msgContent) {

        ScheduleTaskMsg msg = new ScheduleTaskMsg();

        msg.setTopic(THREE_LAYER_SCHEDULE_SUB_TASK);
        msg.setTag(tag);
        msg.setMsgContent(msgContent);

        return msg;
    }

    public static ScheduleTaskMsg buildThreeLayerSubTask(String tag, String msgContent, Map<String, Object> extendInfo) {

        ScheduleTaskMsg msg = new ScheduleTaskMsg();

        msg.setTopic(THREE_LAYER_SCHEDULE_SUB_TASK);
        msg.setTag(tag);
        msg.setMsgContent(msgContent);
        msg.setExtendInfo(extendInfo);

        return msg;
    }

    public String getRocketMqDestination() {

        return getTopic() + ":" + getTag();
    }
}
