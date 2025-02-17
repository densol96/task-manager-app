import { useQuery } from "@tanstack/react-query";
import toast from "react-hot-toast";
import { getAll, getAllComments } from "../services/apiTasks";

// TODO, IN_PROGRESS, FOR_REVIEW, DONE
function useTaskDiscussions({ taskId, logout }) {
  const {
    data: comments,
    isLoading,
    isSuccess,
    isError,
    error,
  } = useQuery({
    queryKey: ["task-discussions", taskId],
    queryFn: () => getAllComments(taskId),
    retry: 1,
  });
  if (isError) console.log("useTaskDiscussions error: ", error);

  if (error?.response?.data?.message?.includes("JWT expired")) {
    toast.error("Session expired... You will need to log in again.");
    logout();
  }

  console.log("useTaskDiscussions", comments);

  return {
    comments,
    isLoading,
    isSuccess,
    isError,
  };
}

export default useTaskDiscussions;
