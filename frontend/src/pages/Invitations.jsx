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

function Invitations() {
  const { logout } = useAuthContext();
  const {
    interactions: invtations,
    isLoading,
    isSuccess,
    isError,
    error,
  } = useUserInteractions("invitations", logout);
  return (
    <>
      <Heading spacing={2} as="h1">
        My Invitations
      </Heading>
      <TableContainer>
        <UserInvitations invitations={invtations} />
        <OptionParameter style={{ display: "flex", justifyContent: "center" }}>
          <Link to="/interactions/applications">
            <Button variation="primary" size="large">
              Check applications
            </Button>
          </Link>
        </OptionParameter>
      </TableContainer>
    </>
  );
}

export default Invitations;
