import { Outlet } from "react-router-dom";
import styled from "styled-components";

const Layout = styled.div`
  height: 100vh;
  display: grid;
  grid-template-columns: 3fr 2fr;
`;

const Wallpaper = styled.div`
  box-shadow: -10px 0px 25px rgba(0, 0, 0, 0.1);
  background-image: url("/bg.jpg");
  background-size: cover;
  background-position: center;
  filter: blur(0.5px);

  &::after {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.2);
    z-index: 1;
  }
`;

const Main = styled.main`
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--color-brand-100);
`;

function EntranceLayout() {
  return (
    <Layout>
      <Main>
        <Outlet />
      </Main>
      <Wallpaper />
    </Layout>
  );
}

export default EntranceLayout;
