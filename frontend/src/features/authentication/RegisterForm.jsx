import { useState } from "react";
// import axios from "axios";
import toast from "react-hot-toast";
import { Navigate } from "react-router";
import { Form } from "../../ui/Form";
import { FormLine } from "../../ui/FormLine";
import { AppLink } from "../../ui/AppLink";
import Button from "../../ui/Button";
import Input from "../../ui/Input";
import axios from "axios";

export const RegisterForm = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");

  function cleanInputs() {
    setEmail("");
    setPassword("");
    setFirstName("");
    setLastName("");
  }

  async function register(e) {
    e.preventDefault();
    const API_ENDPOINT = `${process.env.REACT_APP_API_URL}/sign-up`;
    try {
      const response = await axios.post(API_ENDPOINT, {
        email,
        password,
        firstName,
        lastName,
      });
      toast.success(<p>{response.data}</p>);
      cleanInputs();
    } catch (e) {
      toast.error(e.response.data.error);
    }
  }

  return (
    <Form onSubmit={register}>
      <FormLine>
        <label>Email</label>
        <Input
          name="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          type="text"
        />
      </FormLine>
      <FormLine>
        <label>Password</label>
        <Input
          name="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          type="password"
        />
      </FormLine>
      <FormLine>
        <label>First name</label>
        <Input
          name="firstName"
          value={firstName}
          onChange={(e) => setFirstName(e.target.value)}
          type="password"
        />
      </FormLine>
      <FormLine>
        <label>Last name</label>
        <Input
          name="lastName"
          value={lastName}
          onChange={(e) => setLastName(e.target.value)}
          type="password"
        />
      </FormLine>
      <Button>Submit</Button>
      <AppLink to="/login">Sign in</AppLink>
      <AppLink to="/forgot-password">Forgot password</AppLink>
    </Form>
  );
};
