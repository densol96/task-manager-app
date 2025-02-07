import { useState } from "react";
// import axios from "axios";
import toast from "react-hot-toast";
import { Navigate } from "react-router";
import { Form } from "../../ui/Form";
import { FormLine } from "../../ui/FormLine";
import { AppLink } from "../../ui/AppLink";
import Button from "../../ui/Button";
import Input from "../../ui/Input";

export const ForgotPasswordForm = () => {
  const [email, setEmail] = useState("");

  return (
    <Form>
      <FormLine>
        <label>Email</label>
        <Input
          name="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          type="text"
          defaultValue="example@mail.com"
        />
      </FormLine>
      <Button>Submit</Button>
      <AppLink to="/login">Sign up</AppLink>
      <AppLink to="/register">Create account</AppLink>
    </Form>
  );
};
