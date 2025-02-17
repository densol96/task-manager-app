import styled from "styled-components";
import { GoogleLogin, GoogleOAuthProvider } from "@react-oauth/google";
import { useAuthContext } from "../../context/AuthContext";
import toast from "react-hot-toast";

function GoogleProvider() {
  const { updateJwt } = useAuthContext();

  const handleSuccess = async (response) => {
    const oauthToken = response.credential;

    try {
      const res = await fetch(
        `${process.env.REACT_APP_BACKEND_DOMAIN}/login/oauth2/code/google`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ oauthToken }),
        }
      );

      const data = await res.json();
      updateJwt(data.jwt);
    } catch (error) {
      handleFailure();
    }
  };

  const handleFailure = (error) => {
    toast.error("Service is currently unavailable!");
  };

  return (
    <GoogleOAuthProvider clientId="188573725163-gnurb7ro09b9q4dsl9o6so0umkb231jc.apps.googleusercontent.com">
      <GoogleLogin onSuccess={handleSuccess} onError={handleFailure} />
    </GoogleOAuthProvider>
  );
}

export default GoogleProvider;
