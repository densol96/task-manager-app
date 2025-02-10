import { Link, useParams } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";
import useProject from "../features/projects/useProject";
import Heading from "../ui/Heading";
import { TableContainer } from "../ui/TableContainer";
import styled from "styled-components";
import Spinner from "../ui/Spinner";
import Button from "../ui/Button";
import { StyledEmptyMessage } from "../ui/StyledEmptyMessage";
import { formatDate } from "../helpers/functions";
import { FaPlus } from "react-icons/fa6";

const HeadingRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const MetaInfoContainer = styled.div`
  display: flex;
  flex-direction: row;
  gap: 5rem;
  align-items: center;
`;

const ShortInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-style: italic;
  font-size: 1.4rem;
`;

const MyLink = styled(Link)`
  color: var(--color-indigo-700);
  margin-top: 3rem;
  text-decoration: underline;

  &:hover {
    text-decoration: none;
  }
`;

const Tasks = styled.div`
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

function Project() {
  const { logout, user } = useAuthContext();
  const { id: projectId } = useParams();
  const { project, isLoading } = useProject({ projectId, logout });

  console.log(project);

  if (isLoading) return <Spinner />;
  if (!isLoading && !project)
    return (
      <StyledEmptyMessage>
        This project is temporarily unavailable. Please, try again later :(
        <MyLink to="/">Go home</MyLink>
      </StyledEmptyMessage>
    );

  const iAmAuthor = user.id === project?.owner?.userId;

  return (
    <>
      <HeadingRow>
        <Heading spacing={2} as="h2">
          {project.title}
        </Heading>
        {iAmAuthor && (
          <Link>
            <Button>Owner Panel</Button>
          </Link>
        )}
      </HeadingRow>
      <ShortInfo>
        <p>
          <b>Created on:</b> {formatDate(project.createdAt)}
        </p>
        {project.member && (
          <p>
            <b>Member since:</b> {formatDate(project.memberSince)}
          </p>
        )}
        {project.description && (
          <p>
            <b>Description:</b> {project.description}
          </p>
        )}
      </ShortInfo>
      <Tasks>
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
      </Tasks>
    </>
  );
}

export default Project;
