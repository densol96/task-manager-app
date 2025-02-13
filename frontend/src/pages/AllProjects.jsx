import usePublicProjects from "../features/projects/usePublicProjects";
import { useAuthContext } from "../context/AuthContext";
import Heading from "../ui/Heading";
import { PublicProjects } from "../features/projects/PublicProjects";
import Pagination from "../ui/Pagination";
import { useSearchParams } from "react-router-dom";
import SortingFiltration from "../features/projects/SortingFiltration";
import { TableContainer } from "../ui/TableContainer";

function AllProjects() {
  const [searchParams, setSearchParams] = useSearchParams();

  const getParamOrDefault = (param, defaultValue) => {
    return searchParams.get(param) ?? defaultValue;
  };

  const page = Number(getParamOrDefault("page", 1));
  const size = Number(getParamOrDefault("size", 5));
  const sortDirection = getParamOrDefault("sortDirection", "desc");
  const sortBy = getParamOrDefault("sortBy", "createdAt");

  const { logout } = useAuthContext();
  const { projects, pageNumber, totalPages, isLoading, isSuccess, isError } =
    usePublicProjects({
      page,
      size,
      sortDirection,
      sortBy,
      logout,
    });

  return (
    <>
      <Heading spacing={2} as="h1">
        All Projects
      </Heading>
      <TableContainer>
        <PublicProjects
          pagination={<Pagination pagesTotal={totalPages} />}
          data={projects}
        />
        <SortingFiltration />
      </TableContainer>
    </>
  );
}

export default AllProjects;
