import styled, { css } from "styled-components";

const headingStyles = (fontSize, fontWeight) => {
  return `
    font-size: ${fontSize}rem;
    font-weight: ${fontWeight};
  `;
};

const Heading = styled.h1`
  ${(props) => props.as === "h1" && headingStyles(3.2, 600)}
  ${(props) => props.as === "h2" && headingStyles(2, 600)}
  ${(props) => props.as === "h3" && headingStyles(2, 500)}
  ${(props) =>
    props.as === "h4" && headingStyles(3, 600) + "text-align: center"}

${(props) => props.as === "h5" && headingStyles(2, 700) + "text-align: left"}


  ${(props) => props.spacing && `letter-spacing: ${props.spacing}px`}
`;

export default Heading;
