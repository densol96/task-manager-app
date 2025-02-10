import { useEffect, useState } from "react";
import Button from "../../ui/Button";
import { FormLine } from "../../ui/FormLine";
import Heading from "../../ui/Heading";
import Input from "../../ui/Input";
import { Modal } from "../../ui/Modal";
import { Textarea } from "../../ui/Textarea";
import { createProject } from "../services/apiProjects";
import ConfirmForm from "./ConfirmForm";
import { StyledCheckbox } from "../../ui/StyedCheckbox";
import { useQueryClient } from "@tanstack/react-query";

function CreateProjectButton({ size = "small" }) {
  const queryClient = useQueryClient();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [isPublic, setIsPublic] = useState(true);
  const [maxParticipants, setMaxPaticipants] = useState(5);

  const formData = {
    title,
    description,
    isPublic,
    maxParticipants,
  };

  return (
    <Modal
      triggerElement={
        <Button size={size} variation="primary">
          Create project
        </Button>
      }
    >
      <ConfirmForm
        heading="Create a new form"
        width={50}
        action={async () => createProject(formData, queryClient)}
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
            onChange={(e) => setMaxPaticipants(+e.target.value)}
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
  );
}

export default CreateProjectButton;
