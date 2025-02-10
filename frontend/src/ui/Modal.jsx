import React, {
  cloneElement,
  createContext,
  useContext,
  useRef,
  useState,
} from "react";
import { createPortal } from "react-dom";
import { IoMdCloseCircle } from "react-icons/io";
import styled from "styled-components";

const Overlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  width: 100vw;
  height: 100vh;
  background-color: #00000049;
  backdrop-filter: blur(12px);
`;

const Window = styled.div`
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: absolute;
  top: 50%;
  left: 50%;
  padding: 5rem;
  background-color: var(--color-grey-0);
  border-radius: 3px;
  border: 2px solid var(--color-table-border);
  max-height: 90vh;
  overflow-y: auto;
  max-width: 90vw;
  border-radius: 20px;

  opacity: 0;
  animation: fadeIn 700ms ease-in-out forwards;

  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translate(-50%, -40%);
    }
    to {
      opacity: 1;
      transform: translate(-50%, -50%);
    }
  }
`;

const CloseBtn = styled.button`
  position: absolute;
  top: 2rem;
  right: 2rem;
  border: none;
  background: none;
`;

const ModalContext = createContext(undefined);

export const useModalContext = () => {
  const context = useContext(ModalContext);
  if (context === undefined) {
    throw new Error("useModalContext used outside the Provider");
  }
  return context;
};

export const Modal = ({ triggerElement, children, style }) => {
  const [isOpen, setIsOpen] = useState(false);
  const overlay = useRef(null);

  function detectOutsideClick(e) {
    if (e.target.contains(overlay.current)) {
      setIsOpen(false);
    }
  }

  return (
    <ModalContext.Provider
      value={{
        isOpen,
        open: () => setIsOpen(true),
        close: () => setIsOpen(false),
      }}
    >
      {cloneElement(triggerElement, {
        onClick: () => setIsOpen(true),
      })}
      {isOpen &&
        createPortal(
          <Overlay ref={overlay} onClick={detectOutsideClick}>
            <Window style={style}>
              <CloseBtn style={style} onClick={() => setIsOpen(false)}>
                <IoMdCloseCircle size={25} />
              </CloseBtn>
              {children}
            </Window>
          </Overlay>,
          document.body
        )}
    </ModalContext.Provider>
  );
};
