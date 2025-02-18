import { useState } from "react";
// import axios from "axios";
import { Navigate } from "react-router";
import { Form } from "../../ui/Form";
import { FormLine } from "../../ui/FormLine";
import { AppLink } from "../../ui/AppLink";
import Button from "../../ui/Button";
import Input from "../../ui/Input";
import axios from "axios";
import { useAuthContext } from "../../context/AuthContext";
import toast from "react-hot-toast";
import { errorParser } from "../../helpers/functions";
import GoogleProvider from "./GoogleProvider";

export const LoginForm = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const { updateJwt, user } = useAuthContext();

  async function login(e) {
    e.preventDefault();
    const API_ENDPOINT = `${process.env.REACT_APP_API_URL}/login`;
    try {
      const response = await axios.post(API_ENDPOINT, { email, password });
      updateJwt(response.data);
      toast.success("Login succesfull");
    } catch (e) {
      errorParser(e);
    }
  }

  if (user) return <Navigate to="/" />;

  return (
    <Form onSubmit={login}>
      <FormLine>
        <label>Email</label>
        <Input
          name="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          type="text"
          placeholder="example@mail.com"
        />
      </FormLine>
      <FormLine>
        <label>Password</label>
        <Input
          name="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          type="password"
          placeholder="● ● ● ● ● ● ● ● ● "
        />
      </FormLine>
      <Button>Submit</Button>
      <GoogleProvider type="sign-in" />
      <AppLink to="/register">Create account</AppLink>
    </Form>
  );
};
