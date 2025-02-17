import styled from "styled-components";
import { Textarea } from "../../ui/Textarea";
import useTaskDiscussions from "./useTaskDiscussions";
import Button from "../../ui/Button";
import { addComment } from "../services/apiTasks";
import toast from "react-hot-toast";
import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { formatDate } from "../../helpers/functions";

const Comments = styled.div`
  max-height: 20rem;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const Comment = styled.div`
  background-color: var(--color-brand-200);
  padding: 1rem;
  border: 6px;
`;

const SmallP = styled.div`
  font-size: 1.2rem;
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
      setComment("");
      toast.success("Comment posted");
      queryClient.invalidateQueries({ queryKey: ["task-discussions", taskId] });
    } catch (e) {
      console.log(e);
      toast.error("Service currently unavailable");
    }
  }

  return (
    <div>
      {comments?.length ? (
        <Comments>
          {comments.map((comment) => (
            <Comment>
              <p>{comment.message}</p>
              <SmallP>
                Posted on: {formatDate(comment.postedAt)} by:{" "}
                {comment.userInfo.email}
              </SmallP>
            </Comment>
          ))}
        </Comments>
      ) : (
        <p>No any task comments...</p>
      )}
      <Textarea
        onChange={(e) => setComment(e.target.value)}
        value={comment}
        placeholder="Leave a comment to this task..."
      />
      <Button onClick={sendComment}>Send</Button>
    </div>
  );
}

export default TaskComments;
