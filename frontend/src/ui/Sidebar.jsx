import styled from "styled-components";
import { NavLink } from "react-router-dom";
import Logo from "./Logo";
import Heading from "./Heading";
import { MdSpaceDashboard } from "react-icons/md";
import { IoDocumentsSharp, IoDocumentLock } from "react-icons/io5";
import Footer from "./Footer";

const StyledSidebar = styled.aside`
  padding: 3.2rem 2.4rem;
  border-right: 1px solid var(--color-grey-100);
  grid-row: 1 / span 2;
  display: flex;
  flex-direction: column;
  gap: 3.2rem;
  /* border-right: 3px solid var(--color-brand-600); */
  box-shadow: 10px 0px 5px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  align-items: center;
`;

const MainText = styled.div`
  font-family: "Anton", sans-serif;
  font-size: 3rem;
  font-weight: 400;
  text-transform: uppercase;
  color: var(--color-brand-900);
  letter-spacing: 0.2rem;
`;

const StyledNav = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 90%;
`;

const NavItem = styled(NavLink)`
  padding: 1rem;
  display: flex;
  gap: 1rem;
  align-items: center;
  font-size: 2rem;

  border-radius: 12px;

  &:hover {
    color: var(--color-brand-900);
    background-color: #6365f15a;
  }

  svg {
    font-size: 2.5rem;
    transition: all 300ms;
  }

  &.active {
    font-weight: bold;
    background-color: #b4b4da5a;

    &:hover {
      color: var(--color-brand-900);
      background-color: #6365f15a;
    }
  }
`;

function Sidebar() {
  return (
    <StyledSidebar>
      <Logo size="mini" />
      <Heading as="h2">
        <MainText>Task App Pro</MainText>
      </Heading>
      <StyledNav>
        <NavItem to="/dashboard">
          <MdSpaceDashboard />
          Dashboard
        </NavItem>
        <NavItem to="/projects-all">
          <IoDocumentsSharp />
          All Projects
        </NavItem>
        <NavItem to="/projects-mine">
          <IoDocumentLock />
          My Projects
        </NavItem>
      </StyledNav>
      <Footer />
    </StyledSidebar>
  );
}

export default Sidebar;
