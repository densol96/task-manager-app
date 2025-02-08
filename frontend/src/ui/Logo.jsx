import styled from "styled-components";

const StyledLogoWrapper = styled.div`
  position: relative;
  display: inline-block;
`;

const StyledLogo = styled.img`
  width: ${({ size }) => (size === "mini" ? "15rem" : "20rem")};
  border-radius: 50%;
  filter: blur(2px);
  display: block;
`;

const Logo = ({ size }) => <StyledLogo size={size} src="/lg.jpg" alt="Logo" />;

export default Logo;
