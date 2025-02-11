import { useQuery } from "@tanstack/react-query";
import {
  getMyProjects,
  getOwnerPrivateInfo,
  getProjectInfo,
} from "../services/apiProjects";
import toast from "react-hot-toast";

function useOwnerInfo({ projectId, logout }) {
  const { data, isLoading, isSuccess, isError, error } = useQuery({
    queryKey: ["project", "config", projectId],
    queryFn: () => getOwnerPrivateInfo(projectId),
    retry: 1,
  });

  if (error?.response?.data?.message?.includes("JWT expired")) {
    toast.error("Session expired... You will need to log in again.");
    logout();
  }

  console.log(data);

  return {
    maxParticipants: data?.maxParticipants,
    isPublic: data?.isPublic,
    isLoading,
    isSuccess,
    isError,
    error,
  };
}

export default useOwnerInfo;
