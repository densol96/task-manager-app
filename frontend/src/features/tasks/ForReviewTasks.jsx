import TasksList from "./TasksList";
import useTasks from "./useTasks";

function ForReviewTasks({ projectId, logout }) {
  const { tasks, isLoading, isError, error } = useTasks({
    status: "for_review",
    projectId,
    logout,
  });
  return (
    <TasksList
      status="FOR_REVIEW"
      projectId={projectId}
      cards={tasks}
      heading="For Review"
    />
  );
}

export default ForReviewTasks;
