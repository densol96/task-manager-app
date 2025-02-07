import styled from "styled-components";

export const FormLine = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  gap: 1em;
  align-items: center;

  &:last-of-type {
    margin-bottom: 2rem;
  }
`;
