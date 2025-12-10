UPDATE tasks
SET days_overdue = DATEDIFF('DAY', due_date, CURRENT_DATE())
WHERE task_complete = FALSE;