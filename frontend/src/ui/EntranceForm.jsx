import styled from "styled-components";

const EntranceForm = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5rem;
  padding: 5rem;

  /* border: 1px solid red; */

  opacity: 0;
  animation: fadeIn 1s ease-in-out forwards;

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
`;

export default EntranceForm;
