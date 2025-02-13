import styled from "styled-components";
import { Textarea } from "../../ui/Textarea";
import useTaskDiscussions from "./useTaskDiscussions";
import Button from "../../ui/Button";
import { addComment } from "../services/apiTasks";
import toast from "react-hot-toast";
import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

const Comments = styled.div`
  max-height: 30rem;
  overflow-y: auto;
`;

function TaskComments({ taskId }) {
  const { comments } = useTaskDiscussions({ taskId });
  const [comment, setComment] = useState();
  const queryClient = useQueryClient();

  async function sendComment() {
    try {
      await addComment(taskId, {
        message: comment,
      });
      toast.success("Comment posted");
      queryClient.invalidateQueries({ queryKey: ["task-discussions", taskId] });
    } catch (e) {
      console.log(e);
      toast.error("Service currently unavailable");
    }
  }
  return (
    <>
      <Textarea
        onChange={(e) => setComment(e.target.value)}
        value={comment}
        placeholder="Leave a comment to this task..."
      />
      <Button onClick={sendComment}>Send</Button>
      {comments?.length ? (
        <Comments></Comments>
      ) : (
        <p>No any task comments...</p>
      )}
    </>
  );
}

export default TaskComments;
