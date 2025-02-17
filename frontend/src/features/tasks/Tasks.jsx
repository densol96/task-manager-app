import styled from "styled-components";
import ToDoTasks from "./ToDoTasks";
import InProgressTasks from "./InProgressTasks";
import CompletedTasks from "./CompletedTasks";
import ForReviewTasks from "./ForReviewTasks";
import { useParams } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";

const StyledTasks = styled.div`
  width: 100%;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 5rem;
  min-width: 0;
  align-items: self-start;
`;

function Tasks() {
  const { logout } = useAuthContext();
  const { id: projectId } = useParams();
  return (
    <StyledTasks>
      <ToDoTasks projectId={projectId} logout={logout} />
      <InProgressTasks projectId={projectId} logout={logout} />
      <ForReviewTasks projectId={projectId} logout={logout} />
      <CompletedTasks projectId={projectId} logout={logout} />
    </StyledTasks>
  );
}

export default Tasks;
