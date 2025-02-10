import { useQueryClient } from "@tanstack/react-query";
import { StyledEmptyMessage } from "../../ui/StyledEmptyMessage";
import { StyledTable } from "../../ui/StyledTable";
import { Modal } from "../../ui/Modal";
import Button from "../../ui/Button";
import ConfirmForm from "../projects/ConfirmForm";
import { formatDate } from "../../helpers/functions";
import { TbMoodEmptyFilled } from "react-icons/tb";
import styled from "styled-components";
import {
  acceptInvitation,
  cancelApplication,
  declineApplication,
  declineInvitation,
} from "../services/apiProjects";
import { Link } from "react-router-dom";

const Th = styled.th`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

export const UserApplications = ({ applications }) => {
  const queryClient = useQueryClient();

  if (!applications?.length) {
    return (
      <StyledEmptyMessage>
        <p>
          No applications for display <TbMoodEmptyFilled />
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
        {applications?.map((applications, rowIndex) => {
          return (
            <tr key={rowIndex}>
              <th>{applications.project.id}</th>
              <th>{applications.project.title}</th>
              <th>{formatDate(applications.initAt)}</th>
              <th>
                <p>{`${applications.owner.firstName} ${applications.owner.lastName} `}</p>
                <p>{`(${applications.owner.email})`}</p>
              </th>
              <th>
                <Modal
                  triggerElement={
                    <Button size="small" variation="danger">
                      Cancel
                    </Button>
                  }
                >
                  <ConfirmForm
                    action={async () =>
                      cancelApplication(applications.id, queryClient)
                    }
                  >
                    Are you sure you want to cancel this application?
                  </ConfirmForm>
                </Modal>
              </th>
            </tr>
          );
        })}
      </tbody>
    </StyledTable>
  );
};
