import styled from "styled-components";

export const PulsateElement = styled.div`
  @keyframes pulsate {
    0% {
      transform: scale(1);
      opacity: 1;
    }
    50% {
      transform: scale(1.1);
      opacity: 0.7;
    }
    100% {
      transform: scale(1);
      opacity: 1;
    }
  }

  animation: pulsate 1.5s infinite;
  display: flex;
  align-items: center;
`;
