import usePublicProjects from "../features/projects/usePublicProjects";
import { useAuthContext } from "../context/AuthContext";
import Heading from "../ui/Heading";
import { PublicProjects } from "../features/projects/PublicProjects";
import Pagination from "../ui/Pagination";
import { Link, useSearchParams } from "react-router-dom";
import SortingFiltration from "../features/projects/SortingFiltration";
import { TableContainer } from "../ui/TableContainer";
import { OptionParameter } from "../ui/OptionParameter";
import useUserInteractions from "../features/interactions/useUserInteractions";
import { UserInvitations } from "../features/interactions/UserInvitations";
import Button from "../ui/Button";
import { UserApplications } from "../features/interactions/UserApplications";

function Applications() {
  const { logout } = useAuthContext();
  const {
    interactions: applications,
    isLoading,
    isSuccess,
    isError,
    error,
  } = useUserInteractions("applications", logout);

  return (
    <>
      <Heading spacing={2} as="h1">
        My Applications
      </Heading>
      <TableContainer>
        <UserApplications applications={applications} />
        <OptionParameter style={{ display: "flex", justifyContent: "center" }}>
          <Link to="/interactions/invitations">
            <Button variation="primary" size="large">
              Check invitations
            </Button>
          </Link>
        </OptionParameter>
      </TableContainer>
    </>
  );
}

export default Applications;
