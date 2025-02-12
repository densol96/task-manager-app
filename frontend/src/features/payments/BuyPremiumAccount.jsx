import Button from "../../ui/Button";
import { Modal } from "../../ui/Modal";
import ConfirmForm from "../projects/ConfirmForm";
import { PulsateElement } from "../../ui/PulsateElement";
import { useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import toast from "react-hot-toast";
import { getCheckoutSession } from "../services/apiPayments";
import { useAuthContext } from "../../context/AuthContext";

function BuyPremiumAccount() {
  const queryClient = useQueryClient();
  const { premiumAccount } = useAuthContext();
  const [loading, setLoading] = useState(false);

  const handleCheckout = async () => {
    try {
      const promise = toast.promise(getCheckoutSession(), {
        loading: "You are being redirected to the payment page",
        error: "Payments are temporarily unavailable.",
      });
      const checkoutUrl = await promise;
      if (checkoutUrl) {
        window.location.href = checkoutUrl;
      }
    } catch (error) {
      console.log("PAYMENT WITH STRIPE DID NOT WORK!");
    } finally {
      setLoading(false);
    }
  };
  return premiumAccount.hasActivePremiumAccount ? (
    <p>Premium User ⭐</p>
  ) : (
    <Modal
      triggerElement={
        <PulsateElement>
          <Button size="small" variation="danger">
            BUY PREMIUM
          </Button>
        </PulsateElement>
      }
    >
      <ConfirmForm heading="Buy premium" width={30} action={handleCheckout}>
        A Premium account costs 10 EUR and lasts for 30 days. It increases the
        project ownership limit to 10, and the projects remain intact even after
        your Premium account expires.
      </ConfirmForm>
    </Modal>
  );
}

export default BuyPremiumAccount;
