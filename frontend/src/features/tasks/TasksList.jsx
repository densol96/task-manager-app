import styled from "styled-components";
import Heading from "../../ui/Heading";
import Button from "../../ui/Button";
import { FaPlus } from "react-icons/fa6";
import { Modal } from "../../ui/Modal";
import ConfirmForm from "../projects/ConfirmForm";
import { FormLine } from "../../ui/FormLine";
import Input from "../../ui/Input";
import { Textarea } from "../../ui/Textarea";
import { useState } from "react";
import { createTask } from "../services/apiTasks";
import { useQueryClient } from "@tanstack/react-query";
import TaskPopup from "./TaskPopup";
import toast from "react-hot-toast";

const Section = styled.div`
  background-color: var(--color-brand-900);
  color: var(--color-grey-0);
  padding: 1rem 2rem 2rem;
  border-radius: 1.2rem;
  min-width: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);

  display: flex;
  flex-direction: column;

  h3 {
    text-align: center;
    text-transform: uppercase;
    font-weight: 700;
  }

  button {
    margin-top: 1rem;
  }
`;

const Cards = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1rem;
`;

const Card = styled.p`
  background-color: var(--color-brand-200);
  padding: 1rem 1rem;
  font-size: 1.3rem;
  border-radius: 3px;
  color: var(--color-grey-700);
  cursor: pointer;
  transition: all 300ms;

  &:hover {
    transform: scale(1.1);
  }
`;

function TasksList({ heading, cards, onAdd, onCardOpen, projectId, status }) {
  const queryClient = useQueryClient();
  const [title, setTitle] = useState();
  const [description, setDescription] = useState();
  const [priority, setPriority] = useState();
  const [deadline, setDeadline] = useState();

  function clearInput() {
    setTitle("");
    setDescription("");
    setPriority(null);
    setDeadline(null);
  }

  return (
    <Section>
      <Heading as="h5">{heading}</Heading>
      <Cards>
        {cards?.map((card) => (
          <Modal
            triggerElement={<Card onClick={onCardOpen}>{card.title}</Card>}
          >
            <TaskPopup task={card} />
          </Modal>
        ))}
      </Cards>
      <Modal
        triggerElement={
          <Button>
            <FaPlus />
            Add a card
          </Button>
        }
      >
        <ConfirmForm
          heading="Add a task"
          width={50}
          action={async () => {
            await createTask(
              {
                title,
                description,
                status,
                priority,
                deadline,
                projectId,
              },
              queryClient
            );
            clearInput();
          }}
        >
          <FormLine>
            <label>Title</label>
            <Input
              name="title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              type="text"
              placeholder="Example task title"
            />
          </FormLine>
          <FormLine>
            <label>Description</label>
            <Textarea
              name="description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Example task description"
            />
          </FormLine>
          <FormLine>
            <label>Status</label>
            <select disabled={true} value={status} id="status" name="status">
              <option value="TODO">To Do</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="FOR_REVIEW">For Review</option>
              <option value="DONE">Done</option>
            </select>
          </FormLine>
          <FormLine>
            <label>Priority</label>
            <select
              name="priority"
              value={priority}
              onChange={(e) => setPriority(e.target.value)}
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </FormLine>
          <FormLine>
            <label>Deadline</label>
            <Input
              name="deadline"
              value={deadline}
              onChange={(e) => setDeadline(e.target.value)}
              type="datetime-local"
            />
          </FormLine>
        </ConfirmForm>
      </Modal>
    </Section>
  );
}

export default TasksList;
