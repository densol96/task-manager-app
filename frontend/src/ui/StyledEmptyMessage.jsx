import styled from "styled-components";

export const StyledEmptyMessage = styled.div`
  width: 100%;
  padding: 3rem;
  text-align: center;
  border: 1px solid var(--color-table-border);
  font-size: 1.4rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 2rem;

  button {
    margin-top: 5rem;
  }

  p:first-child {
    margin-bottom: 0.5rem;
  }
`;
