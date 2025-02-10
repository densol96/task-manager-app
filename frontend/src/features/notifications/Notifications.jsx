import { TbMoodEmptyFilled } from "react-icons/tb";
import { useAuthContext } from "../../context/AuthContext";
import { StyledEmptyMessage } from "../../ui/StyledEmptyMessage";
import { StyledTable } from "../../ui/StyledTable";
import CreateProjectButton from "../projects/CreateProjectButton";
import useNotifications from "./useNotifications";
import { formatDate } from "../../helpers/functions";
import Button from "../../ui/Button";
import Heading from "../../ui/Heading";
import styled from "styled-components";
import { useEffect, useState } from "react";
import { MdDelete } from "react-icons/md";
import { FaMarker } from "react-icons/fa6";
import { IoMdCheckmarkCircle } from "react-icons/io";
import { HiChevronLeft, HiChevronRight } from "react-icons/hi2";
import { useQueryClient } from "@tanstack/react-query";
import { deleteNotification, markAsRead } from "../services/apiNotifications";

const Pagination = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.2rem;
  padding: 1rem;
  gap: 3rem;
  border: 1px solid var(--color-brand-900);
  border-radius: 6px;
`;

const HeaderLine = styled.header`
  margin-bottom: 2rem;
  display: flex;
  justify-content: space-between;
`;

const SelectUnit = styled.div`
  display: flex;
  align-items: center;
  gap: 2rem;

  label {
    font-size: 2rem;
  }
`;

const Buttons = styled.div`
  display: flex;
  gap: 1rem;
`;

const PaginationButton = styled.button`
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: inherit;
  padding: 0.5rem 1rem;
  background-color: var(--color-brand-600);
  color: var(--color-grey-0);
  border-radius: 6px;
  border: none;

  &:disabled {
    color: var(--color-text-grey) !important;
    background-color: var(--color-grey-0);
  }
`;

const Container = styled.div`
  width: 70rem;
`;

function Notifications() {
  const { logout } = useAuthContext();
  const [page, setPage] = useState(1);
  const [filterBy, setFilterBy] = useState("all");
  const { notifications, totalPages, isLoading, isError } = useNotifications({
    logout,
    page,
    filterBy,
  });

  const queryClient = useQueryClient();

  useEffect(() => {
    if (totalPages < page) setPage(totalPages);
  }, [totalPages, page]);

  return (
    <Container>
      <HeaderLine>
        <Heading spacing={2} as="h1">
          Notifications
        </Heading>
        <SelectUnit>
          <label htmlFor="filterBy">Filter by</label>
          <select
            onChange={(e) => {
              setFilterBy(e.target.value);
              setPage(1);
            }}
            value={filterBy}
            name="filterBy"
            id="filterBy"
          >
            <option value="all">All</option>
            <option value="read">Read</option>
            <option value="unread">Unread</option>
          </select>
        </SelectUnit>
      </HeaderLine>
      {!notifications?.length ? (
        <StyledEmptyMessage>
          <p>
            No notifications for display <TbMoodEmptyFilled />
          </p>
        </StyledEmptyMessage>
      ) : (
        <StyledTable hasFooter={true}>
          <thead>
            <tr>
              <th>Title</th>
              <th>Message</th>
              <th>Datetime date</th>
              <th>Mark as read</th>
              <th>Delete</th>
            </tr>
          </thead>
          <tbody>
            {notifications.map((notification, rowIndex) => {
              return (
                <tr key={rowIndex}>
                  <td>{notification.title}</td>
                  <td>{notification.message}</td>
                  <td>{formatDate(notification.createdAt)}</td>
                  <td>
                    <p>
                      {!notification.hasBeenRead ? (
                        <Button
                          size="medium"
                          onClick={() =>
                            markAsRead(notification.id, queryClient)
                          }
                        >
                          <FaMarker />
                        </Button>
                      ) : (
                        <IoMdCheckmarkCircle />
                      )}
                    </p>
                  </td>
                  <td>
                    <p>
                      <Button
                        variation="danger"
                        size="medium"
                        onClick={() =>
                          deleteNotification(notification.id, queryClient)
                        }
                      >
                        <MdDelete />
                      </Button>
                    </p>
                  </td>
                </tr>
              );
            })}
          </tbody>
          {totalPages > 1 && (
            <tfoot>
              <tr>
                <td className="footer-pagination" colSpan={5}>
                  <Pagination>
                    <p>
                      Currently on {page}. of {totalPages}
                    </p>
                    <Buttons>
                      <PaginationButton
                        onClick={() => setPage((page) => page - 1)}
                        disabled={page === 1}
                      >
                        <HiChevronLeft />
                        <span>Back</span>
                      </PaginationButton>
                      <PaginationButton
                        disabled={page === totalPages}
                        onClick={() => setPage((page) => page + 1)}
                      >
                        <span>Forward</span>
                        <HiChevronRight />
                      </PaginationButton>
                    </Buttons>
                  </Pagination>
                </td>
              </tr>
            </tfoot>
          )}
        </StyledTable>
      )}
    </Container>
  );
}

export default Notifications;
