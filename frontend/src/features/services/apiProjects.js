import axios from "axios";
import toast from "react-hot-toast";
import { errorParser } from "../../helpers/functions";

const API_URL = process.env.REACT_APP_API_URL;
const getJWT = () => JSON.parse(localStorage.getItem("jwt"));

export async function getAll(page, size, sortDirection, sortBy) {
  const API_ENDPOINT = `${API_URL}/projects/public?page=${page}&size=${size}&sortDirection=${sortDirection}&sortBy=${sortBy}`;
  console.log("=====================> ", API_ENDPOINT);
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function getMyProjects(page, size, sortDirection, sortBy) {
  const API_ENDPOINT = `${API_URL}/projects/owned?page=${page}&size=${size}&sortDirection=${sortDirection}&sortBy=${sortBy}`;
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function getProjectMembers({ projectId, page, sortDirection }) {
  const API_ENDPOINT = `${API_URL}/projects/${projectId}/members?page=${page}&sortDirection=${sortDirection}`;
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function createProject(formData, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects`;
  const response = await axios.post(API_ENDPOINT, formData, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success(response?.data?.message || "Project created");
  queryClient.invalidateQueries({ queryKey: ["projects"] });
}

export async function updateProject(projectId, formData, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/${projectId}`;
  const response = await axios.put(API_ENDPOINT, formData, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success(response?.data?.message || "Project updated");
  queryClient.invalidateQueries({ queryKey: ["project", projectId] });
  queryClient.invalidateQueries({ queryKey: ["project", "config", projectId] });
}

export async function deleteProject(projectId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/${projectId}`;
  const response = await axios.delete(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success(response?.data?.message || "Project updated");
  queryClient.invalidateQueries({ queryKey: ["project", projectId] });
}

export async function applyToJoin(projectId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/${projectId}/application`;
  const response = await axios.post(
    API_ENDPOINT,
    {},
    { headers: { Authorization: `Bearer ${getJWT()}` } }
  );
  toast.success(response?.data?.message || "Application sent");
  queryClient.invalidateQueries({ queryKey: ["projects"] });
}

export async function leaveProject(projectId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/${projectId}/leave`;
  const response = await axios.delete(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success(response?.data?.message || "Successfully left the project");
  queryClient.invalidateQueries({ queryKey: ["projects"] });
  queryClient.invalidateQueries({ queryKey: ["project"] });
}

export async function getUserInteractions(type) {
  const API_ENDPOINT = `${API_URL}/projects/${type}`;
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function getProjectInteractions(projectId, type) {
  const API_ENDPOINT = `${API_URL}/projects/${projectId}/${type}`;
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function acceptInvitation(invitationId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/invitations/${invitationId}/accept`;
  const response = await axios.post(
    API_ENDPOINT,
    {},
    { headers: { Authorization: `Bearer ${getJWT()}` } }
  );
  toast.success(response?.data?.message || "Invitation accepted");
  queryClient.invalidateQueries({ queryKey: ["interactions"] });
}

export async function declineInvitation(invitationId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/invitations/${invitationId}/decline`;
  const response = await axios.post(
    API_ENDPOINT,
    {},
    { headers: { Authorization: `Bearer ${getJWT()}` } }
  );
  toast.success(response?.data?.message || "Invitation declined");
  queryClient.invalidateQueries({ queryKey: ["interactions"] });
}

export async function cancelApplication(applicationId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/applications/${applicationId}`;
  const response = await axios.delete(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success(response?.data?.message || "Application cancelled");
  queryClient.invalidateQueries({ queryKey: ["interactions"] });
}

export async function getProjectInfo(projectId) {
  const API_ENDPOINT = `${API_URL}/projects/${projectId}`;
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

export async function getOwnerPrivateInfo(projectId) {
  const API_ENDPOINT = `${API_URL}/projects/${projectId}/for-owner`;
  const response = await axios.get(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  return response.data;
}

// ===== Invitations =====

export async function sendInvitation(projectId, email, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/${projectId}/invitation`;

  await axios.post(
    API_ENDPOINT,
    { email },
    { headers: { Authorization: `Bearer ${getJWT()}` } }
  );
  toast.success("Invitation sent");
  queryClient.invalidateQueries({
    queryKey: ["projectInvitations", projectId],
  });
}

export async function cancelInvitation(invitationId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/invitations/${invitationId}`;
  await axios.delete(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success("Invitation cancelled");
  queryClient.invalidateQueries({ queryKey: ["project", "interactions"] });
}

// ===== Applications =====

export async function acceptApplication(applicationId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/applications/${applicationId}/accept`;
  await axios.post(
    API_ENDPOINT,
    {},
    { headers: { Authorization: `Bearer ${getJWT()}` } }
  );
  toast.success("Application accepted");
  queryClient.invalidateQueries({ queryKey: ["project", "interactions"] });
}

export async function declineApplication(applicationId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/applications/${applicationId}/decline`;
  await axios.post(
    API_ENDPOINT,
    {},
    { headers: { Authorization: `Bearer ${getJWT()}` } }
  );
  toast.success("Application declined");
  queryClient.invalidateQueries({ queryKey: ["project", "interactions"] });
}

export async function deleteApplication(applicationId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/applications/${applicationId}`;
  await axios.delete(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success("Application deleted");
  queryClient.invalidateQueries({ queryKey: ["userApplications"] });
}

// ===== Project Members =====

export async function kickMember(projectMemberId, queryClient) {
  const API_ENDPOINT = `${API_URL}/projects/${projectMemberId}/kick`;
  await axios.delete(API_ENDPOINT, {
    headers: { Authorization: `Bearer ${getJWT()}` },
  });
  toast.success("Member removed");
  queryClient.invalidateQueries({ queryKey: ["project", "members"] });
}
