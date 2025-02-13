import styled from "styled-components";
import Heading from "../../ui/Heading";
import { FaPlus } from "react-icons/fa6";
import Button from "../../ui/Button";

const StyledTasks = styled.div`
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 5rem;
  min-width: 0;
  align-items: self-start;
`;

const Section = styled.div`
  background-color: var(--color-brand-900);
  color: var(--color-grey-0);
  padding: 1rem 2rem 2rem;
  border-radius: 1.2rem;
  min-width: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);

  display: flex;
  flex-direction: column;

  h3 {
    text-align: center;
    text-transform: uppercase;
    font-weight: 700;
  }

  button {
    margin-top: 1rem;
  }
`;

const Cards = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-top: 1rem;
`;

const Card = styled.p`
  background-color: var(--color-brand-200);
  padding: 0.5rem 1rem;
  font-size: 1.3rem;
  border-radius: 3px;
  color: var(--color-grey-700);
`;

function Tasks() {
  return (
    <StyledTasks>
      <Section>
        <Heading as="h5">To Do</Heading>
        <Cards>
          <Card>
            fgfhgsgfsd kjfhsfhlks;jf kldsfjlkasdjfkjsql;
            dasdasdasdasdasdasdasdasdj
          </Card>
          <Card>
            fgfhgsgfsd kjfhsfhlks;jf kldsfjlkasdjfkjsql;
            dasdasdasdasdasdasdasdasdj
          </Card>
        </Cards>
        <Button>
          <FaPlus />
          Add a card
        </Button>
      </Section>
      <Section>
        <Heading as="h5">In progress</Heading>
        <Button>
          <FaPlus />
          Add a card
        </Button>
      </Section>
      <Section>
        <Heading as="h5">Done</Heading>
        <Button>
          <FaPlus />
          Add a card
        </Button>
      </Section>
    </StyledTasks>
  );
}

export default Tasks;
