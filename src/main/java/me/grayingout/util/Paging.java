package me.grayingout.util;

/**
 * Utility methods for paging arrays
 */
public final class Paging {

    /**
     * Bounds a page between the max page and min page
     * 
     * @param page         The page
     * @param pageSize     The page size
     * @param lengthOfList The number of items in the list
     * @return
     */
    public static final int boundPage(int page, int pageSize, int lengthOfList) {
        return Math.max(Math.min(Paging.getNumberOfPages(pageSize, lengthOfList), page), 1);
    }
    
    /**
     * Calculates the number of pages possible in a list with
     * a specific page size
     * 
     * @param pageSize     The size of each page
     * @param lengthOfList The number of items in the list
     * @return The number of possible pages
     */
    public static final int getNumberOfPages(int pageSize, int lengthOfList) {
        /* Check if empty */
        if (lengthOfList == 0) {
            return 1;
        }

        /* Calculate page count */
        return (int) Math.ceil(lengthOfList/(pageSize * 1.0));
    }

    /**
     * Calculates the start index of a list page
     * 
     * @param page         The page number
     * @param pageSize     The size of a page
     * @param lengthOfList The number of items in the list
     * @return The start index
     */
    public static final int getPageStartIndex(int page, int pageSize, int lengthOfList) {
        /* Check if empty */
        if (lengthOfList == 0) {
            return 0;
        }

        /* Calculate index */
        int startIndex = (int) ((page - 1) * pageSize);

        /* Check if outside bounds */
        if (startIndex >= lengthOfList) {
            startIndex = lengthOfList - 1;
        }

        /* Return index */
        return startIndex;
    }

    /**
     * Calculates the end index of a list page
     * 
     * @param page         The page number
     * @param pageSize     The size of a page
     * @param lengthOfList The number of items in the list
     * @return The start index
     */
    public static final int getPageEndIndex(int page, int pageSize, int lengthOfList) {
        /* Get the start index */
        int startIndex = getPageStartIndex(page, pageSize, lengthOfList);

        /* Check if enough items after start index for page */
        if (lengthOfList >= startIndex + pageSize) {
            return (int) (startIndex + pageSize);
        }

        /* Not enough after start index for page */
        return lengthOfList;
    }
}
