import { RegisterForm } from "../features/authentication/RegisterForm";
import EntranceForm from "../ui/EntranceForm";
import Heading from "../ui/Heading";
import Logo from "../ui/Logo";

function Register() {
  return (
    <EntranceForm>
      <Logo />
      <Heading as="h1">Sign up</Heading>
      <RegisterForm />
    </EntranceForm>
  );
}

export default Register;
