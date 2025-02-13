import styled from "styled-components";

const Styled = styled.div`
  position: fixed;
  top: 50%;
  left: 60%;
  font-size: 7.2rem;
  transform: translate(-50%, -50%);
  text-align: center;
`;

function InProgress() {
  return <Styled> Not Available Till Next Week :(</Styled>;
}

export default InProgress;
