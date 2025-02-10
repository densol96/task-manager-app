import { createPortal } from "react-dom";
import styled, { keyframes } from "styled-components";

const rotate = keyframes`
  to {
    transform: rotate(1turn)
  }
`;

const Container = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #e0e7ff;
  margin-top: 10%;
`;

const StyledSpinner = styled.div`
  width: 6.4rem;
  height: 6.4rem;
  aspect-ratio: 1;
  border-radius: 50%;
  background: radial-gradient(farthest-side, #4f46e5 94%, #0000) top/10px 10px
      no-repeat,
    conic-gradient(#0000 30%, #4f46e5);
  -webkit-mask: radial-gradient(farthest-side, #0000 calc(100% - 10px), #000 0);
  animation: ${rotate} 1.5s infinite linear;
  z-index: 1000;
`;

function Spinner() {
  return (
    <Container>
      <StyledSpinner />
    </Container>
  );
}

export default Spinner;
