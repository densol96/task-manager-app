import TasksList from "./TasksList";
import useTasks from "./useTasks";

function InProgressTasks({ projectId, logout }) {
  const { tasks, isLoading, isError, error } = useTasks({
    status: "in_progress",
    projectId,
    logout,
  });
  return (
    <TasksList
      status="IN_PROGRESS"
      projectId={projectId}
      cards={tasks}
      heading="In Progress"
    />
  );
}

export default InProgressTasks;
