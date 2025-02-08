import { Navigate } from "react-router-dom";
import usePublicProjects from "../features/projects/usePublicProjects";
import Heading from "../ui/Heading";
import InProgress from "../ui/InProgress";

function Dashboard() {
  if (true) return <Navigate to="/projects-all" />;
  return (
    <>
      <Heading spacing={2} as="h1">
        Dashboard
      </Heading>
      {/* <InProgress /> */}
    </>
  );
}

export default Dashboard;
