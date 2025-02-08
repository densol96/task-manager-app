import axios from "axios";

export async function getAll(page, size, sortDirection, sortBy) {
  const API_ENDPOINT = `${process.env.REACT_APP_API_URL}/projects/public?page=${page}&size=${size}&sortDirection=${sortDirection}&sortBy=${sortBy}`;
  const jwt = JSON.parse(localStorage.getItem("jwt"));
  const response = await axios.get(API_ENDPOINT, {
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  });
  return response.data;
}
