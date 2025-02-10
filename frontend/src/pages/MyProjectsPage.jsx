import { useState } from "react";
import usePublicProjects from "../features/projects/usePublicProjects";
import { useAuthContext } from "../context/AuthContext";
import Heading from "../ui/Heading";
import { MyProjects } from "../features/projects/MyProjects";
import Pagination from "../ui/Pagination";
import { useSearchParams } from "react-router-dom";
import SortingFiltration from "../features/projects/SortingFiltration";
import { TableContainer } from "../ui/TableContainer";
import useMyProjects from "../features/projects/useMyProjects";
import styled from "styled-components";
import CreateProjectButton from "../features/projects/CreateProjectButton";
import { HiddenPlaceholder } from "../ui/HiddenPlaceholder";

const HeadingWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

function MyProjectsPage() {
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
    useMyProjects({
      page,
      size,
      sortDirection,
      sortBy,
      logout,
    });

  return (
    <>
      <TableContainer>
        <HeadingWrapper>
          <Heading spacing={2} as="h1">
            My Projects
          </Heading>
          <CreateProjectButton size="medium" />
        </HeadingWrapper>
        <HiddenPlaceholder />
        <MyProjects
          pagination={<Pagination pagesTotal={totalPages} />}
          data={projects}
        />
        <SortingFiltration />
      </TableContainer>
    </>
  );
}

export default MyProjectsPage;
