import styled from "styled-components";
import Button from "../../ui/Button";
import { Form } from "../../ui/Form";
import { useModalContext } from "../../ui/Modal";

const BtnHolder = styled.div`
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 1rem;
`;

const Container = styled.div`
  width: 30rem;
`;

function ConfirmForm({ action }) {
  const { close } = useModalContext();
  return (
    <Container>
      <Form onSubmit={action}>
        <p>
          Are you sure you want to send the application to join this project?
        </p>
        <BtnHolder>
          <Button variation="primary">Confirm</Button>
          <Button onClick={close} variation="danger">
            Cancel
          </Button>
        </BtnHolder>
      </Form>
    </Container>
  );
}

export default ConfirmForm;
