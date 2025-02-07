import styled from "styled-components";
import { useEffect, useState } from "react";
import { Navigate, Outlet, useNavigate } from "react-router-dom";
// import useUser from "./useUser";
import Spinner from "../../ui/Spinner";
import { useAuthContext } from "../../context/AuthContext";

const FullPage = styled.div`
  height: 100vh;
  background-color: var(--color-grey-50);
  display: flex;
  align-items: center;
  justify-content: center;
`;

function ProtectedRoute({ children }) {
  const navigate = useNavigate();
  const { user } = useAuthContext();

  if (user) return <div>{children}</div>;
  return <Navigate to="/login" />;
}

export default ProtectedRoute;
