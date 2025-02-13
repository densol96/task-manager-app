import styled from "styled-components";
import { LoginForm } from "../features/authentication/LoginForm";
import Logo from "../ui/Logo";
import Heading from "../ui/Heading";
import EntranceForm from "../ui/EntranceForm";

function Login() {
  return (
    <EntranceForm>
      <Logo />
      <Heading as="h1">Sign in</Heading>
      <LoginForm />
    </EntranceForm>
  );
}

export default Login;
