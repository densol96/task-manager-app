import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { Toaster } from "react-hot-toast";
import GlobalStyles from "./styles/GlobalStyles";
import AppLayout from "./ui/AppLayout";
import ProtectedRoute from "./features/authentication/ProtectedRoute";

import Login from "./pages/Login";
import PageNotFound from "./pages/PageNotFound";
import EntranceLayout from "./ui/EntranceLayout";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import { AuthProvider } from "./context/AuthContext";
import AllProjects from "./pages/AllProjects";
import Dashboard from "./pages/Dashboard";
import MyProjectsPage from "./pages/MyProjectsPage";
import Invitations from "./pages/Invitations";
import Applications from "./pages/Applications";
import Project from "./pages/Project";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 0,
    },
  },
});

function App() {
  return (
    <>
      <QueryClientProvider client={queryClient}>
        <GlobalStyles />
        <AuthProvider>
          <ReactQueryDevtools />
          <BrowserRouter>
            <Routes>
              <Route
                element={
                  <ProtectedRoute>
                    <AppLayout />
                  </ProtectedRoute>
                }
              >
                <Route index element={<Navigate replace to="dashboard" />} />
                <Route path="dashboard" element={<Dashboard />} />
                <Route path="projects-all" element={<AllProjects />} />
                <Route path="projects-mine" element={<MyProjectsPage />} />
                <Route path="projects/:id" element={<Project />} />
                <Route path="interactions">
                  <Route
                    index
                    element={<Navigate replace to="invitations" />}
                  />
                  <Route path="invitations" element={<Invitations />} />
                  <Route path="applications" element={<Applications />} />
                </Route>
              </Route>
              <Route element={<EntranceLayout />}>
                <Route path="login" element={<Login />} />
                <Route path="register" element={<Register />} />
                <Route path="forgot-password" element={<ForgotPassword />} />
              </Route>
              <Route path="*" element={<PageNotFound />} />
            </Routes>
          </BrowserRouter>
          <Toaster
            position="top-center"
            gutter={12}
            containerStyle={{ margin: "8px" }}
            toastOptions={{
              success: {
                duration: 3000,
              },
              error: {
                duration: 5000,
              },
              style: {
                fontSize: "16px",
                maxWidth: "400px",
                padding: "16px 24px",
                backgroundColor: "var(--color-grey-0)",
                color: "var(--color-grey-700)",
                textAlign: "center",
              },
            }}
          />
        </AuthProvider>
      </QueryClientProvider>
    </>
  );
}

export default App;
