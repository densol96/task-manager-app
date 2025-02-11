import { Link, useParams } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";
import Heading from "../../ui/Heading";
import { OptionParameter } from "../../ui/OptionParameter";
import { TableContainer } from "../../ui/TableContainer";
import useProjectInteractions from "./useProjectInteractions";
import Button from "../../ui/Button";
import { StyledTable } from "../../ui/StyledTable";
import { formatDate } from "../../helpers/functions";
import { Modal } from "../../ui/Modal";
import ConfirmForm from "./ConfirmForm";
import { useQueryClient } from "@tanstack/react-query";
import { cancelInvitation } from "../services/apiProjects";
import { StyledEmptyMessage } from "../../ui/StyledEmptyMessage";
import { TbMoodEmptyFilled } from "react-icons/tb";

function ProjectInvitations() {
  const { logout } = useAuthContext();
  const projectId = +useParams().id;
  const queryClient = useQueryClient();
  const {
    interactions: invitations,
    isLoading,
    isSuccess,
    isError,
    error,
  } = useProjectInteractions({ type: "invitations", logout, projectId });

  const emptyTable = (
    <StyledEmptyMessage>
      <p>
        No invitations for display <TbMoodEmptyFilled />
      </p>
    </StyledEmptyMessage>
  );
  return (
    <>
      <Heading spacing={2} as="h2">
        Project Invitations
      </Heading>
      <TableContainer>
        {invitations?.length ? (
          <StyledTable hasFooter={true}>
            <thead>
              <tr>
                <th>Email</th>
                <th>Sent on</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {invitations?.map((invitation, rowIndex) => {
                return (
                  <tr key={rowIndex}>
                    <th>{invitation.user.email}</th>
                    <th>{formatDate(invitation.initAt)}</th>
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
                            cancelInvitation(invitation.id, queryClient)
                          }
                        >
                          Are you sure you want to cancel this invitation?
                        </ConfirmForm>
                      </Modal>
                    </th>
                  </tr>
                );
              })}
            </tbody>
          </StyledTable>
        ) : (
          emptyTable
        )}

        <OptionParameter style={{ display: "flex", justifyContent: "center" }}>
          <Link to={`/projects/${projectId}/owner-panel/applications`}>
            <Button variation="primary" size="large">
              Check applications
            </Button>
          </Link>
        </OptionParameter>
      </TableContainer>
    </>
  );
}

export default ProjectInvitations;
