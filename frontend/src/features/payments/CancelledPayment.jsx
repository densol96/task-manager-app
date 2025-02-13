import PaymentResult from "./PaymentResult";

function CancelledPayment() {
  return (
    <PaymentResult title="Payment сanceled! 😔 ">
      <p style={{ width: "40rem" }}>
        Your payment has been canceled and you have not been charged
      </p>
    </PaymentResult>
  );
}

export default CancelledPayment;
