package org.seols.ohiolegalservicesassistant;

/**
 * Created by joshuagoodwin on 10/1/15.
 */
public class FederalPovertyLevel {

        private static final int[] fpl2015 = {11770, 4160};
        private static final int[] fpl2014 = {11670, 4060};

        double annualIncome, results;

        int size;

        String year;

        private int getValues(String year, int pos) {

            switch (year) {
                case "2015":
                    return fpl2015[pos];
                case "2014":
                    return fpl2014[pos];
                default:
                    return fpl2015[pos];
            }
        }

    /**
     * Calculates the federal poverty level and returns the result.
     * @return Federal poverty level
     */
        public double getResults() {

            int povertyStart = getValues(year, 0);
            int povertyIncrement = getValues(year, 1);

            double fpl = ((size - 1) * povertyIncrement) + povertyStart;

            results = Math.floor(((annualIncome / fpl) * 100) * 100) / 100;

            return results;

        }

    /**
     * Sets the assistance group size for calculating the poverty level
     * @param size Assistance Group Size
     */
        public void setSize(int size) {
            this.size = size;
        }

    /**
     * Sets the year for calucating poverty level. This allows for historic calculations.
     * @param year
     */
        public void setYear(String year) {
            this.year = year;
        }

    /**
     * Sets the annual income for calculating the poverty level.
     * @param income
     */
        public void setAnnualIncome(double income) {
            annualIncome = income;
        }

}
