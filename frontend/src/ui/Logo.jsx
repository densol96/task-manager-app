import styled from "styled-components";

const StyledLogoWrapper = styled.div`
  position: relative;
  display: inline-block;
`;

const StyledLogo = styled.img`
  width: 20rem;
  border-radius: 50%;
  filter: blur(2px);
  display: block;
`;

const Logo = () => (
  <StyledLogoWrapper>
    <StyledLogo src="/lg.jpg" alt="Logo" />
  </StyledLogoWrapper>
);

export default Logo;
