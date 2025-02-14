import styled from "styled-components";
import Heading from "../../ui/Heading";
import TaskComments from "./TaskComments";
import { useProjectContext } from "../../pages/Project";
import Button from "../../ui/Button";
import { useQueryClient } from "@tanstack/react-query";
import { errorParser, getJWT } from "../../helpers/functions";
import axios from "axios";
import toast from "react-hot-toast";
import { useAuthContext } from "../../context/AuthContext";
import { useModalContext } from "../../ui/Modal";
import { useEffect, useState } from "react";
import Input from "../../ui/Input";
import TaskAssignedTo from "./TaskAssignedTo";

const TaskInfo = styled.div`
  width: 70rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
`;

const InfoUnit = styled.div`
  p {
    font-style: italic;
  }
`;

const Units = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const Row = styled.div`
  display: flex;
  justify-content: space-between;
`;

function TaskPopup({ task }) {
  const { isOwner } = useProjectContext();
  const { logout } = useAuthContext();
  const { close } = useModalContext();
  const queryClient = useQueryClient();

  const [status, setStatus] = useState(task.status);

  async function deleteTask() {
    try {
      const API_ENDPOINT = `${process.env.REACT_APP_API_URL}/tasks/delete/${task.id}`;
      const response = await axios.delete(API_ENDPOINT, {
        headers: { Authorization: `Bearer ${getJWT()}` },
      });
      toast.success("Task deleted.");
      queryClient.invalidateQueries({ queryKey: ["tasks"] });
      close();
    } catch (e) {
      errorParser(e, logout);
    }
  }

  async function changeStatus() {
    try {
      const API_ENDPOINT = `${process.env.REACT_APP_API_URL}/tasks/delete/${task.id}`;
      const response = await axios.delete(API_ENDPOINT, {
        headers: { Authorization: `Bearer ${getJWT()}` },
      });
      toast.success("Task deleted.");
      queryClient.invalidateQueries({ queryKey: ["tasks"] });
      close();
    } catch (e) {
      errorParser(e, logout);
    }
  }

  async function changeStatus(e) {
    try {
      const API_ENDPOINT = `${process.env.REACT_APP_API_URL}/tasks/update-status/${task.id}`;
      const response = await axios.put(
        API_ENDPOINT,
        { status: e.target.value },
        {
          headers: { Authorization: `Bearer ${getJWT()}` },
        }
      );
      toast.success("Changed status");
      queryClient.invalidateQueries({ queryKey: ["tasks"] });
      close();
    } catch (e) {
      errorParser(e, logout);
    }
  }

  return (
    <TaskInfo>
      <Row>
        <Heading>{task.title}</Heading>
        {isOwner && (
          <Button onClick={deleteTask} variation="danger">
            Delete
          </Button>
        )}
      </Row>

      <Row>
        <Units>
          <InfoUnit>
            <Heading as="h5"> Description:</Heading>
            <p>{task.description || "No description for this task..."}</p>
          </InfoUnit>
          <InfoUnit>
            <Heading as="h5"> Priority:</Heading>
            <p>{task.priority || "No priority for this task..."}</p>
          </InfoUnit>
          <InfoUnit>
            <Heading as="h5"> Status:</Heading>
            <select
              onChange={changeStatus}
              value={status}
              id="status"
              name="status"
            >
              <option value="TODO">To Do</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="FOR_REVIEW">For Review</option>
              <option value="DONE">Done</option>
            </select>
          </InfoUnit>
          <InfoUnit>
            <Heading as="h5"> Deadline:</Heading>
            <p>{task.deadline || "No deadline for this task..."}</p>
          </InfoUnit>
          <TaskAssignedTo taskId={task.id} />
        </Units>
        <TaskComments taskId={task.id} />
      </Row>
    </TaskInfo>
  );
}

export default TaskPopup;
