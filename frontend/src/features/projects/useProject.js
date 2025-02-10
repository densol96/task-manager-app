import { useQuery } from "@tanstack/react-query";
import { getMyProjects, getProjectInfo } from "../services/apiProjects";
import toast from "react-hot-toast";

function useProject({ projectId, logout }) {
  const {
    data: project,
    isLoading,
    isSuccess,
    isError,
    error,
  } = useQuery({
    queryKey: ["project", projectId],
    queryFn: () => getProjectInfo(projectId),
    retry: 1,
  });

  if (error?.response?.message?.includes("JWT expired")) {
    toast.error("Session expired... You will need to log in again.");
    logout();
  }

  return {
    project,
    isLoading,
    isSuccess,
    isError,
  };
}

export default useProject;
