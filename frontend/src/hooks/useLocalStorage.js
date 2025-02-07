import { useEffect, useState } from "react";

function useLocalStorage(key, defaultValue) {
  const [state, setState] = useState(() => {
    try {
      const storedValue = localStorage.getItem(key);
      return storedValue ? JSON.parse(storedValue) : defaultValue || null;
    } catch (error) {
      console.log("Unable to parse value from localStorage", error);
      return defaultValue;
    }
  });

  const updateLocalStorage = (newValue) => {
    setState(newValue);
  };

  const deleteFromLocalStorage = () => {
    setState(null);
  };

  useEffect(() => {
    if (state !== null) {
      localStorage.setItem(key, JSON.stringify(state));
    } else {
      localStorage.removeItem(key);
    }
  }, [state]);

  return { state, updateLocalStorage, deleteFromLocalStorage };
}

export default useLocalStorage;
