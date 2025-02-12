import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL;
const getJWT = () => JSON.parse(localStorage.getItem("jwt"));

export async function getCheckoutSession() {
  const API_ENDPOINT = `${API_URL}/payments/create-checkout-session`;
  const response = await axios.post(
    API_ENDPOINT,
    {},
    {
      headers: { Authorization: `Bearer ${getJWT()}` },
    }
  );
  console.log("I RUNNN");
  console.log(response.data.checkoutUrl);
  return response.data.checkoutUrl;
}
