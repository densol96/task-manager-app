import toast from "react-hot-toast";

export const formatDate = (dateString) => {
  const date = new Date(dateString);
  const formattedDate = date.toLocaleString("en-US", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
  });
  return formattedDate;
};

export const errorParser = (e, logout) => {
  if (e.code === "ERR_NETWORK") {
    toast.error("Service is currently unavailable! Please, try again later!");
    console.log("ERR_NETWORK problem, server is likely down...");
    return;
  }

  if (e?.response?.message?.includes("JWT expired")) {
    console.log("I RUN!");
    toast.error("Session expired... You will need to log in again.");
    logout?.();
    return;
  }
  const errors = e.response?.data?.errors || {};
  const errorFields = Object.keys(errors);
  const invalidField = errorFields.length > 0 ? errorFields[0] : undefined;
  const message = invalidField
    ? `${invalidField}: ${errors[invalidField]}`
    : e.response?.data?.error;
  toast.error(
    message || e.response?.data?.message || "Wrong input. Check and try again!"
  );
};
