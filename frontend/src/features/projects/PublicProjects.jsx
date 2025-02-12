import React from "react";

import { TbMoodEmptyFilled } from "react-icons/tb";
import { formatDate } from "../../helpers/functions";
import Button from "../../ui/Button";
import { Modal } from "../../ui/Modal";
import ConfirmForm from "./ConfirmForm";
import { useQueryClient } from "@tanstack/react-query";
import { StyledEmptyMessage } from "../../ui/StyledEmptyMessage";
import { StyledTable } from "../../ui/StyledTable";
import { applyToJoin } from "../services/apiProjects";
import CreateProjectButton from "./CreateProjectButton";
import { Link } from "react-router-dom";
import styled from "styled-components";

const Th = styled.th`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: center;
`;

export const PublicProjects = ({ data, pagination }) => {
  const queryClient = useQueryClient();

  const projects = data || [];
  if (projects?.length === 0) {
    return (
      <StyledEmptyMessage>
        <p>
          No data for display <TbMoodEmptyFilled />
        </p>
        <p>Try to create a new project</p>
        <CreateProjectButton size="large" />
      </StyledEmptyMessage>
    );
  }

  const columns = Object.keys(data[0]);
  return (
    <StyledTable hasFooter={pagination !== null}>
      <thead>
        <tr>
          <th>Title</th>
          <th>Description</th>
          <th>Creation date</th>
          <th>Owner</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        {data.map((project, rowIndex) => {
          return (
            <tr key={rowIndex}>
              <th>{project.title}</th>
              <th>{project.description}</th>
              <th>{formatDate(project.createdAt)}</th>
              <th>
                <p>{`${project.owner.firstName} ${project.owner.lastName} `}</p>
                <p>{`(${project.owner.email})`}</p>
              </th>
              <Th>
                <Link to={`/projects/${project.id}`}>
                  <Button size="small">Open</Button>
                </Link>
                {!(project.member || project.hasPendingRequest) && (
                  <Modal
                    triggerElement={
                      <Button size="small" variation="secondary">
                        Apply
                      </Button>
                    }
                  >
                    <ConfirmForm
                      action={(e) => {
                        applyToJoin(project.id, queryClient);
                      }}
                    >
                      Are you sure you want to send the application to join this
                      project?
                    </ConfirmForm>
                  </Modal>
                )}
                {project.hasPendingRequest && <p>Pending...</p>}
              </Th>
            </tr>
          );
        })}
      </tbody>
      <tfoot>
        <tr>
          <td className="footer-pagination" colSpan={columns.length}>
            {pagination}
          </td>
        </tr>
      </tfoot>
    </StyledTable>
  );
};
