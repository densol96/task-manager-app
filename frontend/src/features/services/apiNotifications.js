import axios from "axios";
import toast from "react-hot-toast";
import { errorParser } from "../../helpers/functions";

const API_URL = process.env.REACT_APP_API_URL;
const getJWT = () => JSON.parse(localStorage.getItem("jwt"));

export async function getAllNotificationsByUser(
  filterBy = "all",
  page = 1,
  size = 5
) {
  const API_ENDPOINT = `${API_URL}/notifications/${filterBy}?page=${page}&size=${size}`;
  console.log(API_ENDPOINT);
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function getUnreadNotificationsByUser(page = 1, size = 5) {
  const API_ENDPOINT = `${API_URL}/notifications/unread?page=${page}&size=${size}`;
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function markAsRead(notificationId, queryClient) {
  const API_ENDPOINT = `${API_URL}/notifications/${notificationId}/mark-as-read`;
  const response = await axios.put(API_ENDPOINT, null, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success("Notification marked as read");
  queryClient.invalidateQueries({ queryKey: ["notifications"] });
  return response.data;
}

export async function userHasUnreadNotifications() {
  const API_ENDPOINT = `${API_URL}/notifications/has-unread-messages`;
  try {
    const response = await axios.get(API_ENDPOINT, {
      headers: { Authorization: `Bearer ${getJWT()}` },
    });
    return response.data?.hasUnreadMessages;
  } catch (e) {
    console.log("userHasUnreadNotifications exception ===> ", e);
    return false;
  }
}

export async function deleteNotification(notificationId, queryClient) {
  const API_ENDPOINT = `${API_URL}/notifications/${notificationId}`;
  const response = await axios.delete(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success("Notification deleted");
  queryClient.invalidateQueries({ queryKey: ["notifications"] });
  return response.data;
}
