import axios from "axios";
import toast from "react-hot-toast";
import { getJWT } from "../../helpers/functions";

const API_URL = process.env.REACT_APP_API_URL;

// TODO, IN_PROGRESS, FOR_REVIEW, DONE
export async function getAll(status, projectId) {
  const API_ENDPOINT = `${API_URL}/tasks/projects/${projectId}/status/${status}`;
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function createTask(formData, queryClient) {
  const API_ENDPOINT = `${API_URL}/tasks/add`;
  const response = await axios.post(API_ENDPOINT, formData, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success("Task created");
  queryClient.invalidateQueries({ queryKey: ["tasks"] });
  return response.data;
}

export async function getAllComments(taskId) {
  const API_ENDPOINT = `${API_URL}/tasks/${taskId}/messages`;
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function addComment(taskId, formData) {
  const API_ENDPOINT = `${API_URL}/tasks/${taskId}/messages/add`;
  const response = await axios.post(API_ENDPOINT, formData, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function deleteComment(messageId) {
  const API_ENDPOINT = `${API_URL}/tasks/messages/${messageId}`;
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}
