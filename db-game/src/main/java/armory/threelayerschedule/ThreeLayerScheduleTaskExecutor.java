package armory.threelayerschedule;

import com.dtarmory.dto.ScheduleTaskMsg;

import java.util.List;

public interface ThreeLayerScheduleTaskExecutor {


    List<SubTask> getSplitTask(ScheduleTask scheduleTask, ScheduleTaskMsg message);

    ScheduleTask initScheduleTask(ScheduleTaskMsg message);

    boolean executeSubTask(SubTask subTask);

    default void whenTaskFinish(ScheduleTask task, List<SubTask> subTasks) {
    }
}
