import styled from "styled-components";

const StyledFooter = styled.div`
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 5rem;
`;

const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <StyledFooter>
      <p>This project is a final Accenture Bootcamp project {currentYear}.</p>
      <p>
        Created by <br />
        Deniss Solovjovs, <br />
        Nazar Bondarenko, <br />
        Nina Yushchenko, <br />
        Olena Boiko, <br />
        Oleksand Pohorielov
      </p>
    </StyledFooter>
  );
};

export default Footer;
