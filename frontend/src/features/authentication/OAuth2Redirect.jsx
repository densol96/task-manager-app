import styled from "styled-components";
import Spinner from "../../ui/Spinner";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";
import { useEffect } from "react";
import axios from "axios";
import toast from "react-hot-toast";
import { errorParser } from "../../helpers/functions";

const Container = styled.div`
  height: 100vh;
  width: 100vw;
  background-color: var(--color-brand-100);
`;

function OAuth2Redirect() {
  const { updateJwt } = useAuthContext();
  const [searchParams] = useSearchParams();
  const uuid = searchParams.get("uuid");
  const navigate = useNavigate();
  async function getJwt() {
    const API_ENDPOINT = `${process.env.REACT_APP_API_URL}/oauth2/get-jwt/${uuid}`;
    try {
      const response = await axios.get(API_ENDPOINT);
      updateJwt(response.data?.jwt);
      toast.success("Login succesfull");
      navigate("/");
    } catch (e) {
      //   errorParser(e);
    }
  }

  useEffect(() => getJwt, []);

  return (
    <Container>
      <Spinner />
    </Container>
  );
}

export default OAuth2Redirect;
