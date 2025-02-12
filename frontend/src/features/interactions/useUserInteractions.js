import { useQuery } from "@tanstack/react-query";
import { getAll, getUserInteractions } from "../services/apiProjects";
import toast from "react-hot-toast";

function useUserInteractions(type, logout) {
  const {
    data: interactions,
    isLoading,
    isSuccess,
    isError,
    error,
  } = useQuery({
    queryKey: ["interactions", type],
    queryFn: () => getUserInteractions(type),
    retry: 1,
  });

  if (error?.response?.message?.includes("JWT expired")) {
    toast.error("Session expired... You will need to log in again.");
    logout();
  }

  console.log(interactions);

  return {
    interactions,
    isLoading,
    isSuccess,
    isError,
    error,
  };
}

export default useUserInteractions;
