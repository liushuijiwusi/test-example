import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ThreeLayerScheduleTaskMapper {

    void insertTask(ScheduleTask task);

    void insertSubTask(List<SubTask> subTasks);

    SubTask findSubTask(Long id);

    ScheduleTask findTask(Long id);

    List<SubTask> findSubTasks(Long parentId);

    void finishTask(ScheduleTask task);

    void finishSubTask(SubTask subTask);

    int countFinishedSubTask(Long parentId);

    void copyToHistoryTable(@Param("ids") List<Long> ids);

    void deleteTasks(@Param("ids") List<Long> ids);
}
