import styled from "styled-components";

export const StyledTable = styled.table`
  font-size: 1.4rem;
  border-collapse: collapse;
  width: 100%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  td,
  th {
    padding: 1.2rem;
    text-align: center;
  }

  thead tr {
    background-color: var(--color-brand-600) !important;
    color: white;
  }

  tr:nth-child(even) {
    background-color: var(--color-brand-300);
  }
  tr:nth-child(odd) {
    background-color: var(--color-brand-200);
  }

  tfoot tr td {
    padding: 0;
    ${({ hasFooter }) => !hasFooter && `border: none`}
  }

  .delete-btn {
    color: var(--color-active);
  }

  tbody {
    tr {
      transition: all 300ms;
    }
    tr:hover {
      transform: scale(1.1);
    }
  }
`;
