import { Link, Outlet, useParams } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";
import useProject from "../features/projects/useProject";
import Heading from "../ui/Heading";
import styled from "styled-components";
import Spinner from "../ui/Spinner";
import Button from "../ui/Button";
import { StyledEmptyMessage } from "../ui/StyledEmptyMessage";
import { formatDate } from "../helpers/functions";
import { FaPlus } from "react-icons/fa6";
import { createContext, useContext } from "react";
import { Modal } from "../ui/Modal";
import ConfirmForm from "../features/projects/ConfirmForm";
import { leaveProject } from "../features/services/apiProjects";
import { useQueryClient } from "@tanstack/react-query";
import { MyLink } from "../ui/MyLink";

const HeadingRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const MetaInfoContainer = styled.div`
  display: flex;
  flex-direction: row;
  gap: 5rem;
  align-items: center;
`;

const ShortInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-style: italic;
  font-size: 1.4rem;
`;

const OptionsHolder = styled.div`
  display: flex;
  gap: 1rem;
`;

export const ProjectContext = createContext();

export const useProjectContext = () => useContext(ProjectContext);

function Project() {
  const queryClient = useQueryClient();
  const { logout, user } = useAuthContext();
  const params = useParams();
  const projectId = Number(params.id);
  const { project, isLoading, isError, error } = useProject({
    projectId,
    logout,
  });

  if (isLoading) return <Spinner />;
  if (!project?.id || isError)
    return (
      <StyledEmptyMessage>
        Only private project memebrs can see this page :(
        <MyLink to="/">Go home</MyLink>
      </StyledEmptyMessage>
    );
  const isOwner = user.id === project?.owner?.userId;
  return (
    <ProjectContext.Provider value={{ isOwner, userId: user.id, project }}>
      <HeadingRow>
        <Heading spacing={2} as="h1">
          {project.title}
        </Heading>
        <OptionsHolder>
          <Link to="tasks">
            <Button>Tasks</Button>
          </Link>
          <Link to="members">
            <Button>Project members</Button>
          </Link>
          {isOwner ? (
            <Link to="owner-panel">
              <Button variation="danger">Owner Panel</Button>
            </Link>
          ) : (
            project.member && (
              <Modal triggerElement={<Button variation="danger">Leave</Button>}>
                <ConfirmForm
                  action={async () => leaveProject(project.id, queryClient)}
                >
                  Are you sure you want to leave this project?
                </ConfirmForm>
              </Modal>
            )
          )}
        </OptionsHolder>
      </HeadingRow>
      <ShortInfo>
        <p>
          <b>Created on:</b> {formatDate(project.createdAt)}
        </p>
        {project.member && (
          <p>
            <b>Member since:</b> {formatDate(project.memberSince)}
          </p>
        )}
        {project.description && (
          <p>
            <b>Description:</b> {project.description}
          </p>
        )}
      </ShortInfo>
      <Outlet />
    </ProjectContext.Provider>
  );
}

export default Project;
