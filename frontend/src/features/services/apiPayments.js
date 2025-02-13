import axios from "axios";
import { getJWT } from "../../helpers/functions";

const API_URL = process.env.REACT_APP_API_URL;

export async function getCheckoutSession() {
  const API_ENDPOINT = `${API_URL}/payments/create-checkout-session`;
  const response = await axios.post(
    API_ENDPOINT,
    {},
    {
      headers: { Authorization: `Bearer ${getJWT()}` },
    }
  );
  return response.data.checkoutUrl;
}
