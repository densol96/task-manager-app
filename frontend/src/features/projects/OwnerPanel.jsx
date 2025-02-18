import styled from "styled-components";
import Heading from "../../ui/Heading";
import { Modal } from "../../ui/Modal";
import ConfirmForm from "./ConfirmForm";
import Button from "../../ui/Button";
import { useQueryClient } from "@tanstack/react-query";
import {
  deleteProject,
  leaveProject,
  updateProject,
} from "../services/apiProjects";
import { StyledEmptyMessage } from "../../ui/StyledEmptyMessage";
import { useContext, useEffect, useState } from "react";
import { ProjectContext } from "../../pages/Project";
import { MyLink } from "../../ui/MyLink";
import { FormLine } from "../../ui/FormLine";
import Input from "../../ui/Input";
import useOwnerInfo from "./useOwnerInfo";
import { useAuthContext } from "../../context/AuthContext";
import { StyledCheckbox } from "../../ui/StyledCheckbox";
import { Textarea } from "../../ui/Textarea";
import { Form } from "../../ui/Form";
import { Outlet } from "react-router-dom";

const Main = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5rem;
`;

const Section = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const Buttons = styled.div`
  display: flex;
  flex-direction: row;
  gap: 1rem;
`;

const FormContainer = styled.div`
  width: 70%;
`;

function OwnerPanel() {
  const queryClient = useQueryClient();
  const { logout } = useAuthContext();
  const { isOwner, project } = useContext(ProjectContext);

  const { maxParticipants: oldMaxParticipants, isPublic: oldIsPublic } =
    useOwnerInfo({ projectId: project.id, logout });
  const [title, setTitle] = useState(project.title);
  const [description, setDescription] = useState(project.description);
  const [maxParticipants, setMaxParticipants] = useState();
  const [isPublic, setIsPublic] = useState();

  useEffect(() => {
    setMaxParticipants(oldMaxParticipants);
    setIsPublic(oldIsPublic);
  }, [oldMaxParticipants, oldIsPublic]);

  function onSubmit(e) {
    e.preventDefault();
    updateProject(
      project?.id,
      { title, description, maxParticipants, isPublic },
      queryClient
    );
  }

  if (!isOwner)
    return (
      <StyledEmptyMessage>
        Only the project owner can see this page :(
        <MyLink to="/">Go home</MyLink>
      </StyledEmptyMessage>
    );

  return (
    <Main>
      <Section>
        <Heading as="h2">Manage project</Heading>
        <Buttons>
          <Modal
            triggerElement={<Button variation="danger">Delete project</Button>}
          >
            <ConfirmForm
              action={async () => deleteProject(project.id, queryClient)}
            >
              Are you sure you want to delete this project? This action cannot
              be reversed.
            </ConfirmForm>
          </Modal>
          <Modal
            triggerElement={<Button>Update projects info and settings</Button>}
          >
            <ConfirmForm
              heading="Update project seetings"
              width={70}
              action={async () =>
                updateProject(
                  project.id,
                  { title, description, maxParticipants, isPublic },
                  queryClient
                )
              }
            >
              <FormLine>
                <label>Title</label>
                <Input
                  name="title"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  type="text"
                  placeholder="Example project name"
                />
              </FormLine>
              <FormLine>
                <label>Members number (max)</label>
                <Input
                  name="maxParticipants"
                  value={maxParticipants}
                  onChange={(e) => setMaxParticipants(+e.target.value)}
                  type="number"
                  min={1}
                  max={20}
                />
              </FormLine>
              <FormLine>
                <label>Is public</label>
                <StyledCheckbox>
                  <input
                    name="isPublic"
                    checked={isPublic}
                    onChange={(e) => setIsPublic(e.target.checked)}
                    type="checkbox"
                  />
                </StyledCheckbox>
              </FormLine>
              <FormLine>
                <label>Description</label>
                <Textarea
                  name="description"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Example project name"
                />
              </FormLine>
            </ConfirmForm>
          </Modal>
        </Buttons>
      </Section>
      <Outlet />
    </Main>
  );
}

export default OwnerPanel;
