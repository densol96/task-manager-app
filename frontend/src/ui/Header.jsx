import styled from "styled-components";
import Button from "./Button";
import { useAuthContext } from "../context/AuthContext";
import NotificationsAlarm from "../features/notifications/NotificationsAlarm";

const StyledHeader = styled.header`
  padding: 1.2rem 4.8rem;
  border-bottom: 2.5px solid var(--color-brand-600);
  z-index: 1000;

  display: flex;
  gap: 2.4rem;
  align-items: center;
  justify-content: flex-end;
`;

function Header() {
  const { logout, user } = useAuthContext();
  return (
    <StyledHeader>
      <p>{user.email}</p>
      <NotificationsAlarm />
      <Button onClick={logout} size="small">
        Logout
      </Button>
    </StyledHeader>
  );
}

export default Header;
