import React, { createContext, useContext, useEffect, useState } from "react";
import useLocalStorage from "../hooks/useLocalStorage";
import Spinner from "../ui/Spinner";
import axios from "axios";

const AuthContext = createContext({
  user: null,
  logout: () => {},
  updateJwt: (jwt) => {},
  refreshUser: () => {},
  jwt: "",
  trigger: true,
});

const AuthProvider = ({ children }) => {
  const {
    state: jwt,
    updateLocalStorage: updateJwt,
    deleteFromLocalStorage: logout,
  } = useLocalStorage("jwt", "");

  const [user, setUser] = useState(null);
  const [isReady, setIsReady] = useState(false);
  const [trigger, setTrigger] = useState(true);

  function refreshUser() {
    setTrigger(!trigger);
  }

  async function identifyJwt() {
    const API_ENDPOINT = `${process.env.REACT_APP_API_URL}/identity`;
    try {
      const response = await axios.get(API_ENDPOINT, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });
      setUser(response.data);
    } catch (e) {
      console.log(e);
      setUser(null);
    }
    setIsReady(true);
  }

  useEffect(() => {
    identifyJwt();
  }, [jwt, trigger]);

  return (
    <AuthContext.Provider
      value={{
        user,
        updateJwt,
        logout,
        refreshUser,
        jwt,
        trigger,
      }}
    >
      {!isReady ? <Spinner /> : children}
    </AuthContext.Provider>
  );
};

function useAuthContext() {
  const context = useContext(AuthContext);
  if (context === undefined)
    throw new Error("AuthContext must be used within a AuthProvider");

  return context;
}

export { AuthProvider, useAuthContext };
