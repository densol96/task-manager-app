import { useQuery } from "@tanstack/react-query";
import { getProjectInteractions } from "../services/apiProjects";
import toast from "react-hot-toast";

function useProjectInteractions({ type, logout, projectId }) {
  const {
    data: interactions,
    isLoading,
    isSuccess,
    isError,
    error,
  } = useQuery({
    queryKey: ["project", "interactions", type],
    queryFn: () => getProjectInteractions(projectId, type),
    retry: 1,
  });

  if (error?.response?.message?.includes("JWT expired")) {
    toast.error("Session expired... You will need to log in again.");
    logout();
  }

  return {
    interactions,
    isLoading,
    isSuccess,
    isError,
    error,
  };
}

export default useProjectInteractions;
