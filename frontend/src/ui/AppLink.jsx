import { Link } from "react-router-dom";
import styled from "styled-components";

const AppLink = styled(Link)`
  text-decoration: underline;

  &:hover {
    text-decoration: none;
  }
`;

export { AppLink };
