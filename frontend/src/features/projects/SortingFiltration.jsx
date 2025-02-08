import styled from "styled-components";

import { IoAddCircleOutline } from "react-icons/io5";
import { MdRemoveCircleOutline } from "react-icons/md";

import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 3.2rem;
`;

const OptionParameter = styled.div`
  border-radius: 1.2rem;
  background-color: var(--color-grey-0);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2rem 1rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
`;

const Title = styled.p``;

const NumIncreaser = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;

  svg {
    font-size: 25px;
    color: var(--color-brand-900);

    &:hover {
      cursor: pointer;
    }
  }
`;

const Disabled = styled.div`
  display: flex;
  align-items: center;
  svg {
    font-size: 25px;
    color: var(--color-brand-200);
    cursor: disabled;
  }
`;

const PageSize = styled.div``;

function SortingFiltration() {
  const [size, setSize] = useState(5);
  const [searchParams, setSearchParams] = useSearchParams();

  useEffect(() => {
    searchParams.set("size", size);
    setSearchParams(searchParams);
  }, [size]);

  const minus = (
    <MdRemoveCircleOutline
      onClick={() => {
        if (size > 1) setSize(size - 1);
      }}
    />
  );
  const plus = (
    <IoAddCircleOutline
      onClick={() => {
        if (size < 5) setSize(size + 1);
      }}
    />
  );

  return (
    <StyledContainer>
      <OptionParameter>
        <Title>Sort by</Title>
        <select
          name="sortBy"
          onChange={(e) => {
            searchParams.set("sortBy", e.target.value);
            setSearchParams(searchParams);
          }}
        >
          <option value="createdAt">Creation date</option>
          <option value="title">Title</option>
        </select>
      </OptionParameter>
      <OptionParameter>
        <Title>Sort direction</Title>
        <select
          name="sortDirection"
          onChange={(e) => {
            searchParams.set("sortDirection", e.target.value);
            setSearchParams(searchParams);
          }}
        >
          <option value="desc">Descending</option>
          <option value="asc">Ascending</option>
        </select>
      </OptionParameter>
      <OptionParameter>
        <Title>Page size</Title>
        <NumIncreaser>
          {size > 1 ? minus : <Disabled>{minus}</Disabled>}
          <PageSize>{size}</PageSize>
          {size < 5 ? plus : <Disabled>{plus}</Disabled>}
        </NumIncreaser>
      </OptionParameter>
    </StyledContainer>
  );
}

export default SortingFiltration;
