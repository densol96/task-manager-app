import axios from "axios";
import { useEffect, useState } from "react";
import { getJWT } from "../../helpers/functions";
import Input from "../../ui/Input";
import Button from "../../ui/Button";
import Heading from "../../ui/Heading";
import toast from "react-hot-toast";
import { useProjectContext } from "../../pages/Project";
import { useQueryClient } from "@tanstack/react-query";
const API_URL = process.env.REACT_APP_API_URL;

function TaskAssignedTo({ taskId }) {
  const [to, setTo] = useState();
  const [newTo, setNewTo] = useState();

  const { isOwner } = useProjectContext();

  async function getAllAssignees() {
    const API_ENDPOINT = `${API_URL}/tasks/${taskId}/assignees`;
    try {
      const response = await axios.get(API_ENDPOINT, {
        headers: { Authorization: `Bearer ${getJWT()}` },
      });
      setTo(response.data.assignees?.[0]);
    } catch (e) {
      setTo(undefined);
    }
  }

  useEffect(() => {
    getAllAssignees();
  }, []);

  const queryClient = useQueryClient();

  async function assign() {
    const API_ENDPOINT = `${API_URL}/tasks/${taskId}/assign/${newTo}`;
    try {
      const response = await axios.post(
        API_ENDPOINT,
        {},
        {
          headers: { Authorization: `Bearer ${getJWT()}` },
        }
      );
      toast.success("Done!");
      queryClient.invalidateQueries({ queryKey: ["tasks"] });
      getAllAssignees();
    } catch (e) {
      console.log(e);
      toast.error(e.response?.data?.message || "Try again later!");
    }
  }

  return (
    <div>
      <Heading as="h5">Assignee: </Heading>
      {to && to.email}
      {isOwner && !to && (
        <div style={{ width: "20rem", display: "flex", gap: "1rem" }}>
          <Input onChange={(e) => setNewTo(+e.target.value)} type="number" />
          <Button onClick={assign}>Assign</Button>
        </div>
      )}
    </div>
  );
}

export default TaskAssignedTo;
