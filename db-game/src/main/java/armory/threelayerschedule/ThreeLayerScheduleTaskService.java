package armory.threelayerschedule;

import com.alibaba.fastjson.JSON;
import com.dtarmory.base.ArmoryCommonStatus;
import com.dtarmory.component.TransactionSynchronizationHelper;
import com.dtarmory.dto.ScheduleTaskMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ThreeLayerScheduleTaskService {

    @Autowired
    private Map<String, ThreeLayerScheduleTaskExecutor> taskExecutors;
    @Autowired
    private ThreeLayerScheduleTaskMapper taskMapper;
    @Resource
    private DataSourceTransactionManager dataSourceTransactionManager;
    @Resource
    private TransactionDefinition transactionDefinition;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void execute(ScheduleTaskMsg message) {

        TransactionStatus transaction = null;

        try {
            ThreeLayerScheduleTaskExecutor executor = taskExecutors.get(message.getTag());

            ScheduleTask scheduleTask = executor.initScheduleTask(message);
            List<SubTask> subTasks = executor.getSplitTask(scheduleTask, message);

            log.info("get main task:{}", JSON.toJSON(scheduleTask));
            // save all tasks
            log.info("get split sub tasks:{}", subTasks);
            // open transaction
            transaction = dataSourceTransactionManager.getTransaction(transactionDefinition);
            taskMapper.insertTask(scheduleTask);
            taskMapper.insertSubTask(subTasks);

            // send sub task execute msg
            sendSubTaskMsgAfterTaskCommit(subTasks);

            // commit if all good
            dataSourceTransactionManager.commit(transaction);

        } catch (Exception e) {

            log.error("rollback when exception happen", e);

            dataSourceTransactionManager.rollback(transaction);
        }
    }

    private void sendSubTaskMsgAfterTaskCommit(List<SubTask> subTasks) {

        TransactionSynchronizationHelper.doAfterCompletion(() -> subTasks.forEach(subTask -> {

            ScheduleTaskMsg taskMsg = ScheduleTaskMsg.buildThreeLayerSubTask(subTask.getTag(),
                    subTask.getId() + "", subTask.getExtendInfo());

            sendTaskMsg(taskMsg);

            log.info("send subtask msg:{}", JSON.toJSON(subTask));
        }));
    }

    private void sendTaskMsg(ScheduleTaskMsg taskMsg) {
        rocketMQTemplate.syncSend(taskMsg.getRocketMqDestination(), taskMsg);
    }

    public void executeSubTask(ScheduleTaskMsg message) {

        TransactionStatus transaction = null;

        try {

            ThreeLayerScheduleTaskExecutor executor = taskExecutors.get(message.getTag());

            // open transaction
            transaction = dataSourceTransactionManager.getTransaction(transactionDefinition);

            SubTask subTask = taskMapper.findSubTask(Long.parseLong(message.getMsgContent()));

            if (subTask.getStatus() != ArmoryCommonStatus.PROCESSING) {

                log.warn("sub task not in processing status:{}", JSON.toJSON(subTask));
                return;
            }

            subTask.setExtendInfo(message.getExtendInfo());

            boolean executeResult = executor.executeSubTask(subTask);

            if (executeResult) {

                finishSubTask(subTask);
                sendFinishTaskMsgAfterCommit(subTask);
            }

            // commit if all good
            dataSourceTransactionManager.commit(transaction);

        } catch (Exception e) {

            log.error("rollback sub task execution when exception happen", e);

            dataSourceTransactionManager.rollback(transaction);
        }
    }

    private void sendFinishTaskMsgAfterCommit(SubTask subTask) {

        TransactionSynchronizationHelper.doAfterCompletion(() -> {

            ScheduleTask task = taskMapper.findTask(subTask.getParentId());

            int count = taskMapper.countFinishedSubTask(subTask.getParentId());

            if (task.getSubTaskCount() == count) {

                ScheduleTaskMsg taskMsg = ScheduleTaskMsg.buildThreeLayerTaskFinish(subTask.getTag(), subTask.getParentId());

                sendTaskMsg(taskMsg);
            }
        });
    }

    private void finishSubTask(SubTask subTask) {

        taskMapper.finishSubTask(subTask);
    }

    public void finishTask(ScheduleTaskMsg message) {

        TransactionStatus transaction = null;

        try {
            ThreeLayerScheduleTaskExecutor executor = taskExecutors.get(message.getTag());

            ScheduleTask task = taskMapper.findTask(message.getParentTaskId());
            List<SubTask> subTasks = taskMapper.findSubTasks(message.getParentTaskId());

            log.info("when finish task get main task:{}", JSON.toJSON(task));
            log.info("when finish task get split sub tasks:{}", subTasks);
            // open transaction
            transaction = dataSourceTransactionManager.getTransaction(transactionDefinition);

            executor.whenTaskFinish(task, subTasks);

            taskMapper.finishTask(task);

            List<Long> taskIds = subTasks.stream().map(SubTask::getId).collect(Collectors.toList());
            taskIds.add(task.getId());

            taskMapper.copyToHistoryTable(taskIds);
            taskMapper.deleteTasks(taskIds);


            dataSourceTransactionManager.commit(transaction);

        } catch (Exception e) {

            log.error("exception happen when finish task, taskId:{}", message.getParentTaskId(), e);
            dataSourceTransactionManager.rollback(transaction);
        }
    }
}
