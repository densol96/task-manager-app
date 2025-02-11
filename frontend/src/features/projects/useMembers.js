import { useQuery } from "@tanstack/react-query";
import { getProjectMembers } from "../services/apiProjects";
import toast from "react-hot-toast";

function useMembers({ projectId, logout, page, sortDirection }) {
  const { data, isLoading, isSuccess, isError, error } = useQuery({
    queryKey: ["project", "members", projectId, page, sortDirection],
    queryFn: () => getProjectMembers({ projectId, page, sortDirection }),
    retry: 1,
  });

  if (error?.response?.data?.message?.includes("JWT expired")) {
    toast.error("Session expired... You will need to log in again.");
    logout();
  }

  return {
    members: data?.content,
    totalPages: data?.totalPages,
    isLoading,
    isSuccess,
    isError,
  };
}

export default useMembers;
