import { Link, useParams } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";
import Heading from "../../ui/Heading";
import { OptionParameter } from "../../ui/OptionParameter";
import { StyledTable } from "../../ui/StyledTable";
import { TableContainer } from "../../ui/TableContainer";
import useProjectInteractions from "./useProjectInteractions";
import Button from "../../ui/Button";
import { useQueryClient } from "@tanstack/react-query";
import { Modal } from "../../ui/Modal";
import ConfirmForm from "./ConfirmForm";
import { formatDate } from "../../helpers/functions";
import styled from "styled-components";
import { acceptApplication, declineApplication } from "../services/apiProjects";
import { StyledEmptyMessage } from "../../ui/StyledEmptyMessage";
import { TbMoodEmptyFilled } from "react-icons/tb";

const Buttons = styled.div`
  display: flex;
  gap: 1rem;
`;

function ProjectApplications() {
  const { logout } = useAuthContext();
  const projectId = +useParams().id;
  const queryClient = useQueryClient();
  const {
    interactions: applications,
    isLoading,
    isSuccess,
    isError,
    error,
  } = useProjectInteractions({ type: "applications", logout, projectId });

  return (
    <>
      <Heading spacing={2} as="h2">
        Project Applications
      </Heading>
      <TableContainer>
        {applications?.length ? (
          <StyledTable hasFooter={true}>
            <thead>
              <tr>
                <th>Email</th>
                <th>Sent on</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {applications?.map((application, rowIndex) => {
                return (
                  <tr key={rowIndex}>
                    <th>{application.user.email}</th>
                    <th>{formatDate(application.initAt)}</th>
                    <th>
                      <Buttons>
                        <Modal
                          triggerElement={
                            <Button size="small" variation="primary">
                              Accept
                            </Button>
                          }
                        >
                          <ConfirmForm
                            action={async () =>
                              acceptApplication(application.id, queryClient)
                            }
                          >
                            Are you sure you want to accept this user as a
                            member of the project?
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
                              declineApplication(application.id, queryClient)
                            }
                          >
                            Are you sure you want to decline this user as a
                            member of the project?
                          </ConfirmForm>
                        </Modal>
                      </Buttons>
                    </th>
                  </tr>
                );
              })}
            </tbody>
          </StyledTable>
        ) : (
          <StyledEmptyMessage>
            <p>
              No applications for display <TbMoodEmptyFilled />
            </p>
          </StyledEmptyMessage>
        )}
        <OptionParameter style={{ display: "flex", justifyContent: "center" }}>
          <Link to={`/projects/${projectId}/owner-panel/invitations`}>
            <Button variation="primary" size="large">
              Check invitations
            </Button>
          </Link>
        </OptionParameter>
      </TableContainer>
    </>
  );
}

export default ProjectApplications;
