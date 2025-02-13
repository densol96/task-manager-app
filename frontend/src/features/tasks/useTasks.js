import { useQuery } from "@tanstack/react-query";
import toast from "react-hot-toast";
import { getAll } from "../services/apiTasks";

// TODO, IN_PROGRESS, FOR_REVIEW, DONE
function useTasks({ status, projectId, logout }) {
  const {
    data: tasks,
    isLoading,
    isSuccess,
    isError,
    error,
  } = useQuery({
    queryKey: ["tasks", projectId, status],
    queryFn: () => getAll(status),
    retry: 1,
  });
  if (isError) console.log("useTasks error: ", error);
  console.log("useTasks", tasks);
  if (error?.response?.data?.message?.includes("JWT expired")) {
    toast.error("Session expired... You will need to log in again.");
    logout();
  }

  return {
    tasks,
    isLoading,
    isSuccess,
    isError,
  };
}

export default useTasks;
