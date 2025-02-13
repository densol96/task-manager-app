import styled from "styled-components";
import Heading from "../../ui/Heading";
import { MyLink } from "../../ui/MyLink";

const Container = styled.div`
  width: 100vw;
  height: 100vh;
  background-color: var(--color-brand-200);
  position: relative;
`;

const Message = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: var(--color-brand-50);
  padding: 5rem;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: center;
  text-align: center;
`;

function PaymentResult({ title, children }) {
  return (
    <Container>
      <Message>
        <Heading>{title}</Heading>
        {children}
        <MyLink to="/">Go to home page</MyLink>
      </Message>
    </Container>
  );
}

export default PaymentResult;
