import React from "react";
import styled from "styled-components";
import { TbMoodEmptyFilled } from "react-icons/tb";
import { FaTrash } from "react-icons/fa";
import { errorParser, formatDate } from "../../helpers/functions";
import Button from "../../ui/Button";
import axios from "axios";
import toast from "react-hot-toast";
import { Modal } from "../../ui/Modal";
import ConfirmForm from "./ConfirmForm";
import { useQueryClient } from "@tanstack/react-query";

const StyledTable = styled.table`
  font-size: 1.4rem;
  border-collapse: collapse;
  width: 100%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  td,
  th {
    padding: 1.2rem;
    text-align: center;
  }

  thead tr {
    background-color: var(--color-brand-600) !important;
    color: white;
  }

  tr:nth-child(even) {
    background-color: var(--color-brand-300);
  }
  tr:nth-child(odd) {
    background-color: var(--color-brand-200);
  }

  tfoot tr td {
    padding: 0;
    ${({ hasFooter }) => !hasFooter && `border: none`}
  }

  .delete-btn {
    color: var(--color-active);
  }

  tbody {
    tr {
      transition: all 300ms;
    }
    tr:hover {
      transform: scale(1.1);
    }
  }
`;

const StyledEmptyMessage = styled.div`
  width: 100%;
  padding: 3rem;
  text-align: center;
  border: 1px solid var(--color-table-border);
  font-size: 1.4rem;

  p:first-child {
    margin-bottom: 0.5rem;
  }
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
          async function apply(e) {
            e.preventDefault();
            const API_ENDPOINT = `${process.env.REACT_APP_API_URL}/projects/${project.id}/application`;
            const jwt = JSON.parse(localStorage.getItem("jwt"));
            try {
              const response = await axios.post(
                API_ENDPOINT,
                {},
                {
                  headers: {
                    Authorization: `Bearer ${jwt}`,
                  },
                }
              );
              toast.success("Application sent");
              queryClient.invalidateQueries({ queryKey: ["projects"] });
            } catch (e) {
              errorParser(e);
            }
          }
          return (
            <tr key={rowIndex}>
              <th>{project.title}</th>
              <th>{project.description}</th>
              <th>{formatDate(project.createdAt)}</th>
              <th>{project.owner.firstName + " " + project.owner.lastName}</th>
              <th>
                {!(project.member || project.hasPendingRequest) && (
                  <Modal
                    triggerElement={
                      <Button size="small" variation="secondary">
                        Apply
                      </Button>
                    }
                  >
                    <ConfirmForm action={apply} />
                  </Modal>
                )}
                {project.hasPendingRequest && <p>Pending...</p>}
              </th>
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
