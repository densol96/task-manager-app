import { IoIosNotifications } from "react-icons/io";
import { MdNotificationsActive } from "react-icons/md";
import styled from "styled-components";
import { userHasUnreadNotifications } from "../services/apiNotifications";
import { useEffect, useState } from "react";
import { Modal } from "../../ui/Modal";
import { PulsateElement } from "../../ui/PulsateElement";
import Notifications from "./Notifications";

const IconWrapper = styled.div`
  font-size: 3rem;
  display: flex;
  align-items: center;
  color: var(--color-brand-700);
  cursor: pointer;
`;

function NotificationsAlarm() {
  const [hasUnread, setHasUnread] = useState(false);

  const checkNotifications = async () => {
    const result = await userHasUnreadNotifications();
    setHasUnread(result);
  };

  useEffect(() => {
    checkNotifications();
    const interval = setInterval(checkNotifications, 10000); // 10s

    return () => clearInterval(interval);
  }, []);

  return (
    <Modal
      triggerElement={
        <IconWrapper>
          {hasUnread ? (
            <PulsateElement>
              <MdNotificationsActive />
            </PulsateElement>
          ) : (
            <IoIosNotifications />
          )}
        </IconWrapper>
      }
    >
      <Notifications checkHasUnread={checkNotifications} />
    </Modal>
  );
}

export default NotificationsAlarm;
