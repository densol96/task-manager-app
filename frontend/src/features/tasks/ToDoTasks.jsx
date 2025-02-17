import TasksList from "./TasksList";
import useTasks from "./useTasks";

function ToDoTasks({ projectId, logout }) {
  const { tasks, isLoading, isError, error } = useTasks({
    status: "todo",
    projectId,
    logout,
  });

  return (
    <TasksList
      status="TODO"
      projectId={projectId}
      cards={tasks}
      heading="To Do"
    />
  );
}

export default ToDoTasks;
