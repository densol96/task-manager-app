import styled from "styled-components";

const Placeholder = styled.div`
  visibility: hidden;
  height: 0;
  width: 100%;
`;

function HiddenPlaceholder() {
  return (
    <Placeholder>I AM OCCUPYING SPACE BUT U CANNOT SEE ME HAHA</Placeholder>
  );
}

export { HiddenPlaceholder };
