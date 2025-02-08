import { useState } from "react";
import usePublicProjects from "../features/projects/usePublicProjects";
import { useAuthContext } from "../context/AuthContext";
import Heading from "../ui/Heading";
import { PublicProjects, Table } from "../features/projects/PublicProjects";
import Pagination from "../ui/Pagination";
import { useSearchParams } from "react-router-dom";
import styled from "styled-components";
import SortingFiltration from "../features/projects/SortingFiltration";

const StyledContainer = styled.div`
  display: grid;
  grid-template-columns: 6fr 2.5fr;
  gap: 10rem;
`;

function AllProjects() {
  const [searchParams, setSearchParams] = useSearchParams();

  const getParamOrDefault = (param, defaultValue) => {
    return searchParams.get(param) ?? defaultValue;
  };

  const page = Number(getParamOrDefault("page", 1));
  const size = Number(getParamOrDefault("size", 5));
  const sortDirection = getParamOrDefault("sortDirection", "desc");
  const sortBy = getParamOrDefault("sortBy", "createdAt");

  const { jwt } = useAuthContext();
  const { projects, pageNumber, totalPages, isLoading, isSuccess, isError } =
    usePublicProjects({
      page,
      size,
      sortDirection,
      sortBy,
    });

  return (
    <>
      <Heading spacing={2} as="h1">
        All Projects
      </Heading>
      <StyledContainer>
        <PublicProjects
          pagination={<Pagination pagesTotal={totalPages} />}
          data={projects}
        />
        <SortingFiltration />
      </StyledContainer>
    </>
  );
}

export default AllProjects;
