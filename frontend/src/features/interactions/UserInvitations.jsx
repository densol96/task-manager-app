import { useQueryClient } from "@tanstack/react-query";
import { StyledEmptyMessage } from "../../ui/StyledEmptyMessage";
import { StyledTable } from "../../ui/StyledTable";
import { Modal } from "../../ui/Modal";
import Button from "../../ui/Button";
import ConfirmForm from "../projects/ConfirmForm";
import { formatDate } from "../../helpers/functions";
import { TbMoodEmptyFilled } from "react-icons/tb";
import styled from "styled-components";
import { acceptInvitation, declineInvitation } from "../services/apiProjects";

const Th = styled.th`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

export const UserInvitations = ({ invitations }) => {
  const queryClient = useQueryClient();

  if (!invitations?.length) {
    return (
      <StyledEmptyMessage>
        <p>
          No invitations for display <TbMoodEmptyFilled />
        </p>
      </StyledEmptyMessage>
    );
  }

  return (
    <StyledTable hasFooter={false}>
      <thead>
        <tr>
          <th>Project Id</th>
          <th>Project</th>
          <th>Sent on</th>
          <th>Owner</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        {invitations?.map((invitation, rowIndex) => {
          return (
            <tr key={rowIndex}>
              <th>{invitation.project.id}</th>
              <th>{invitation.project.title}</th>
              <th>{formatDate(invitation.initAt)}</th>
              <th>
                <p>{`${invitation.owner.firstName} ${invitation.owner.lastName} `}</p>
                <p>{`(${invitation.owner.email})`}</p>
              </th>
              <Th>
                <Modal
                  triggerElement={
                    <Button size="small" variation="primary">
                      Accept
                    </Button>
                  }
                >
                  <ConfirmForm
                    action={async () =>
                      acceptInvitation(invitation.id, queryClient)
                    }
                  >
                    Are you sure you want to accept this invitation?
                  </ConfirmForm>
                </Modal>

                <Modal
                  triggerElement={
                    <Button size="small" variation="danger">
                      Decline
                    </Button>
                  }
                >
                  <ConfirmForm
                    action={async () =>
                      declineInvitation(invitation.id, queryClient)
                    }
                  >
                    Are you sure you want to decline this invitation?
                  </ConfirmForm>
                </Modal>
              </Th>
            </tr>
          );
        })}
      </tbody>
    </StyledTable>
  );
};
