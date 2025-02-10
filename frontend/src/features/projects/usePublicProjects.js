import { useQuery } from "@tanstack/react-query";
import { getAll } from "../services/apiProjects";
import toast from "react-hot-toast";

function usePublicProjects({ page, size, sortDirection, sortBy, logout }) {
  const { data, isLoading, isSuccess, isError, error } = useQuery({
    queryKey: ["projects", "public", page, page, sortDirection, sortBy, size],
    queryFn: () => getAll(page, size, sortDirection, sortBy),
    retry: 1,
  });

  if (error?.response?.data?.message?.includes("JWT expired")) {
    toast.error("Session expired... You will need to log in again.");
    logout();
  }

  const projects = data?.content;
  const pageNumber = data?.pageable?.pageNumber;
  const totalPages = data?.totalPages;
  return {
    projects,
    pageNumber,
    totalPages,
    isLoading,
    isSuccess,
    isError,
  };
}

export default usePublicProjects;
