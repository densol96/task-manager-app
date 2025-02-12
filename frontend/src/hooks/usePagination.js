import { useCallback, useState } from "react";
import { useSearchParams } from "react-router-dom";

function usePagination(pagesTotal) {
  const [searchParams, setSearchParams] = useSearchParams();
  const currentPage = searchParams.get("page")
    ? Number(searchParams.get("page"))
    : 1;
  const [isLimitError, setIsLimitError] = useState(false);

  const next = useCallback(() => {
    if (currentPage === pagesTotal) {
      setIsLimitError(true);
    } else {
      searchParams.set("page", `${currentPage + 1}`);
      setSearchParams(searchParams);
    }
  }, [currentPage, pagesTotal, searchParams, setSearchParams]);

  const prev = useCallback(() => {
    if (currentPage === 1) {
      setIsLimitError(true);
    } else {
      searchParams.set("page", `${currentPage - 1}`);
      setSearchParams(searchParams);
    }
  }, [currentPage, searchParams, setSearchParams]);

  return { currentPage, prev, next, isLimitError };
}

export default usePagination;
