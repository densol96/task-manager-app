import { SiGmail } from "react-icons/si";
import Button from "../../ui/Button";
import styled from "styled-components";

const googleAuthUrl =
  `https://accounts.google.com/o/oauth2/v2/auth?` +
  `scope=email%20profile&` +
  `access_type=offline&` +
  `response_type=code&` +
  `redirect_uri=${process.env.REACT_APP_BACKEND_DOMAIN}/login/oauth2/code/google&` +
  `client_id=${process.env.REACT_APP_GOOGLE_CLIENT_ID}`;

const A = styled.a`
  display: flex;
  width: 100%;

  button {
    width: 100%;
    padding: 1.6rem;

    svg {
      margin-right: 1rem;
    }
  }
`;

function GoogleProvider({ type }) {
  return (
    <A href={googleAuthUrl}>
      <Button type="button" variation="danger">
        <SiGmail />
        {type === "sign-in" ? "Sign In" : "Sign Up"} With Google Account
      </Button>
    </A>
  );
}

export default GoogleProvider;
