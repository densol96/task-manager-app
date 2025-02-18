import TasksList from "./TasksList";
import useTasks from "./useTasks";

function CompletedTasks({ projectId, logout }) {
  const { tasks, isLoading, isError, error } = useTasks({
    status: "done",
    projectId,
    logout,
  });
  return (
    <TasksList
      status="DONE"
      projectId={projectId}
      cards={tasks}
      heading="Completed"
    />
  );
}

export default CompletedTasks;
