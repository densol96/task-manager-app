import styled from "styled-components";
import Button from "../../ui/Button";
import { Form } from "../../ui/Form";
import { useModalContext } from "../../ui/Modal";
import Heading from "../../ui/Heading";
import { errorParser } from "../../helpers/functions";
import { useAuthContext } from "../../context/AuthContext";

const BtnHolder = styled.div`
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 1rem;
`;

const HeadingWrapper = styled.div`
  text-align: center;
  margin-bottom: 2rem;
`;

function ConfirmForm({ action, children, width = 30, heading }) {
  const { logout } = useAuthContext();
  const { close } = useModalContext();

  async function onSubmit(event) {
    event.preventDefault();
    try {
      await action();
      close();
    } catch (e) {
      errorParser(e, logout);
    }
  }

  return (
    <Form style={{ maxWidth: `${width}rem` }} onSubmit={onSubmit}>
      {heading && (
        <HeadingWrapper>
          <Heading>{heading}</Heading>
        </HeadingWrapper>
      )}
      {children}
      <BtnHolder>
        <Button variation="primary">Confirm</Button>
        <Button onClick={close} variation="danger">
          Cancel
        </Button>
      </BtnHolder>
    </Form>
  );
}

export default ConfirmForm;
