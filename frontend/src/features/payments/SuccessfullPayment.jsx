import { useSearchParams } from "react-router-dom";
import PaymentResult from "./PaymentResult";
import styled from "styled-components";

const P = styled.p`
  width: 40rem;
`;

function SuccessfullPayment() {
  const [searchParams] = useSearchParams();
  const sessionId = searchParams.get("session_id");
  return (
    <PaymentResult title="Payment succefull! 🎉 ">
      <P>
        Your payment has been accepted and a premium account has been activated!
      </P>
      <P>Payment ID: {sessionId}</P>
    </PaymentResult>
  );
}

export default SuccessfullPayment;
