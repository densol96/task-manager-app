import { ForgotPasswordForm } from "../features/authentication/ForgotPasswordForm copy";
import EntranceForm from "../ui/EntranceForm";
import Heading from "../ui/Heading";
import Logo from "../ui/Logo";

function ForgotPassword() {
  return (
    <EntranceForm>
      <Logo />
      <Heading as="h1">Restore password</Heading>
      <ForgotPasswordForm />
    </EntranceForm>
  );
}

export default ForgotPassword;
