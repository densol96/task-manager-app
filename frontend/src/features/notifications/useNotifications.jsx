import { useQuery } from "@tanstack/react-query";
import { getMyProjects } from "../services/apiProjects";
import toast from "react-hot-toast";
import { getAllNotificationsByUser } from "../services/apiNotifications";

function useNotifications({ logout, filterBy, page = 1, size = 5 }) {
  const { data, isLoading, isSuccess, isError, error } = useQuery({
    queryKey: ["notifications", filterBy, page, size],
    queryFn: () => getAllNotificationsByUser(filterBy, page, size),
    retry: 1,
  });

  if (error?.response?.data?.message?.includes("JWT expired")) {
    toast.error("Session expired... You will need to log in again.");
    logout();
  }

  console.log(data);

  return {
    notifications: data?.content,
    isLoading,
    isSuccess,
    isError,
    totalPages: data?.totalPages,
  };
}

export default useNotifications;
