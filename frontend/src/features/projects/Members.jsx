import { useParams, useSearchParams } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";
import useMembers from "./useMembers";
import Spinner from "../../ui/Spinner";
import { StyledTable } from "../../ui/StyledTable";
import Pagination from "../../ui/Pagination";
import Heading from "../../ui/Heading";
import { SelectUnit } from "../../ui/SelectUnit";
import { HeaderLine } from "../../ui/HeaderLine";
import { ProjectContext } from "../../pages/Project";
import { useContext } from "react";
import { formatDate } from "../../helpers/functions";
import Button from "../../ui/Button";
import { Modal } from "../../ui/Modal";
import ConfirmForm from "./ConfirmForm";
import { kickMember } from "../services/apiProjects";
import { useQueryClient } from "@tanstack/react-query";

function Members() {
  const { logout } = useAuthContext();
  const { id } = useParams();

  const [searchParams, setSearchParams] = useSearchParams();

  const getParamOrDefault = (param, defaultValue) => {
    return searchParams.get(param) ?? defaultValue;
  };

  const page = Number(getParamOrDefault("page", 1));
  const sortDirection = getParamOrDefault("sortDirection", "desc");

  const { members, totalPages, isLoading, isSuccess, isError } = useMembers({
    logout,
    projectId: id,
    page,
    sortDirection,
  });

  const { isOwner, userId } = useContext(ProjectContext);
  const queryClient = useQueryClient();

  if (isLoading || members?.length === 0) return null;

  const columns = Object.keys(members[0]);
  return (
    <>
      <HeaderLine>
        <Heading spacing={2} as="h1">
          Project members
        </Heading>
        <SelectUnit>
          <label htmlFor="sortDirection">Sort direction</label>
          <select
            onChange={(e) => {
              searchParams.set("sortDirection", e.target.value);
              setSearchParams(searchParams);
            }}
            value={sortDirection}
            name="sortDirection"
            id="sortDirection"
          >
            <option value="desc">Descending</option>
            <option value="asc">Ascending</option>
          </select>
        </SelectUnit>
      </HeaderLine>
      <StyledTable style={{ width: "70%", margin: "0 auto" }} hasFooter={true}>
        <thead>
          <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Surname</th>
            <th>Join date</th>
            <th>Role</th>
            {isOwner && <th>Action</th>}
          </tr>
        </thead>
        <tbody>
          {members.map((member, rowIndex) => {
            return (
              <tr key={rowIndex}>
                <td>{member.userId}</td>
                <td>{member.firstName}</td>
                <td>{member.lastName}</td>
                <td>{formatDate(member.joinDate)}</td>
                <td>{member.projectRole}</td>
                <td>
                  {isOwner && userId !== member.userId && (
                    <Modal
                      triggerElement={
                        <Button size="small" variation="danger">
                          Remove from project
                        </Button>
                      }
                    >
                      <ConfirmForm
                        action={async () =>
                          kickMember(member.projectMemberId, queryClient)
                        }
                      >
                        Are you sure you want to cancel this application?
                      </ConfirmForm>
                    </Modal>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
        <tfoot>
          <tr>
            <td className="footer-pagination" colSpan={columns.length}>
              <Pagination pagesTotal={totalPages} />
            </td>
          </tr>
        </tfoot>
      </StyledTable>
    </>
  );
}

export default Members;
