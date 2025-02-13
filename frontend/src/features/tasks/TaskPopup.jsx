import styled from "styled-components";
import Heading from "../../ui/Heading";
import TaskComments from "./TaskComments";

const TaskInfo = styled.div`
  width: 35rem;
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

function TaskPopup({ task }) {
  console.log(task);
  return (
    <TaskInfo>
      <Heading>{task.title}</Heading>
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
          <p>{task.status || "No status for this task..."}</p>
        </InfoUnit>
        <InfoUnit>
          <Heading as="h5"> Deadline:</Heading>
          <p>{task.deadline || "No deadline for this task..."}</p>
        </InfoUnit>
      </Units>
      <TaskComments taskId={task.id} />
    </TaskInfo>
  );
}

export default TaskPopup;
