select * from three_layer_schedule_task \G;

truncate three_layer_schedule_task;

select * from three_layer_schedule_task into three_layer_schedule_task_history where id in (996125126716547074,996125126716547075);

insert into three_layer_schedule_task_history select * from three_layer_schedule_task where id in (996125126716547074,996125126716547075);