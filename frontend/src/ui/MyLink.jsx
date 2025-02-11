import { Link } from "react-router-dom";
import styled from "styled-components";

export const MyLink = styled(Link)`
  color: var(--color-indigo-700);
  margin-top: 3rem;
  text-decoration: underline;

  &:hover {
    text-decoration: none;
  }
`;
