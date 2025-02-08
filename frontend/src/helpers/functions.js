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

export const errorParser = (e) => {
  const errors = e.response.data?.errors || {};
  const invalidField = Object.keys(errors)[0];
  const message = invalidField
    ? `${invalidField}: ${errors[invalidField]}`
    : e.response.data?.error;
  toast.error(message || "Wrong input. Check and try again!");
};
