import { useQuery } from "@tanstack/react-query";
import { getAll } from "../services/apiPublicProjects";

function usePublicProjects({ page, size, sortDirection, sortBy, jwt }) {
  const { data, isLoading, isSuccess, isError, error } = useQuery({
    queryKey: ["projects", "public", page, page, sortDirection, sortBy, size],
    queryFn: () => getAll(page, size, sortDirection, sortBy),
    retry: 1,
  });

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
