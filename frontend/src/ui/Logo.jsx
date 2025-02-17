import styled from "styled-components";

const StyledLogoWrapper = styled.div`
  position: relative;
  display: inline-block;
`;

const StyledLogo = styled.img`
  width: ${({ size }) => (size === "mini" ? "25rem" : "40rem")};
  border-radius: 50%;
  /* filter: blur(2px); */
  display: block;
`;

const Logo = ({ size = "mini" }) => (
  <StyledLogo size={size} src="/1.svg" alt="Logo" />
);

export default Logo;
